/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.alts;

import java.net.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class LoginManager
{
	private static final Logger logger = LogManager.getLogger();
	
	public static String login(String email, String password)
	{
		YggdrasilAuthenticationService authenticationService =
			new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication authentication =
			(YggdrasilUserAuthentication)authenticationService
				.createUserAuthentication(Agent.MINECRAFT);
		authentication.setUsername(email);
		authentication.setPassword(password);
		String displayText;
		try
		{
			authentication.logIn();
			Minecraft.getMinecraft().session =
				new Session(authentication.getSelectedProfile().getName(),
					authentication.getSelectedProfile().getId().toString(),
					authentication.getAuthenticatedToken(), "mojang");
			displayText = "";
		}catch(AuthenticationUnavailableException e)
		{
			displayText = "§4§lCannot contact authentication server!";
		}catch(AuthenticationException e)
		{// wrong password account migrated
			if(e.getMessage().contains("Invalid username or password.")
				|| e.getMessage().toLowerCase().contains("account migrated"))
				displayText = "§4§lWrong password!";
			else
				displayText = "§4§lCannot contact authentication server!";
			logger.error(e.getMessage());
		}catch(NullPointerException e)
		{
			displayText = "§4§lWrong password!";
		}
		return displayText;
	}
	
	public static String check(String email, String password)
	{
		YggdrasilAuthenticationService authenticationService =
			new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication authentication =
			(YggdrasilUserAuthentication)authenticationService
				.createUserAuthentication(Agent.MINECRAFT);
		authentication.setUsername(email);
		authentication.setPassword(password);
		String displayText;
		try
		{
			authentication.logIn();
			displayText = "";
		}catch(AuthenticationUnavailableException e)
		{
			displayText = "§4§lCannot contact authentication server!";
		}catch(AuthenticationException e)
		{// wrong password account migrated
			if(e.getMessage().contains("Invalid username or password.")
				|| e.getMessage().toLowerCase().contains("account migrated"))
				displayText = "§4§lWrong password!";
			else
				displayText = "§4§lCannot contact authentication server!";
			logger.error(e.getMessage());
		}catch(NullPointerException e)
		{
			displayText = "§4§lWrong password!";
		}
		return displayText;
	}
	
	public static String getName(String email, String password)
	{
		YggdrasilAuthenticationService authenticationService =
			new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication authentication =
			(YggdrasilUserAuthentication)authenticationService
				.createUserAuthentication(Agent.MINECRAFT);
		authentication.setUsername(email);
		authentication.setPassword(password);
		try
		{
			authentication.logIn();
			return authentication.getSelectedProfile().getName();
		}catch(Exception e)
		{
			return null;
		}
	}
	
	public static void changeCrackedName(String newName)
	{
		Minecraft.getMinecraft().session =
			new Session(newName, "", "", "mojang");
	}
}
