/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer.C05PacketPlayerLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlockUtils
{
	public static void faceBlockClient(BlockPos blockPos)
	{
		double diffX =
			blockPos.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY =
			blockPos.getY() + 0.5 - (Minecraft.getMinecraft().thePlayer.posY
				+ Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ =
			blockPos.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		Minecraft.getMinecraft().thePlayer.rotationYaw =
			Minecraft.getMinecraft().thePlayer.rotationYaw
				+ MathHelper.wrapAngleTo180_float(
					yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
		Minecraft.getMinecraft().thePlayer.rotationPitch =
			Minecraft.getMinecraft().thePlayer.rotationPitch
				+ MathHelper.wrapAngleTo180_float(
					pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
	}
	
	public static void faceBlockPacket(BlockPos blockPos)
	{
		double diffX =
			blockPos.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY =
			blockPos.getY() + 0.5 - (Minecraft.getMinecraft().thePlayer.posY
				+ Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ =
			blockPos.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		Minecraft.getMinecraft().thePlayer.sendQueue
			.addToSendQueue(new C05PacketPlayerLook(
				Minecraft.getMinecraft().thePlayer.rotationYaw
					+ MathHelper.wrapAngleTo180_float(
						yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
				Minecraft.getMinecraft().thePlayer.rotationPitch
					+ MathHelper.wrapAngleTo180_float(pitch
						- Minecraft.getMinecraft().thePlayer.rotationPitch),
				Minecraft.getMinecraft().thePlayer.onGround));
	}
	
	public static void faceBlockClientHorizontally(BlockPos blockPos)
	{
		double diffX =
			blockPos.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffZ =
			blockPos.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		Minecraft.getMinecraft().thePlayer.rotationYaw =
			Minecraft.getMinecraft().thePlayer.rotationYaw
				+ MathHelper.wrapAngleTo180_float(
					yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
	}
	
	public static float getPlayerBlockDistance(BlockPos blockPos)
	{
		return getPlayerBlockDistance(blockPos.getX(), blockPos.getY(),
			blockPos.getZ());
	}
	
	public static float getPlayerBlockDistance(double posX, double posY,
		double posZ)
	{
		float xDiff = (float)(Minecraft.getMinecraft().thePlayer.posX - posX);
		float yDiff = (float)(Minecraft.getMinecraft().thePlayer.posY - posY);
		float zDiff = (float)(Minecraft.getMinecraft().thePlayer.posZ - posZ);
		return getBlockDistance(xDiff, yDiff, zDiff);
	}
	
	public static float getBlockDistance(float xDiff, float yDiff, float zDiff)
	{
		return MathHelper.sqrt_float(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (yDiff - 0.5F) * (yDiff - 0.5F)
				+ (zDiff - 0.5F) * (zDiff - 0.5F));
	}
	
	public static float getHorizontalPlayerBlockDistance(BlockPos blockPos)
	{
		float xDiff =
			(float)(Minecraft.getMinecraft().thePlayer.posX - blockPos.getX());
		float zDiff =
			(float)(Minecraft.getMinecraft().thePlayer.posZ - blockPos.getZ());
		return MathHelper.sqrt_float(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (zDiff - 0.5F) * (zDiff - 0.5F));
	}
}
