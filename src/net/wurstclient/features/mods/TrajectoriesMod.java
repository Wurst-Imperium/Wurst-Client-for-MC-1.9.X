/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Feature;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(name = "Trajectories",
	description = "Predicts the flight path of arrows and throwable items.",
	
	tags = "AimAssist, ArrowPrediction, aim assist, arrow prediction",
	help = "Mods/Trajectories")
@Mod.Bypasses
public final class TrajectoriesMod extends Mod implements RenderListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.bowAimbotMod, wurst.mods.fastBowMod,
			wurst.mods.throwMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		EntityPlayerSP player = WMinecraft.getPlayer();
		
		// check if player is holding item
		ItemStack stack = player.inventory.getCurrentItem();
		if(stack == null)
			return;
		
		// check if item is throwable
		Item item = stack.getItem();
		if(!(item instanceof ItemBow || item instanceof ItemSnowball
			|| item instanceof ItemEgg || item instanceof ItemEnderPearl
			|| item instanceof ItemSplashPotion
			|| item instanceof ItemLingeringPotion
			|| item instanceof ItemFishingRod))
			return;
		
		boolean usingBow =
			player.inventory.getCurrentItem().getItem() instanceof ItemBow;
		
		// calculate starting position
		double arrowPosX = player.lastTickPosX
			+ (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks
			- WMath.cos((float)Math.toRadians(player.rotationYaw)) * 0.16F;
		double arrowPosY = player.lastTickPosY
			+ (player.posY - player.lastTickPosY)
				* Minecraft.getMinecraft().timer.renderPartialTicks
			+ player.getEyeHeight() - 0.1;
		double arrowPosZ = player.lastTickPosZ
			+ (player.posZ - player.lastTickPosZ)
				* Minecraft.getMinecraft().timer.renderPartialTicks
			- WMath.sin((float)Math.toRadians(player.rotationYaw)) * 0.16F;
		
		// calculate starting motion
		float arrowMotionFactor = usingBow ? 1F : 0.4F;
		float yaw = (float)Math.toRadians(player.rotationYaw);
		float pitch = (float)Math.toRadians(player.rotationPitch);
		float arrowMotionX =
			-WMath.sin(yaw) * WMath.cos(pitch) * arrowMotionFactor;
		float arrowMotionY = -WMath.sin(pitch) * arrowMotionFactor;
		float arrowMotionZ =
			WMath.cos(yaw) * WMath.cos(pitch) * arrowMotionFactor;
		double arrowMotion = Math.sqrt(arrowMotionX * arrowMotionX
			+ arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ);
		arrowMotionX /= arrowMotion;
		arrowMotionY /= arrowMotion;
		arrowMotionZ /= arrowMotion;
		if(usingBow)
		{
			float bowPower = (72000 - player.getItemInUseCount()) / 20F;
			bowPower = (bowPower * bowPower + bowPower * 2F) / 3F;
			
			if(bowPower > 1F)
				bowPower = 1F;
			
			if(bowPower <= 0.1F)
				bowPower = 1F;
			
			bowPower *= 3F;
			arrowMotionX *= bowPower;
			arrowMotionY *= bowPower;
			arrowMotionZ *= bowPower;
		}else
		{
			arrowMotionX *= 1.5D;
			arrowMotionY *= 1.5D;
			arrowMotionZ *= 1.5D;
		}
		
		// GL settings
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glDepthMask(false);
		GL11.glLineWidth(1.8F);
		
		RenderManager renderManager = mc.getRenderManager();
		
		// draw trajectory line
		double gravity = usingBow ? 0.05D : item instanceof ItemPotion ? 0.4D
			: item instanceof ItemFishingRod ? 0.15D : 0.03D;
		Vec3d playerVector = new Vec3d(player.posX,
			player.posY + player.getEyeHeight(), player.posZ);
		GL11.glColor3d(0, 1, 0);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for(int i = 0; i < 1000; i++)
		{
			GL11.glVertex3d(arrowPosX - renderManager.renderPosX,
				arrowPosY - renderManager.renderPosY,
				arrowPosZ - renderManager.renderPosZ);
			
			arrowPosX += arrowMotionX * 0.1;
			arrowPosY += arrowMotionY * 0.1;
			arrowPosZ += arrowMotionZ * 0.1;
			arrowMotionX *= 0.999D;
			arrowMotionY *= 0.999D;
			arrowMotionZ *= 0.999D;
			arrowMotionY -= gravity * 0.1;
			
			if(WMinecraft.getWorld().rayTraceBlocks(playerVector,
				new Vec3d(arrowPosX, arrowPosY, arrowPosZ)) != null)
				break;
		}
		GL11.glEnd();
		
		// draw end of trajectory line
		double renderX = arrowPosX - renderManager.renderPosX;
		double renderY = arrowPosY - renderManager.renderPosY;
		double renderZ = arrowPosZ - renderManager.renderPosZ;
		AxisAlignedBB bb = new AxisAlignedBB(renderX - 0.5, renderY - 0.5,
			renderZ - 0.5, renderX + 0.5, renderY + 0.5, renderZ + 0.5);
		GL11.glColor4f(0F, 1F, 0F, 0.15F);
		RenderUtils.drawColorBox(bb, 0F, 1F, 0F, 0.15F);
		GL11.glColor4d(0, 0, 0, 0.5F);
		RenderUtils.drawSelectionBoundingBox(bb);
		
		// GL resets
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(GL13.GL_MULTISAMPLE);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPopMatrix();
	}
}
