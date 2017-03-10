/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.alts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.minecraft.client.Minecraft;
import tk.wurst_client.WurstClient;

public class Encryption
{
	public static final String CHARSET = "UTF-8";
	
	private static final File rsaKeyDir =
		System.getProperty("user.home") != null ? new File(
			System.getProperty("user.home"), ".ssh") : null;
	private static final File privateFile = rsaKeyDir != null ? new File(
		rsaKeyDir, "wurst_rsa") : null;
	private static final File publicFile = rsaKeyDir != null ? new File(
		rsaKeyDir, "wurst_rsa.pub") : null;
	private static final File aesFile = new File(
		WurstClient.INSTANCE.files.wurstDir, "key");
	
	private static KeyPair keypair;
	private static SecretKey aesKey;
	
	public static String encrypt(String string)
	{
		checkKeys();
		try
		{
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey,
				new IvParameterSpec(aesKey.getEncoded()));
			return Base64.getEncoder().encodeToString(
				cipher.doFinal(string.getBytes(CHARSET)));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String string)
	{
		checkKeys();
		try
		{
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, aesKey,
				new IvParameterSpec(aesKey.getEncoded()));
			return new String(
				cipher.doFinal(Base64.getDecoder().decode(string)), CHARSET);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static void checkKeys()
	{
		if(hasKeys())
			return;
		
		if(rsaKeyDir == null)
		{
			System.err
				.println("FATAL ERROR: RSA key directory does not exist!");
			Minecraft.getMinecraft().shutdown();
			return;
		}
		
		if(hasRsaKeyFiles() && loadRsaKeys())
			if(hasAesKeyFile() && loadAesKey())
				return;
			else
				regenerateAesKey();
		else
		{
			regenerateRsaKeys();
			regenerateAesKey();
		}
	}
	
	private static boolean hasRsaKeyFiles()
	{
		return privateFile.exists() && publicFile.exists();
	}
	
	private static boolean hasAesKeyFile()
	{
		return aesFile.exists();
	}
	
	private static boolean hasKeys()
	{
		return keypair != null && keypair.getPrivate().getEncoded() != null
			&& keypair.getPublic().getEncoded() != null && aesKey != null
			&& aesKey.getEncoded() != null;
	}
	
	private static void regenerateRsaKeys()
	{
		System.out.println("WARNING: Regenerating RSA keys!");
		try
		{
			KeyPairGenerator keypairgen = KeyPairGenerator.getInstance("RSA");
			keypairgen.initialize(1024);
			keypair = keypairgen.generateKeyPair();
			
			if(!publicFile.getParentFile().exists())
				publicFile.getParentFile().mkdirs();
			ObjectOutputStream savePublic =
				new ObjectOutputStream(new FileOutputStream(publicFile));
			savePublic.writeObject(KeyFactory.getInstance("RSA")
				.getKeySpec(keypair.getPublic(), RSAPublicKeySpec.class)
				.getModulus());
			savePublic.writeObject(KeyFactory.getInstance("RSA")
				.getKeySpec(keypair.getPublic(), RSAPublicKeySpec.class)
				.getPublicExponent());
			savePublic.close();
			
			if(!privateFile.getParentFile().exists())
				privateFile.getParentFile().mkdirs();
			ObjectOutputStream savePrivate =
				new ObjectOutputStream(new FileOutputStream(privateFile));
			savePrivate.writeObject(KeyFactory.getInstance("RSA")
				.getKeySpec(keypair.getPrivate(), RSAPrivateKeySpec.class)
				.getModulus());
			savePrivate.writeObject(KeyFactory.getInstance("RSA")
				.getKeySpec(keypair.getPrivate(), RSAPrivateKeySpec.class)
				.getPrivateExponent());
			savePrivate.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void regenerateAesKey()
	{
		System.out.println("WARNING: Regenerating AES key!");
		try
		{
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			aesKey = keygen.generateKey();
			
			if(!aesFile.getParentFile().exists())
				aesFile.getParentFile().mkdirs();
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.ENCRYPT_MODE, keypair.getPublic());
			Files.write(aesFile.toPath(),
				rsaCipher.doFinal(aesKey.getEncoded()));
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean loadRsaKeys()
	{
		try
		{
			ObjectInputStream publicLoad =
				new ObjectInputStream(new FileInputStream(publicFile));
			PublicKey loadedPublicKey =
				KeyFactory.getInstance("RSA").generatePublic(
					new RSAPublicKeySpec((BigInteger)publicLoad.readObject(),
						(BigInteger)publicLoad.readObject()));
			publicLoad.close();
			
			ObjectInputStream privateLoad =
				new ObjectInputStream(new FileInputStream(privateFile));
			PrivateKey loadedPrivateKey =
				KeyFactory.getInstance("RSA").generatePrivate(
					new RSAPrivateKeySpec((BigInteger)privateLoad.readObject(),
						(BigInteger)privateLoad.readObject()));
			privateLoad.close();
			
			keypair = new KeyPair(loadedPublicKey, loadedPrivateKey);
			return true;
			
		}catch(Exception e)
		{
			System.err.println("Failed to load RSA keys!");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean loadAesKey()
	{
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.DECRYPT_MODE, keypair.getPrivate());
			aesKey =
				new SecretKeySpec(rsaCipher.doFinal(Files.readAllBytes(aesFile
					.toPath())), "AES");
			return true;
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
