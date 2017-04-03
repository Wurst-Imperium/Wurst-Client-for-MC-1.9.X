/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.GUIRenderListener;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.font.Fonts;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.RenderUtils;

@Info(
	description = "Automatically aims your bow at the closest entity.\n"
		+ "Tip: This works with FastBow.",
	name = "BowAimbot",
	tags = "bow aimbot",
	help = "Mods/BowAimbot")
@Mod.Bypasses
public class BowAimbotMod extends Mod
	implements UpdateListener, RenderListener, GUIRenderListener
{
	private Entity target;
	private float velocity;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastBowMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(GUIRenderListener.class, this);
		wurst.events.add(RenderListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(target == null)
			return;
		RenderUtils.entityESPBox(target, 3);
	}
	
	@Override
	public void onRenderGUI()
	{
		if(target == null)
			return;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		
		String message;
		if(velocity < 1)
			message = "Charging: " + (int)(velocity * 100) + "%";
		else
			message = "Ready To Shoot";
		
		// translate to center
		ScaledResolution sr = new ScaledResolution(mc);
		int msgWidth = Fonts.segoe15.getStringWidth(message);
		GL11.glTranslated(sr.getScaledWidth() / 2 - msgWidth / 2,
			sr.getScaledHeight() / 2 + 1, 0);
		
		// background
		GL11.glColor4f(0, 0, 0, 0.5F);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(msgWidth + 3, 0);
			GL11.glVertex2d(msgWidth + 3, 10);
			GL11.glVertex2d(0, 10);
		}
		GL11.glEnd();
		
		// text
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Fonts.segoe15.drawString(message, 2, -1, 0xffffffff);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onUpdate()
	{
		target = null;
		if(WMinecraft.getPlayer().inventory.getCurrentItem() != null
			&& WMinecraft.getPlayer().inventory.getCurrentItem()
				.getItem() instanceof ItemBow
			&& mc.gameSettings.keyBindUseItem.pressed)
		{
			target = EntityUtils.getClosestEntity(true, 360, false);
			aimAtTarget();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(GUIRenderListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
	}
	
	private void aimAtTarget()
	{
		if(target == null)
			return;
		int bowCharge = WMinecraft.getPlayer().getItemInUseDuration();
		velocity = bowCharge / 20;
		velocity = (velocity * velocity + velocity * 2) / 3;
		if(wurst.mods.fastBowMod.isActive())
			velocity = 1;
		if(velocity < 0.1)
		{
			if(target instanceof EntityLivingBase)
				EntityUtils.faceEntityClient((EntityLivingBase)target);
			return;
		}
		if(velocity > 1)
			velocity = 1;
		double posX = target.posX + (target.posX - target.prevPosX) * 5
			- WMinecraft.getPlayer().posX;
		double posY = target.posY + (target.posY - target.prevPosY) * 5
			+ target.getEyeHeight() - 0.15 - WMinecraft.getPlayer().posY
			- WMinecraft.getPlayer().getEyeHeight();
		double posZ = target.posZ + (target.posZ - target.prevPosZ) * 5
			- WMinecraft.getPlayer().posZ;
		float yaw = (float)(Math.atan2(posZ, posX) * 180 / Math.PI) - 90;
		double y2 = Math.sqrt(posX * posX + posZ * posZ);
		float g = 0.006F;
		float tmp = (float)(velocity * velocity * velocity * velocity
			- g * (g * (y2 * y2) + 2 * posY * (velocity * velocity)));
		float pitch = (float)-Math.toDegrees(
			Math.atan((velocity * velocity - Math.sqrt(tmp)) / (g * y2)));
		WMinecraft.getPlayer().rotationYaw = yaw;
		WMinecraft.getPlayer().rotationPitch = pitch;
	}
}
