/*
 * Copyright © 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.updater;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import tk.wurst_client.updater.gui.ProgressDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main
{
	public static File currentDirectory;
	public static File wurstJar;
	public static File newWurstJar;
	public static File wurstJSON;
	public static File newWurstJSON;
	public static File tmp;
	private static ProgressDialog progress;
	
	public static void main(final String[] args)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					if(args != null && args.length == 3
						&& args[0].equals("update"))
					{
						currentDirectory =
							new File(args[2].replace("%20", " "));
						wurstJar =
							new File(currentDirectory,
								"1.9.X with Wurst Client.jar");
						newWurstJar =
							new File(currentDirectory, "Wurst-update.jar");
						wurstJSON =
							new File(currentDirectory,
								"1.9.X with Wurst Client.json");
						newWurstJSON =
							new File(currentDirectory, "Wurst-update.json");
						tmp = new File(currentDirectory, "Wurst-update.tmp");
						progress = new ProgressDialog();
						Thread thread = new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								// Thread needed because setVisible() freezes
								progress.setVisible(true);
							}
						});
						thread.start();
						download(args[1]);
						extract();
						install();
						System.exit(0);
					}else
						System.err.println("Syntax error.\n" + "Syntax:\n"
							+ "    update <release_id> <path>");
				}catch(Exception e)
				{
					e.printStackTrace();
					StringWriter stacktraceWriter = new StringWriter();
					e.printStackTrace(new PrintWriter(stacktraceWriter));
					String trace = stacktraceWriter.toString();
					try
					{
						switch(JOptionPane.showOptionDialog(null,
							"An error occurred while updating Wurst.", "Error",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE, null, new String[]{
								"Report on GitHub", "Report via e-mail",
								"Copy stack trace", "Do nothing"}, 0))
						{
							case 0:
								Desktop
									.getDesktop()
									.browse(
										new URI(
											"https://github.com/Wurst-Imperium/Wurst-Client-for-MC-1.9.X/issues/new?title="
												+ URLEncoder.encode(
													"Wurst updater - Error report: "
														+ e.getMessage(),
													"UTF-8")
												+ "&body="
												+ URLEncoder
													.encode(
														"# Description\n"
															+ "Auto-generated error report.\n\n"
															+ "# Stacktrace\n"
															+ "```\n" + trace
															+ "```", "UTF-8")));
								break;
							case 1:
								Desktop
									.getDesktop()
									.browse(
										new URI(
											"mailto:contact.wurstimperium@gmail.com?subject="
												+ URLEncoder
													.encode(
														"Wurst updater - Error report",
														"UTF-8")
												+ "&body="
												+ URLEncoder
													.encode(
														"Description:\n"
															+ "Auto-generated error report.\n\n"
															+ "Stacktrace:\n"
															+ trace, "UTF-8")));
								break;
							case 2:
								Toolkit
									.getDefaultToolkit()
									.getSystemClipboard()
									.setContents(new StringSelection(trace),
										null);
								break;
							default:
								break;
						}
					}catch(Exception e1)
					{
						e1.printStackTrace();
					}
					try
					{
						if(JOptionPane.showConfirmDialog(null,
							"Would you like to update manually?", "",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
							Desktop
								.getDesktop()
								.browse(
									new URI(
										"https://github.com/Wurst-Imperium/Wurst-Client-for-MC-1.9.X/releases/latest"));
					}catch(Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	private static void download(String id) throws Exception
	{
		JsonArray json =
			new JsonParser()
				.parse(
					new InputStreamReader(
						new URL(
							"https://api.github.com/repos/Wurst-Imperium/Wurst-Client-for-MC-1.9.X/releases/"
								+ id + "/assets").openStream()))
				.getAsJsonArray();
		URL downloadUrl =
			new URL(json.get(0).getAsJsonObject().get("browser_download_url")
				.getAsString());
		long bytesTotal = downloadUrl.openConnection().getContentLengthLong();
		InputStream input = downloadUrl.openStream();
		FileOutputStream output = new FileOutputStream(tmp);
		byte[] buffer = new byte[8192];
		long bytesDownloaded = 0;
		for(int length; (length = input.read(buffer)) != -1;)
		{
			bytesDownloaded += length;
			if(bytesDownloaded > 0)
			{
				String percent =
					((float)(short)((float)bytesDownloaded / (float)bytesTotal * 1000F) / 10F)
						+ "%";
				String data =
					((float)(int)((float)bytesDownloaded * 1000F / 1048576F) / 1000F)
						+ " / "
						+ ((float)(int)((float)bytesTotal * 1000F / 1048576F) / 1000F)
						+ " MB";
				progress.updateProgress("Downloading Update: " + percent, data);
				System.out.println("Downloading Update: " + percent + " ("
					+ data + ")");
			}
			output.write(buffer, 0, length);
		}
		input.close();
		output.close();
	}
	
	private static void extract() throws Exception
	{
		progress.updateProgress("Extracting update...", "");
		System.out.println("Extracting update...");
		ZipInputStream input = new ZipInputStream(new FileInputStream(tmp));
		byte[] buffer = new byte[8192];
		for(ZipEntry entry; (entry = input.getNextEntry()) != null;)
		{
			if(entry.getName().equals("1.9.X with Wurst Client/"))
				continue;
			File file;
			if(entry.getName().equals(
				"1.9.X with Wurst Client/1.9.X with Wurst Client.jar"))
				file = newWurstJar;
			else if(entry.getName().equals(
				"1.9.X with Wurst Client/1.9.X with Wurst Client.json"))
				file = newWurstJSON;
			else
				file = new File(currentDirectory, entry.getName());
			FileOutputStream output = new FileOutputStream(file);
			for(int length; (length = input.read(buffer)) != -1;)
			{
				output.write(buffer, 0, length);
			}
			output.close();
		}
		input.close();
	}
	
	private static void install() throws Exception
	{
		while(!(wurstJar.delete() || !wurstJar.exists())
			|| !newWurstJar.renameTo(wurstJar)
			|| !(wurstJSON.delete() || !wurstJSON.exists())
			|| !newWurstJSON.renameTo(wurstJSON))
		{
			progress.updateProgress("Update ready",
				"Restart Minecraft to install it.");
			System.out
				.println("Update ready - Restart Minecraft to install it.");
			Thread.sleep(500);
		}
		System.out.println("Done.");
		progress.dispose();
	}
}
