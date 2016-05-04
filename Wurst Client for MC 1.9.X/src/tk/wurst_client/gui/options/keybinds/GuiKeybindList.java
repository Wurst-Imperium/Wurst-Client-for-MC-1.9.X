/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.options.keybinds;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import tk.wurst_client.WurstClient;
import tk.wurst_client.mods.Mod;

public class GuiKeybindList extends GuiSlot
{
	public GuiKeybindList(Minecraft par1Minecraft, GuiScreen prevMenu)
	{
		super(par1Minecraft, prevMenu.width, prevMenu.height, 36,
			prevMenu.height - 56, 30);
		mc = par1Minecraft;
	}
	
	private int selectedSlot;
	private Minecraft mc;
	@Deprecated
	public static ArrayList<Mod> mods = new ArrayList<Mod>();
	
	@Override
	protected boolean isSelected(int id)
	{
		return selectedSlot == id;
	}
	
	protected int getSelectedSlot()
	{
		return selectedSlot;
	}
	
	@Override
	protected int getSize()
	{
		return WurstClient.INSTANCE.keybinds.size();
	}
	
	@Override
	protected void elementClicked(int var1, boolean var2, int var3, int var4)
	{
		selectedSlot = var1;
	}
	
	@Override
	protected void drawBackground()
	{}
	
	@Override
	protected void drawSlot(int id, int x, int y, int var4, int var5, int var6)
	{
		Entry entry =
			WurstClient.INSTANCE.keybinds.entrySet().toArray(
				new Map.Entry[WurstClient.INSTANCE.keybinds.size()])[id];
		mc.fontRendererObj.drawString("Key: " + entry.getKey(), x + 3, y + 3,
			10526880);
		mc.fontRendererObj.drawString("Command: " + entry.getValue(), x + 3,
			y + 15, 10526880);
	}
}
