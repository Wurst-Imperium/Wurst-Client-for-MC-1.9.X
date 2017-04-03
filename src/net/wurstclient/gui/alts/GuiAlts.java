/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.alts;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.WurstClient;
import net.wurstclient.alts.Alt;
import net.wurstclient.alts.LoginManager;
import net.wurstclient.alts.NameGenerator;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.hooks.FrameHook;
import net.wurstclient.utils.MiscUtils;

public class GuiAlts extends GuiScreen
{
	private GuiScreen prevMenu;
	private boolean shouldAsk = true;
	private int errorTimer;
	public static GuiAltList altList;
	
	public GuiAlts(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		altList = new GuiAltList(mc, this);
		altList.registerScrollButtons(7, 8);
		altList.elementClicked(-1, false, 0, 0);
		if(GuiAltList.alts.isEmpty() && shouldAsk)
			mc.displayGuiScreen(new GuiYesNo(this, "Your alt list is empty.",
				"Would you like some random alts to get started?", 0));
		buttonList.clear();
		buttonList.add(
			new GuiButton(0, width / 2 - 154, height - 52, 100, 20, "Use"));
		buttonList.add(new GuiButton(1, width / 2 - 50, height - 52, 100, 20,
			"Direct Login"));
		buttonList
			.add(new GuiButton(2, width / 2 + 54, height - 52, 100, 20, "Add"));
		buttonList.add(
			new GuiButton(3, width / 2 - 154, height - 28, 75, 20, "Star"));
		buttonList
			.add(new GuiButton(4, width / 2 - 76, height - 28, 74, 20, "Edit"));
		buttonList.add(
			new GuiButton(5, width / 2 + 2, height - 28, 74, 20, "Delete"));
		buttonList.add(
			new GuiButton(6, width / 2 + 80, height - 28, 75, 20, "Cancel"));
		buttonList
			.add(new GuiButton(8, width - 108, 8, 100, 20, "Session Stealer"));
		
		buttonList.add(new GuiButton(7, 8, 8, 100, 20, "Import Alts"));
		WurstClient.INSTANCE.analytics.trackPageView("/alt-manager/",
			"Alt Manager");
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		buttonList.get(0).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(3).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(4).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
		buttonList.get(5).enabled =
			!GuiAltList.alts.isEmpty() && altList.getSelectedSlot() != -1;
	}
	
