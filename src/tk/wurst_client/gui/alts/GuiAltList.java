/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

import tk.wurst_client.alts.Alt;

public class GuiAltList extends GuiSlot
{
	public GuiAltList(Minecraft mc, GuiAlts prevMenu)
	{
		super(mc, prevMenu.width, prevMenu.height, 36, prevMenu.height - 56, 30);
		
		this.mc = mc;
		this.prevMenu = prevMenu;
	}
	
	private int selectedSlot;
	private Minecraft mc;
	public static ArrayList<Alt> alts = new ArrayList<Alt>();
	public static int premiumAlts;
	public static int crackedAlts;
	private GuiAlts prevMenu;
	
	public static void sortAlts()
	{
		Collections.sort(alts, new Comparator<Alt>()
		{
			@Override
			public int compare(Alt o1, Alt o2)
			{
				if(o1 == null || o2 == null)
					return 0;
				return o1.getNameOrEmail().compareToIgnoreCase(
					o2.getNameOrEmail());
			}
		});
		ArrayList<Alt> newAlts = new ArrayList<Alt>();
		premiumAlts = 0;
		crackedAlts = 0;
		for(int i = 0; i < alts.size(); i++)
			if(alts.get(i).isStarred())
				newAlts.add(alts.get(i));
		for(int i = 0; i < alts.size(); i++)
			if(!alts.get(i).isCracked() && !alts.get(i).isStarred())
				newAlts.add(alts.get(i));
		for(int i = 0; i < alts.size(); i++)
			if(alts.get(i).isCracked() && !alts.get(i).isStarred())
				newAlts.add(alts.get(i));
		for(int i = 0; i < newAlts.size(); i++)
			for(int i2 = 0; i2 < newAlts.size(); i2++)
				if(i != i2
					&& newAlts.get(i).getEmail()
						.equals(newAlts.get(i2).getEmail())
					&& newAlts.get(i).isCracked() == newAlts.get(i2)
						.isCracked())
					newAlts.remove(i2);
		for(int i = 0; i < newAlts.size(); i++)
			if(newAlts.get(i).isCracked())
				crackedAlts++;
			else
				premiumAlts++;
		alts = newAlts;
	}
	
	@Override
	protected boolean isSelected(int id)
	{
		return selectedSlot == id;
	}
	
	protected int getSelectedSlot()
	{
		if(selectedSlot > alts.size())
			selectedSlot = alts.size();
		return selectedSlot;
	}
	
	@Override
	protected int getSize()
	{
		return alts.size();
	}
	
	@Override
	protected void elementClicked(int var1, boolean doubleClick, int var3,
		int var4)
	{
		selectedSlot = var1;
		if(doubleClick)
			prevMenu.actionPerformed(new GuiButton(0, 0, 0, null));
	}
	
	@Override
	protected void drawBackground()
	{}
	
	@Override
	protected void drawSlot(int id, int x, int y, int var4, int var5, int var6)
	{
		Alt alt = alts.get(id);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL_CULL_FACE);
		GL11.glEnable(GL_BLEND);
		if(Minecraft.getMinecraft().getSession().getUsername()
			.equals(alt.getName()))
		{
			float opacity =
				0.3F - MathHelper.abs(MathHelper.sin(Minecraft.getSystemTime()
					% 10000L / 10000.0F * (float)Math.PI * 2.0F) * 0.15F);
			GL11.glColor4f(0.0F, 1.0F, 0.0F, opacity);
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2d(x - 2, y - 2);
				GL11.glVertex2d(x - 2 + 220, y - 2);
				GL11.glVertex2d(x - 2 + 220, y - 2 + 30);
				GL11.glVertex2d(x - 2, y - 2 + 30);
			}
			GL11.glEnd();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL_CULL_FACE);
		GL11.glDisable(GL_BLEND);
		AltRenderer.drawAltFace(alt.getNameOrEmail(), x + 1, y + 1, 24, 24,
			GuiAlts.altList.isSelected(GuiAltList.alts.indexOf(alt)));
		mc.fontRendererObj.drawString("Name: " + alt.getNameOrEmail(), x + 31,
			y + 3, 10526880);
		
		String tags = alt.isCracked() ? "§8cracked" : "§2premium";
		if(alt.isStarred())
			tags += "§r, §estarred";
		if(alt.isUnchecked())
			tags += "§r, §cunchecked";
		mc.fontRendererObj.drawString(tags, x + 31, y + 15, 10526880);
	}
}
