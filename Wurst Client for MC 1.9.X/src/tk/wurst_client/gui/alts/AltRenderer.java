/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;

public class AltRenderer
{
	public static void drawAltFace(String name, int x, int y, int w, int h,
		boolean selected)
	{
		try
		{
			AbstractClientPlayer.getDownloadImageSkin(
				AbstractClientPlayer.getLocationSkin(name), name).loadTexture(
				Minecraft.getMinecraft().getResourceManager());
			Minecraft.getMinecraft().getTextureManager()
				.bindTexture(AbstractClientPlayer.getLocationSkin(name));
			glEnable(GL_BLEND);
			if(selected)
				glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			else
				glColor4f(0.9F, 0.9F, 0.9F, 1.0F);
			// Face
			float fw = 192;
			float fh = 192;
			float u = 24;
			float v = 24;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Hat
			fw = 192;
			fh = 192;
			u = 120;
			v = 24;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			glDisable(GL_BLEND);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void drawAltBody(String name, int x, int y, int width,
		int height)
	{
		try
		{
			AbstractClientPlayer.getDownloadImageSkin(
				AbstractClientPlayer.getLocationSkin(name), name).loadTexture(
				Minecraft.getMinecraft().getResourceManager());
			Minecraft.getMinecraft().getTextureManager()
				.bindTexture(AbstractClientPlayer.getLocationSkin(name));
			boolean slim =
				DefaultPlayerSkin
					.getSkinType(EntityPlayer.getOfflineUUID(name)).equals(
						"slim");
			glEnable(GL_BLEND);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			// Face
			x = x + width / 4;
			y = y + 0;
			int w = width / 2;
			int h = height / 4;
			float fw = 32;
			float fh = 32;
			float u = 32;
			float v = 32;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Hat
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 4;
			fw = 32;
			fh = 32;
			u = 160;
			v = 32;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Chest
			x = x + 0;
			y = y + height / 4;
			w = width / 2;
			h = height / 8 * 3;
			fw = 32;
			fh = 48;
			u = 80;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Jacket
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 8 * 3;
			fw = 32;
			fh = 48;
			u = 80;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Arm
			x = x - width / 16 * (slim ? 3 : 4);
			y = y + (slim ? height / 32 : 0);
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = 176;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = 176;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Arm
			x = x + width / 16 * (slim ? 11 : 12);
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = 176;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = 176;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Leg
			x = x - width / 2;
			y = y + height / 32 * (slim ? 11 : 12);
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 16;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 16;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Leg
			x = x + width / 4;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 16;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 16;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			glDisable(GL_BLEND);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void drawAltBack(String name, int x, int y, int width,
		int height)
	{
		try
		{
			AbstractClientPlayer.getDownloadImageSkin(
				AbstractClientPlayer.getLocationSkin(name), name).loadTexture(
				Minecraft.getMinecraft().getResourceManager());
			Minecraft.getMinecraft().getTextureManager()
				.bindTexture(AbstractClientPlayer.getLocationSkin(name));
			boolean slim =
				DefaultPlayerSkin
					.getSkinType(EntityPlayer.getOfflineUUID(name)).equals(
						"slim");
			glEnable(GL_BLEND);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			// Face
			x = x + width / 4;
			y = y + 0;
			int w = width / 2;
			int h = height / 4;
			float fw = 32;
			float fh = 32;
			float u = 96;
			float v = 32;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Hat
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 4;
			fw = 32;
			fh = 32;
			u = 224;
			v = 32;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Chest
			x = x + 0;
			y = y + height / 4;
			w = width / 2;
			h = height / 8 * 3;
			fw = 32;
			fh = 48;
			u = 128;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Jacket
			x = x + 0;
			y = y + 0;
			w = width / 2;
			h = height / 8 * 3;
			fw = 32;
			fh = 48;
			u = 128;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Arm
			x = x - width / 16 * (slim ? 3 : 4);
			y = y + (slim ? height / 32 : 0);
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = slim ? 204 : 208;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = slim ? 204 : 208;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Arm
			x = x + width / 16 * (slim ? 11 : 12);
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = slim ? 204 : 208;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Sleeve
			x = x + 0;
			y = y + 0;
			w = width / 16 * (slim ? 3 : 4);
			h = height / 8 * 3;
			fw = slim ? 12 : 16;
			fh = 48;
			u = slim ? 204 : 208;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Leg
			x = x - width / 2;
			y = y + height / 32 * (slim ? 11 : 12);
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 48;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Left Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 48;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Leg
			x = x + width / 4;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 48;
			v = 80;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			// Right Pants
			x = x + 0;
			y = y + 0;
			w = width / 4;
			h = height / 8 * 3;
			fw = 16;
			fh = 48;
			u = 48;
			v = 144;
			Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
			glDisable(GL_BLEND);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
