/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.options;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiPressAKey extends GuiScreen
{
	private GuiPressAKeyCallback prevMenu;
	
	public GuiPressAKey(GuiPressAKeyCallback prevMenu)
	{
		if(!(prevMenu instanceof GuiScreen))
			throw new IllegalArgumentException(
				"prevMenu is not an instance of GuiScreen");
		this.prevMenu = prevMenu;
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		prevMenu.setKey(Keyboard.getKeyName(par2));
		mc.displayGuiScreen((GuiScreen)prevMenu);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawBackground(0);
		drawCenteredString(fontRendererObj, "Press a key", width / 2,
			height / 4 + 48, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
