/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.error;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.ResourceLocation;
import tk.wurst_client.WurstClient;
import tk.wurst_client.commands.Cmd;
import tk.wurst_client.hooks.FrameHook;
import tk.wurst_client.mods.Mod;
import tk.wurst_client.utils.JsonUtils;
import tk.wurst_client.utils.MiscUtils;

import com.google.gson.JsonObject;

public class GuiError extends GuiScreen
{
	private final ResourceLocation bugTexture = new ResourceLocation(
		"wurst/bug.png");
	private final Exception e;
	private final Object cause;
	private final String action;
	private final String comment;
	
	public GuiError(Exception e, Object cause, String action, String comment)
	{
		this.e = e;
		this.cause = cause;
		this.action = action;
		this.comment = comment;
	}
	
	@Override
	public void initGui()
	{
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 * 2, 200,
			20, "§a§l§nReport Bug"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 3 * 2 + 24,
			98, 20, "View Bug"));
		buttonList.add(new GuiButton(2, width / 2 + 2, height / 3 * 2 + 24, 98,
			20, "Back to Game"));
		WurstClient.INSTANCE.analytics.trackPageView("/error", "Error");
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		StringWriter stacktraceWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stacktraceWriter));
		String trace = stacktraceWriter.toString();
		final String report = generateReport(trace);
		System.err.println(report);
		switch(button.id)
		{
			case 0:
				if(WurstClient.INSTANCE.updater.isOutdated()
					|| WurstClient.INSTANCE.updater.getLatestVersion() == null)
				{
					backToGame();
					WurstClient.INSTANCE.chat
						.error("Error reports can only be sent from the latest version.");
					return;
				}
				try
				{
					JsonObject gist = new JsonObject();
					gist.addProperty("description", getReportDescription());
					gist.addProperty("public", true);
					JsonObject gistFiles = new JsonObject();
					JsonObject gistError = new JsonObject();
					gistError.addProperty("content", report);
					gistFiles.add("Wurst-Client-v" + WurstClient.VERSION
						+ "-Error-Report" + ".md", gistError);
					gist.add("files", gistFiles);
					JsonObject gistResponse =
						JsonUtils.jsonParser.parse(
							MiscUtils.post(new URL(
								"https://api.github.com/gists"), JsonUtils.gson
								.toJson(gist))).getAsJsonObject();
					MiscUtils.openLink(gistResponse.get("html_url")
						.getAsString());
					
					String reportUrl =
						MiscUtils
							.get(
								new URL(
									"https://www.wurst-client.tk/api/v1/submit-error-report.txt"))
							.trim();
					String reportResponse =
						MiscUtils.get(new URL(reportUrl + "?id="
							+ gistResponse.get("id").getAsString()
							+ "&version="
							+ WurstClient.INSTANCE.updater.getCurrentVersion()
							+ "&class=" + cause.getClass().getName()
							+ "&action=" + action));
					
					backToGame();
					WurstClient.INSTANCE.analytics
						.trackEvent("error", "report");
					WurstClient.INSTANCE.chat.message("Server response: "
						+ reportResponse);
				}catch(Exception e)
				{
					e.printStackTrace();
					WurstClient.INSTANCE.chat
						.error("Something went wrong with that error report.");
					WurstClient.INSTANCE.analytics.trackEvent("error",
						"report failed");
				}
				break;
			case 1:
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						switch(JOptionPane.showOptionDialog(
							FrameHook.getFrame(), report, "Stacktrace",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null,
							new String[]{"Close", "Copy to Clipboard",
								"Save to File"}, 0))
						{
							case 1:
								Toolkit
									.getDefaultToolkit()
									.getSystemClipboard()
									.setContents(new StringSelection(report),
										null);
								break;
							case 2:
								JFileChooser fileChooser = new JFileChooser()
								{
									@Override
									protected JDialog createDialog(
										Component parent)
										throws HeadlessException
									{
										JDialog dialog =
											super.createDialog(parent);
										dialog.setAlwaysOnTop(true);
										return dialog;
									}
								};
								fileChooser
									.setFileSelectionMode(JFileChooser.FILES_ONLY);
								fileChooser.setAcceptAllFileFilterUsed(false);
								fileChooser
									.addChoosableFileFilter(new FileNameExtensionFilter(
										"Markdown files", "md"));
								int action =
									fileChooser.showSaveDialog(FrameHook
										.getFrame());
								if(action == JFileChooser.APPROVE_OPTION)
									try
									{
										File file =
											fileChooser.getSelectedFile();
										if(!file.getName().endsWith(".md"))
											file =
												new File(file.getPath() + ".md");
										PrintWriter save =
											new PrintWriter(
												new FileWriter(file));
										save.println(report);
										save.close();
									}catch(IOException e)
									{
										e.printStackTrace();
										MiscUtils.simpleError(e, fileChooser);
									}
								break;
							default:
								break;
						}
					}
				}).start();
				WurstClient.INSTANCE.analytics.trackEvent("error", "view");
				break;
			case 2:
				mc.displayGuiScreen(new GuiYesNo(
					this,
					"Are you absolutely sure you don't want to report this error?",
					"We can't fix it that way!", "§4Yes", "§2No", 0));
				break;
			default:
				break;
		}
	}
	
	@Override
	public void confirmClicked(boolean result, int id)
	{
		super.confirmClicked(result, id);
		if(result)
		{
			backToGame();
			WurstClient.INSTANCE.analytics.trackEvent("error", "back");
		}else
			mc.displayGuiScreen(this);
		
	}
	
	private void backToGame()
	{
		if(cause instanceof Mod)
			((Mod)cause).setEnabled(false);
		mc.displayGuiScreen(null);
	}
	
	private String getReportDescription()
	{
		String title = "An error occurred ";
		if(cause instanceof Mod)
			title += "in `" + ((Mod)cause).getName() + "` ";
		else if(cause instanceof Cmd)
			title += "in `." + ((Cmd)cause).getCmdName() + "` ";
		title += "while " + action + ".";
		return title;
	}
	
	private String generateReport(String trace)
	{
		try
		{
			BufferedReader input =
				new BufferedReader(new InputStreamReader(getClass()
					.getClassLoader().getResourceAsStream(
						"assets/minecraft/wurst/error-report.md")));
			StringWriter writer = new StringWriter();
			PrintWriter output = new PrintWriter(writer);
			for(String line; (line = input.readLine()) != null;)
				output.println(line);
			String content = writer.toString();
			content =
				content.replace("§time", new SimpleDateFormat(
					"yyyy.MM.dd-hh:mm:ss").format(new Date()));
			content = content.replace("§trace", trace);
			content =
				content.replace("§os", System.getProperty("os.name") + " ("
					+ System.getProperty("os.arch") + ")");
			content =
				content.replace("§java", System.getProperty("java.version")
					+ " (" + System.getProperty("java.vendor") + ")");
			content =
				content
					.replace(
						"§wurst",
						WurstClient.INSTANCE.updater.getCurrentVersion()
							+ " (latest: "
							+ WurstClient.INSTANCE.updater.getLatestVersion()
							+ ")");
			content =
				content.replace("§desc",
					getReportDescription()
						+ (comment.isEmpty() ? "" : "\n" + comment));
			return content;
		}catch(Exception e)
		{
			e.printStackTrace();
			StringWriter stacktraceWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stacktraceWriter));
			String eString = stacktraceWriter.toString();
			return "Could not generate error report. Stack trace:\n```\n"
				+ eString + "\n```";
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(bugTexture);
		drawScaledCustomSizeModalRect(width / 2 - 48, height / 3, 0, 0, 256,
			256, 96, 96, 256, 256);
		drawCenteredString(fontRendererObj, "§nError!§r", width / 2,
			height / 4, 0xffffffff);
		drawCenteredString(fontRendererObj, getReportDescription(), width / 2,
			height / 4 + 16, 0xffffffff);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
