/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.alts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import tk.wurst_client.WurstClient;

public class SkinStealer
{
	public static String stealSkin(String name)
	{
		String reply = "";
		try
		{
			URL skinURL =
				new URL("http://skins.minecraft.net/MinecraftSkins/" + name
					+ ".png");
			URLConnection skinCon = skinURL.openConnection();
			BufferedInputStream skinputStream =
				new BufferedInputStream(skinCon.getInputStream());
			File skin =
				new File(WurstClient.INSTANCE.files.skinDir, name + ".png");
			FileOutputStream outputStream = new FileOutputStream(skin);
			int i;
			while((i = skinputStream.read()) != -1)
				outputStream.write(i);
			outputStream.close();
			skinputStream.close();
			reply = "§a§lSaved skin to wurst/skins/" + name + ".png.";
		}catch(UnknownHostException e)
		{
			reply = "§4§lCannot contact skin server!";
		}catch(Exception e)
		{
			reply = "§4§lUnable to steal skin.";
		}
		return reply;
	}
}
