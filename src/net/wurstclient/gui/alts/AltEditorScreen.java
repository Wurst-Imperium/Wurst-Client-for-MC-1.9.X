/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.alts;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.WurstClient;
import net.wurstclient.alts.Alt;
import net.wurstclient.alts.NameGenerator;
import net.wurstclient.alts.SkinStealer;
import net.wurstclient.utils.MiscUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class AltEditorScreen extends GuiScreen
{
	protected GuiScreen prevMenu;
	protected GuiTextField emailBox;
	protected GuiPasswordField passwordBox;
	protected String displayText = "";
	protected int errorTimer;
	protected Alt alt;
	protected long lastNameCheck;
	
	public AltEditorScreen(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		emailBox.updateCursorCounter();
		passwordBox.updateCursorCounter();
		((GuiButton)buttonList.get(0)).enabled =
			emailBox.getText().trim().length() > 0
				&& (!emailBox.getText().trim()
					.equalsIgnoreCase("Alexander01998") || passwordBox
					.getText().length() != 0);
		((GuiButton)buttonList.get(3)).enabled =
			!emailBox.getText().trim().equalsIgnoreCase("Alexander01998");
	}
	
	protected abstract String getDoneButtonText();
	
	protected abstract String getEmailBoxText();
	
	protected abstract String getPasswordBoxText();
	
	protected String getName()
	{
		return emailBox.getText();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 72 + 12,
			getDoneButtonText()));
		buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 96 + 12,
			"Random Name"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12,
			"Cancel"));
		buttonList.add(new GuiButton(4, width - (width / 2 - 100) / 2 - 64,
			height - 32, 128, 20, "Steal Skin"));
		buttonList.add(new GuiButton(5, (width / 2 - 100) / 2 - 64,
			height - 32, 128, 20, "Open Skin Folder"));
		emailBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		emailBox.setMaxStringLength(48);
		emailBox.setFocused(true);
		emailBox.setText(getEmailBoxText());
		passwordBox =
			new GuiPasswordField(fontRendererObj, width / 2 - 100, 100, 200, 20);
		passwordBox.setFocused(false);
		passwordBox.setText(getPasswordBoxText());
		WurstClient.INSTANCE.analytics.trackPageView(getUrl(), getTitle());
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat events."
	 */
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	protected abstract void onDoneButtonClick(GuiButton button);
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.enabled)
			if(button.id == 1)
				mc.displayGuiScreen(prevMenu);
			else if(button.id == 0)
				onDoneButtonClick(button);
			else if(button.id == 3)
				emailBox.setText(NameGenerator.generateName());
			else if(button.id == 4)
				displayText = SkinStealer.stealSkin(getName());
			else if(button.id == 5)
				MiscUtils.openFile(WurstClient.INSTANCE.files.skinDir);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		emailBox.textboxKeyTyped(par1, par2);
		passwordBox.textboxKeyTyped(par1, par2);
		
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
		super.mouseClicked(par1, par2, par3);
		emailBox.mouseClicked(par1, par2, par3);
		passwordBox.mouseClicked(par1, par2, par3);
		if(emailBox.isFocused() || passwordBox.isFocused())
			displayText = "";
	}
	
	protected abstract String getUrl();
	
	protected abstract String getTitle();
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		AltRenderer.drawAltBack(emailBox.getText(), (width / 2 - 100) / 2 - 64,
			height / 2 - 128, 128, 256);
		AltRenderer.drawAltBody(emailBox.getText(), width - (width / 2 - 100)
			/ 2 - 64, height / 2 - 128, 128, 256);
		drawCenteredString(fontRendererObj, getTitle(), width / 2, 20, 16777215);
		drawString(fontRendererObj, "Name or E-Mail", width / 2 - 100, 47,
			10526880);
		drawString(fontRendererObj, "Password", width / 2 - 100, 87, 10526880);
		drawCenteredString(fontRendererObj, displayText, width / 2, 142,
			16777215);
		emailBox.drawTextBox();
		passwordBox.drawTextBox();
		if(errorTimer > 0)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL_CULL_FACE);
			GL11.glEnable(GL_BLEND);
			GL11.glColor4f(1.0F, 0.0F, 0.0F, (float)errorTimer / 16);
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2d(0, 0);
				GL11.glVertex2d(width, 0);
				GL11.glVertex2d(width, height);
				GL11.glVertex2d(0, height);
			}
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL_CULL_FACE);
			GL11.glDisable(GL_BLEND);
			errorTimer--;
		}
		super.drawScreen(par1, par2, par3);
	}
}
