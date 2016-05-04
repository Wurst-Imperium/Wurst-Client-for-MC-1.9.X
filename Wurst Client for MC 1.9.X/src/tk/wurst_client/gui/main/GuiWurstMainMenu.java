/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.main;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;

import javax.net.ssl.HttpsURLConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.util.xml.XMLElement;
import org.newdawn.slick.util.xml.XMLElementList;
import org.newdawn.slick.util.xml.XMLParser;

import tk.wurst_client.WurstClient;
import tk.wurst_client.gui.alts.GuiAlts;
import tk.wurst_client.utils.JsonUtils;
import tk.wurst_client.utils.MiscUtils;

import com.google.gson.JsonObject;

public class GuiWurstMainMenu extends GuiMainMenu
{
	private static final ResourceLocation title = new ResourceLocation(
		"wurst/wurst_380.png");
	private static final ResourceLocation santaHat = new ResourceLocation(
		"wurst/santa_hat.png");
	private XMLElementList news;
	private String newsTicker;
	private int newsWidth;
	
	private String noticeText = "";// =
									// "Wurst for Minecraft 1.9 is now available.";
	private String noticeLink = "https://www.wurst-client.tk/minecraft-1-9/";
	
	private int noticeWidth2;
	private int noticeWidth1;
	private int noticeX1;
	private int noticeY1;
	private int noticeX2;
	private int noticeY2;
	
	public GuiWurstMainMenu()
	{
		super();
		
		if(WurstClient.INSTANCE.options.wurstNews)
			downloadWurstNews();
		
		WurstClient.INSTANCE.analytics.trackPageView("/", "Main Menu");
	}
	
	private void downloadWurstNews()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpsURLConnection connection =
						(HttpsURLConnection)new URL(
							"https://www.wurst-client.tk/news/feed.xml")
							.openConnection();
					connection.connect();
					XMLElement xml =
						new XMLParser().parse("", connection.getInputStream());
					news =
						xml.getChildrenByName("channel").get(0)
							.getChildrenByName("item");
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		// adjust position of options, quit & language buttons
		for(int i = 3; i <= 5; i++)
			((GuiButton)buttonList.get(i)).yPosition =
				Math.min(((GuiButton)buttonList.get(i)).yPosition, height - 56);
		
		// notice
		this.noticeWidth1 =
			this.fontRendererObj.getStringWidth(this.noticeText);
		this.noticeWidth2 =
			this.fontRendererObj.getStringWidth(GuiMainMenu.field_96138_a);
		int noticeWidth = Math.max(this.noticeWidth1, this.noticeWidth2);
		this.noticeX1 = (this.width - noticeWidth) / 2;
		this.noticeY1 = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
		this.noticeX2 = this.noticeX1 + noticeWidth;
		this.noticeY2 = this.noticeY1 + 24;
		
		// news
		newsTicker = "";
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// wait for news to load
				while(news == null)
					try
					{
						Thread.sleep(50);
					}catch(InterruptedException e1)
					{
						e1.printStackTrace();
					}
				