	@Override
	public void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
			{// Use
				Alt alt = GuiAltList.alts.get(altList.getSelectedSlot());
				if(alt.isCracked())
				{// Cracked
					LoginManager.changeCrackedName(alt.getEmail());
					mc.displayGuiScreen(prevMenu);
				}else
				{// Premium
					String reply =
						LoginManager.login(alt.getEmail(), alt.getPassword());
					if(reply.equals(""))
					{
						mc.displayGuiScreen(prevMenu);
						alt.setChecked(mc.session.getUsername());
						WurstClient.INSTANCE.files.saveAlts();
					}else
					{
						errorTimer = 8;
						if(reply.equals("§4§lWrong password!"))
						{
							GuiAltList.alts.remove(altList.getSelectedSlot());
							GuiAltList.sortAlts();
							WurstClient.INSTANCE.files.saveAlts();
						}
					}
				}
			}else if(clickedButton.id == 1)
				mc.displayGuiScreen(new GuiAltLogin(this));
			else if(clickedButton.id == 2)
				mc.displayGuiScreen(new GuiAltAdd(this));
			else if(clickedButton.id == 3)
			{
				Alt alt = GuiAltList.alts.get(altList.getSelectedSlot());
				alt.setStarred(!alt.isStarred());
				GuiAltList.sortAlts();
				WurstClient.INSTANCE.files.saveAlts();
			}else if(clickedButton.id == 4)
			{
				Alt alt = GuiAltList.alts.get(altList.getSelectedSlot());
				mc.displayGuiScreen(new GuiAltEdit(this, alt));
			}else if(clickedButton.id == 5)
			{// Delete
				Alt alt = GuiAltList.alts.get(altList.getSelectedSlot());
				String deleteQuestion =
					"Are you sure you want to remove this alt?";
				String deleteWarning = "\"" + alt.getNameOrEmail()
					+ "\" will be lost forever! (A long time!)";
				mc.displayGuiScreen(new GuiYesNo(this, deleteQuestion,
					deleteWarning, "Delete", "Cancel", 1));
			}else if(clickedButton.id == 6)
				mc.displayGuiScreen(prevMenu);
			else if(clickedButton.id == 7)
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						JFileChooser fileChooser =
							new JFileChooser(WurstFolders.MAIN.toFile())
							{
								@Override
								protected JDialog createDialog(Component parent)
									throws HeadlessException
								{
									JDialog dialog = super.createDialog(parent);
									dialog.setAlwaysOnTop(true);
									return dialog;
								}
							};
						fileChooser
							.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						fileChooser
							.addChoosableFileFilter(new FileNameExtensionFilter(
								"Username:Password format (TXT)", "txt"));
						int action =
							fileChooser.showOpenDialog(FrameHook.getFrame());
						if(action == JFileChooser.APPROVE_OPTION)
							try
							{
								File file = fileChooser.getSelectedFile();
								BufferedReader load =
									new BufferedReader(new FileReader(file));
								for(String line =
									""; (line = load.readLine()) != null;)
								{
									String[] data = line.split(":");
									if(data.length != 2)
										continue;
									GuiAltList.alts
										.add(new Alt(data[0], data[1], null));
								}
								load.close();
								GuiAltList.sortAlts();
								WurstClient.INSTANCE.files.saveAlts();
							}catch(IOException e)
							{
								e.printStackTrace();
								MiscUtils.simpleError(e, fileChooser);
							}
					}
				}).start();
			else if(clickedButton.id == 8)
				mc.displayGuiScreen(new SessionStealerScreen(this));
	}
	
	@Override
	public void confirmClicked(boolean par1, int par2)
	{
		if(par2 == 0)
		{
			if(par1)
			{
				for(int i = 0; i < 8; i++)
					GuiAltList.alts
						.add(new Alt(NameGenerator.generateName(), null, null));
				GuiAltList.sortAlts();
				WurstClient.INSTANCE.files.saveAlts();
			}
			shouldAsk = false;
		}else if(par2 == 1)
			if(par1)
			{
				GuiAltList.alts.remove(altList.getSelectedSlot());
				GuiAltList.sortAlts();
				WurstClient.INSTANCE.files.saveAlts();
			}
		mc.displayGuiScreen(this);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed(buttonList.get(0));
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		if(par2 >= 36 && par2 <= height - 57)
			if(par1 >= width / 2 + 140 || par1 <= width / 2 - 126)
				altList.elementClicked(-1, false, 0, 0);
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		altList.handleMouseInput();
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		altList.drawScreen(par1, par2, par3);
		if(altList.getSelectedSlot() != -1
			&& altList.getSelectedSlot() < GuiAltList.alts.size())
		{
			Alt alt = GuiAltList.alts.get(altList.getSelectedSlot());
			AltRenderer.drawAltBack(alt.getNameOrEmail(),
				(width / 2 - 125) / 2 - 32, height / 2 - 64 - 9, 64, 128);
			AltRenderer.drawAltBody(alt.getNameOrEmail(),
				width - (width / 2 - 140) / 2 - 32, height / 2 - 64 - 9, 64,
				128);
		}
		drawCenteredString(fontRendererObj, "Alt Manager", width / 2, 4,
			16777215);
		drawCenteredString(fontRendererObj, "Alts: " + GuiAltList.alts.size(),
			width / 2, 14, 10526880);
		drawCenteredString(
			fontRendererObj, "premium: " + GuiAltList.premiumAlts
				+ ", cracked: " + GuiAltList.crackedAlts,
			width / 2, 24, 10526880);
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
