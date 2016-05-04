/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.java8check;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Base64;

import javax.swing.JOptionPane;

public class Java8Checker
{
	public static void checkJavaVersion()
	{
		try
		{
			// new Java 8 method
			Base64.getEncoder();
		}catch(Throwable e)
		{
			// fallback message
			String message = "Your Java is outdated!";
			
			// html message
			try
			{
				InputStream input =
					Java8Checker.class.getResourceAsStream("index.html");
				BufferedReader reader =
					new BufferedReader(new InputStreamReader(input));
				message = reader.readLine();
				for(String line; (line = reader.readLine()) != null;)
					message += line;
				reader.close();
				
				message =
					message.replace("§currentjava",
						System.getProperty("java.version"));
			}catch(IOException e1)
			{
				e1.printStackTrace();
			}
			
			// message dialog
			int action =
				JOptionPane.showOptionDialog(null, message, "Outdated Java",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
					null, new String[]{"Get Help", "I know what to do"}, 0);
			
			// learn more link
			if(action == 0)
				try
				{
					Desktop
						.getDesktop()
						.browse(
							new URI(
								"https://www.wurst-client.tk/redirect/outdated-java-help/"));
				}catch(Exception e1)
				{
					System.err.println("Failed to open link");
					e1.printStackTrace();
				}
			
			System.exit(0);
		}
	}
}