				// build news ticker
				try
				{
					for(int i = 0; i < news.size(); i++)
						newsTicker +=
							news.get(i).getChildrenByName("title").get(0)
								.getContent()
								+ "§e+++§r";
				}catch(ConcurrentModificationException e)
				{	
					
				}
				newsWidth = fontRendererObj.getStringWidth(newsTicker);
				// divide by zero fix
				if(newsWidth % 50 == 0)
					newsWidth++;
				while(fontRendererObj.getStringWidth(newsTicker) < Math.max(
					width * 2, newsWidth * 2) && !newsTicker.isEmpty())
					newsTicker += newsTicker;
			}
		}).start();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		
		switch(button.id)
		{
			case 3:
				mc.displayGuiScreen(new GuiAlts(this));
				break;
		}
	}
	
	@Override
	public void confirmClicked(boolean result, int id)
	{
		super.confirmClicked(result, id);
		
		// changelog
		if(id == 64)
		{
			if(result)
				WurstClient.INSTANCE.analytics.trackEvent("changelog",
					"go play");
			else
			{
				MiscUtils.openLink("https://www.wurst-client.tk/changelog/");
				WurstClient.INSTANCE.analytics.trackEvent("changelog",
					"view changelog");
			}
			mc.displayGuiScreen(this);
		}
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		// updater
		if(WurstClient.INSTANCE.startupMessageDisabled)
			return;
		if(WurstClient.INSTANCE.updater.isOutdated())
		{
			WurstClient.INSTANCE.analytics.trackEvent("updater", "update to v"
				+ WurstClient.INSTANCE.updater.getLatestVersion(), "from "
				+ WurstClient.INSTANCE.updater.getCurrentVersion());
			WurstClient.INSTANCE.updater.update();
			WurstClient.INSTANCE.startupMessageDisabled = true;
		}
		
		// emergency message
		if(WurstClient.INSTANCE.startupMessageDisabled)
			return;
		try
		{
			HttpsURLConnection connection =
				(HttpsURLConnection)new URL(
					"https://www.wurst-client.tk/api/v1/messages.json")
					.openConnection();
			connection.connect();
			
			JsonObject json =
				JsonUtils.jsonParser
					.parse(
						new InputStreamReader(connection.getInputStream(),
							"UTF-8")).getAsJsonObject();
			
			if(json.get(WurstClient.VERSION) != null)
			{
				System.out.println("Emergency message found!");
				mc.displayGuiScreen(new GuiMessage(json
					.get(WurstClient.VERSION).getAsJsonObject()));
				WurstClient.INSTANCE.startupMessageDisabled = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// changelog
		if(WurstClient.INSTANCE.startupMessageDisabled)
			return;
		if(!WurstClient.VERSION
			.equals(WurstClient.INSTANCE.options.lastLaunchedVersion))
		{
			mc.displayGuiScreen(new GuiYesNo(this,
				"Successfully updated to Wurst v" + WurstClient.VERSION, "",
				"Go Play", "View Changelog", 64));
			WurstClient.INSTANCE.options.lastLaunchedVersion =
				WurstClient.VERSION;
			WurstClient.INSTANCE.files.saveOptions();
		}
		
		WurstClient.INSTANCE.startupMessageDisabled = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		// panorama
		GlStateManager.disableAlpha();
		renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		drawGradientRect(0, 0, width, height, -2130706433, 16777215);
		drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
		
		// title image
		mc.getTextureManager().bindTexture(title);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		int x = width / 2 - 256 / 2;
		int y = 36;
		int w = 256;
		int h = 64;
		float fw = 256;
		float fh = 64;
		float u = 0;
		float v = 0;
		if(GuiMainMenu.splashText.equals("umop-apisdn!"))
		{
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-width, (float)(-h - 60), 0.0F);
		}
		drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
		if(Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER)
		{
			mc.getTextureManager().bindTexture(santaHat);
			x = x + 112;
			y = y - 36;
			h = 48;
			w = 48;
			fw = 48;
			fh = 48;
			u = 0;
			v = 0;
			drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
		}
		if(GuiMainMenu.splashText.equals("umop-apisdn!"))
		{
			GL11.glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-width, (float)(-h - 60), 0.0F);
		}
		
		// splash text
		GlStateManager.pushMatrix();
		GlStateManager.translate(width / 2 + 90, 72.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
		float splashScale =
			1.8F - MathHelper.abs(MathHelper.sin(Minecraft.getSystemTime()
				% 1000L / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
		splashScale =
			splashScale * 100.0F
				/ (fontRendererObj.getStringWidth(splashText) + 32);
		GlStateManager.scale(splashScale, splashScale, splashScale);
		
		drawCenteredString(fontRendererObj, splashText, 0, 0, -256);
		GlStateManager.popMatrix();
		
		// text
		String vMinecraft = "Minecraft 1.9";
		String cMinecraft1 = "Copyright Mojang AB";
		String cMinecraft2 = "Do not distribute!";
		drawString(fontRendererObj, vMinecraft,
			width - fontRendererObj.getStringWidth(vMinecraft) - 8, 8, 0xffffff);
		drawString(fontRendererObj, cMinecraft1,
			width - fontRendererObj.getStringWidth(cMinecraft1) - 8, 18,
			0xffffff);
		drawString(fontRendererObj, cMinecraft2,
			width - fontRendererObj.getStringWidth(cMinecraft2) - 8, 28,
			0xffffff);
		drawString(fontRendererObj, "Wurst Client " + WurstClient.VERSION
			+ (WurstClient.INSTANCE.updater.isOutdated() ? " (outdated)" : ""),
			8, 8, 0xffffff);
		drawString(fontRendererObj, "Copyright Alexander01998", 8, 18, 0xffffff);
		drawString(fontRendererObj, "All rights reserved.", 8, 28, 0xffffff);
		drawCenteredString(fontRendererObj, "§nwww.Wurst-Client.tk", width / 2,
			height - 26, 0xffffff);
		
		// buttons
		for(Object button : buttonList)
			((GuiButton)button).drawButton(mc, mouseX, mouseY);
		
		// news
		if(!newsTicker.isEmpty() && newsWidth != 0)
			drawString(fontRendererObj, newsTicker,
				-(int)(Minecraft.getSystemTime() / 50 % newsWidth),
				height - 10, -1);
		
		// tooltips
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = (GuiButton)buttonList.get(i);
			if(button.isMouseOver())
			{
				ArrayList<String> tooltip = new ArrayList<>();
				switch(button.id)
				{
					case 20:
						tooltip.add("Wurst YouTube Channel");
						break;
					case 21:
						tooltip.add("Wurst Twitter Account");
						break;
					case 22:
						tooltip.add("Wurst Google+ Page");
						break;
					case 23:
						tooltip.add("Wurst Source Code");
						break;
					case 24:
						tooltip.add("Wurst Feedback");
						break;
					case 25:
						tooltip.add("Wurst Merchandise");
						break;
				}
				drawHoveringText(tooltip, mouseX, mouseY);
				break;
			}
		}
		
		// notice
		if(this.noticeText != null && this.noticeText.length() > 0)
		{
			drawRect(this.noticeX1 - 2, this.noticeY1 - 2, this.noticeX2 + 2,
				this.noticeY2 - 1, 1428160512);
			this.drawString(this.fontRendererObj, this.noticeText,
				this.noticeX1, this.noticeY1, -1);
			this.drawString(this.fontRendererObj, GuiMainMenu.field_96138_a,
				(this.width - this.noticeWidth2) / 2,
				((GuiButton)this.buttonList.get(0)).yPosition - 12, -1);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
		throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int linkWidth = fontRendererObj.getStringWidth("§nwww.Wurst-Client.tk");
		
		if(mouseButton == 0 && mouseY >= height - 26 && mouseY < height - 16
			&& mouseX > width / 2 - linkWidth / 2
			&& mouseX < width / 2 + linkWidth / 2)
		{
			MiscUtils.openLink("https://www.Wurst-Client.tk/");
			WurstClient.INSTANCE.analytics.trackPageView(
				"/wurst-client-dot-tk", "www.Wurst-Client.tk");
		}
		
		if(news != null && mouseButton == 0 && mouseY >= height - 10)
		{
			MiscUtils.openLink("https://www.wurst-client.tk/news");
			WurstClient.INSTANCE.analytics.trackPageView("/news", "Wurst News");
		}
		
		// notice
		if(this.noticeText.length() > 0 && mouseX >= this.noticeX1
			&& mouseX <= this.noticeX2 && mouseY >= this.noticeY1
			&& mouseY <= this.noticeY2)
			MiscUtils.openLink(noticeLink);
	}
}
