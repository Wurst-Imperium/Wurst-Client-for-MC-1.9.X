/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.compatibility.WMinecraft;

public class BlockUtils
{
	public static void faceBlockClient(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - WMinecraft.getPlayer().posX;
		double diffY = blockPos.getY() + 0.5 - (WMinecraft.getPlayer().posY
			+ WMinecraft.getPlayer().getEyeHeight());
		double diffZ = blockPos.getZ() + 0.5 - WMinecraft.getPlayer().posZ;
		double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		WMinecraft.getPlayer().rotationYaw = WMinecraft.getPlayer().rotationYaw
			+ WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw);
		WMinecraft.getPlayer().rotationPitch =
			WMinecraft.getPlayer().rotationPitch + WMath
				.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch);
	}
	
	public static void faceBlockPacket(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - WMinecraft.getPlayer().posX;
		double diffY = blockPos.getY() + 0.5 - (WMinecraft.getPlayer().posY
			+ WMinecraft.getPlayer().getEyeHeight());
		double diffZ = blockPos.getZ() + 0.5 - WMinecraft.getPlayer().posZ;
		double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		WConnection.sendPacket(new Rotation(
			WMinecraft.getPlayer().rotationYaw
				+ WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw),
			WMinecraft.getPlayer().rotationPitch + WMath
				.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch),
			WMinecraft.getPlayer().onGround));
	}
	
	public static void faceBlockClientHorizontally(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - WMinecraft.getPlayer().posX;
		double diffZ = blockPos.getZ() + 0.5 - WMinecraft.getPlayer().posZ;
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		WMinecraft.getPlayer().rotationYaw = WMinecraft.getPlayer().rotationYaw
			+ WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw);
	}
	
	public static float getPlayerBlockDistance(BlockPos blockPos)
	{
		return getPlayerBlockDistance(blockPos.getX(), blockPos.getY(),
			blockPos.getZ());
	}
	
	public static float getPlayerBlockDistance(double posX, double posY,
		double posZ)
	{
		float xDiff = (float)(WMinecraft.getPlayer().posX - posX);
		float yDiff = (float)(WMinecraft.getPlayer().posY - posY);
		float zDiff = (float)(WMinecraft.getPlayer().posZ - posZ);
		return getBlockDistance(xDiff, yDiff, zDiff);
	}
	
	public static float getBlockDistance(float xDiff, float yDiff, float zDiff)
	{
		return (float)Math.sqrt(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (yDiff - 0.5F) * (yDiff - 0.5F)
				+ (zDiff - 0.5F) * (zDiff - 0.5F));
	}
	
	public static float getHorizontalPlayerBlockDistance(BlockPos blockPos)
	{
		float xDiff = (float)(WMinecraft.getPlayer().posX - blockPos.getX());
		float zDiff = (float)(WMinecraft.getPlayer().posZ - blockPos.getZ());
		return (float)Math.sqrt(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (zDiff - 0.5F) * (zDiff - 0.5F));
	}
}
