/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.options.xray;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.WurstClient;
import tk.wurst_client.mods.XRayMod;

public class GuiXRayBlocksManager extends GuiScreen
{
	private GuiScreen prevMenu;
	public static GuiXRayBlocksList blockList;
	
	public GuiXRayBlocksManager(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		blockList = new GuiXRayBlocksList(mc, this);
		blockList.registerScrollButtons(7, 8);
		GuiXRayBlocksList.sortBlocks();
		blockList.elementClicked(-1, false, 0, 0);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height - 52, 98, 20,
			"Add"));
		buttonList.add(new GuiButton(1, width / 2 + 2, height - 52, 98, 20,
			"Remove"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height - 28, 200, 20,
			"Back"));
		WurstClient.INSTANCE.analytics.trackPageView("/options/xray-manager",
			"X-Ray Block Manager");
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		((GuiButton)buttonList.get(1)).enabled =
			blockList.getSelectedSlot() != -1 && !XRayMod.xrayBlocks.isEmpty();
	}
	
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(new GuiXRayBlocksAdd(this));
			else if(clickedButton.id == 1)
			{// Remove
				WurstClient.INSTANCE.analytics.trackEvent("x-ray blocks",
					"remove", Integer.toString(Block
						.getIdFromBlock(XRayMod.xrayBlocks.get(blockList
							.getSelectedSlot()))));
				XRayMod.xrayBlocks.remove(blockList.getSelectedSlot());
				GuiXRayBlocksList.sortBlocks();
				WurstClient.INSTANCE.files.saveXRayBlocks();
			}else if(clickedButton.id == 2)
				mc.displayGuiScreen(prevMenu);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed((GuiButton)buttonList.get(0));
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
		throws IOException
	{
		if(par2 >= 36 && par2 <= height - 57)
			if(par1 >= width / 2 + 140 || par1 <= width / 2 - 126)
				blockList.elementClicked(-1, false, 0, 0);
		super.mouseClicked(par1, par2, par3);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		blockList.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, "X-Ray Block Manager", width / 2,
			8, 16777215);
		int totalBlocks = 0;
		for(int i = 0; i < GuiXRayBlocksList.blocks.size(); i++)
			if(XRayMod.xrayBlocks.contains(GuiXRayBlocksList.blocks.get(i)))
				totalBlocks++;
		drawCenteredString(fontRendererObj, "Blocks: " + totalBlocks,
			width / 2, 20, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
