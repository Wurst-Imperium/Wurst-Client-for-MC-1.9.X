/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.font;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class UnicodeFontRenderer extends FontRenderer
{
	private final UnicodeFont font;
	
	@SuppressWarnings("unchecked")
	public UnicodeFontRenderer(Font awtFont)
	{
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation(
			"textures/font/ascii.png"), Minecraft.getMinecraft()
			.getTextureManager(), false);
		
		font = new UnicodeFont(awtFont);
		font.addAsciiGlyphs();
		font.getEffects().add(new ColorEffect(Color.WHITE));
		try
		{
			font.loadGlyphs();
		}catch(SlickException exception)
		{
			throw new RuntimeException(exception);
		}
		String alphabet =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
		FONT_HEIGHT = font.getHeight(alphabet) / 4;
	}
	
	@Override
	public int drawString(String string, int x, int y, int color)
	{
		if(string == null)
			return 0;
		glPushMatrix();
		glScaled(0.25, 0.25, 0.25);
		
		boolean blend = glIsEnabled(GL_BLEND);
		boolean lighting = glIsEnabled(GL_LIGHTING);
		boolean texture = glIsEnabled(GL_TEXTURE_2D);
		if(!blend)
			glEnable(GL_BLEND);
		if(lighting)
			glDisable(GL_LIGHTING);
		if(texture)
			glDisable(GL_TEXTURE_2D);
		x *= 4;
		y *= 4;
		
		font.drawString(x, y, string, new org.newdawn.slick.Color(color));
		
		if(texture)
			glEnable(GL_TEXTURE_2D);
		if(lighting)
			glEnable(GL_LIGHTING);
		if(!blend)
			glDisable(GL_BLEND);
		glPopMatrix();
		return x;
	}
	
	@Override
	public int drawStringWithShadow(String string, float x, float y, int color)
	{
		return drawString(string, (int)x, (int)y, color);
	}
	
	@Override
	public int getCharWidth(char c)
	{
		return getStringWidth(Character.toString(c));
	}
	
	@Override
	public int getStringWidth(String string)
	{
		return font.getWidth(string) / 4;
	}
	
	public int getStringHeight(String string)
	{
		return font.getHeight(string) / 4;
	}
}
