/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.target;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.WurstClient;
import net.wurstclient.settings.ColorsSetting;

public class GuiTeamSettings extends GuiScreen
{
	private GuiScreen prevMenu;
	
	public GuiTeamSettings(GuiScreen prevMenu)
	{
		this.prevMenu = prevMenu;
		WurstClient.INSTANCE.analytics.trackPageView("/team-settings",
			"Team Settings");
	}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		
		// color buttons
		String[] colors = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f"};
		for(int i = 0; i < 16; i++)
		{
			int offsetX = -22;
			switch(i % 4)
			{
				case 3:
				offsetX = 26;
				break;
				case 2:
				offsetX = 2;
				break;
				case 0:
				offsetX = -46;
				break;
			}
			int offsetY = 72;
			switch(i % 16 / 4)
			{
				case 2:
				offsetY = 48;
				break;
				case 1:
				offsetY = 24;
				break;
				case 0:
				offsetY = 0;
				break;
			}
			buttonList.add(new TeamColorButton(i, width / 2 + offsetX,
				height / 3 + offsetY, "§" + colors[i] + colors[i]));
		}
		boolean[] team_colors =
			WurstClient.INSTANCE.special.targetSpf.teamColors.getSelected();
		for(int i = 0; i < 16; i++)
			((TeamColorButton)buttonList.get(i)).setFakeHover(team_colors[i]);
		
		// other buttons
		buttonList.add(new GuiButton(16, width / 2 - 46, height / 3 + 96, 44,
			20, "All On"));
		buttonList.add(new GuiButton(17, width / 2 + 2, height / 3 + 96, 44, 20,
			"All Off"));
		buttonList.add(new GuiButton(18, width / 2 - 100, height / 3 + 120, 200,
			20, "Done"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		if(button.id == 18)
		{
			Minecraft.getMinecraft().displayGuiScreen(prevMenu);
			WurstClient.INSTANCE.analytics.trackEvent("team settings", "done");
		}else
		{
			ColorsSetting teamColors =
				WurstClient.INSTANCE.special.targetSpf.teamColors;
			switch(button.id)
			{
				case 16:
				for(int i = 0; i < 16; i++)
				{
					teamColors.setSelected(i, true);
					((TeamColorButton)buttonList.get(i)).setFakeHover(true);
				}
				WurstClient.INSTANCE.analytics.trackEvent("team settings",
					"all on");
				break;
				case 17:
				for(int i = 0; i < 16; i++)
				{
					teamColors.setSelected(i, false);
					((TeamColorButton)buttonList.get(i)).setFakeHover(false);
				}
				WurstClient.INSTANCE.analytics.trackEvent("team settings",
					"all off");
				break;
				default:
				boolean onOff = !teamColors.getSelected()[button.id];
				teamColors.setSelected(button.id, onOff);
				((TeamColorButton)buttonList.get(button.id))
					.setFakeHover(onOff);
				WurstClient.INSTANCE.analytics.trackEvent("team settings",
					"toggle", onOff ? "on" : "off", button.id);
				break;
			}
			WurstClient.INSTANCE.files.saveOptions();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Team Settings", width / 2, 20,
			16777215);
		drawCenteredString(fontRendererObj,
			"Target all entities with the following", width / 2,
			height / 3 - 30, 10526880);
		drawCenteredString(fontRendererObj, "color(s) in their name:",
			width / 2, height / 3 - 20, 10526880);
		
		ArrayList<String> tooltip = new ArrayList<>();
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = buttonList.get(i);
			button.drawButton(mc, mouseX, mouseY);
			
			if(!button.isMouseOver())
				continue;
			switch(button.id)
			{
				case 0:
				tooltip.add("black");
				break;
				case 1:
				tooltip.add("dark blue");
				break;
				case 2:
				tooltip.add("dark green");
				break;
				case 3:
				tooltip.add("dark aqua");
				break;
				case 4:
				tooltip.add("dark red");
				break;
				case 5:
				tooltip.add("dark purple");
				break;
				case 6:
				tooltip.add("gold");
				break;
				case 7:
				tooltip.add("gray");
				break;
				case 8:
				tooltip.add("dark gray");
				break;
				case 9:
				tooltip.add("blue");
				break;
				case 10:
				tooltip.add("green");
				break;
				case 11:
				tooltip.add("aqua");
				break;
				case 12:
				tooltip.add("red");
				break;
				case 13:
				tooltip.add("light purple");
				break;
				case 14:
				tooltip.add("yellow");
				break;
				case 15:
				tooltip.add("white");
				break;
			}
		}
		drawHoveringText(tooltip, mouseX, mouseY);
	}
	
	public class TeamColorButton extends GuiButton
	{
		private boolean fakeHover;
		
		public TeamColorButton(int buttonId, int x, int y, String buttonText)
		{
			super(buttonId, x, y, 20, 20, buttonText);
		}
		
		public void setFakeHover(boolean fakeHover)
		{
			this.fakeHover = fakeHover;
		}
		
		@Override
		protected int getHoverState(boolean mouseOver)
		{
			return fakeHover ? super.getHoverState(mouseOver) : 0;
		}
	}
}
