/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.compatibility.WMinecraft;

public class BuildUtils
{
	public static void build(int[][] building)
	{
		float playerYaw = WMinecraft.getPlayer().rotationYaw;
		while(playerYaw > 180)
			playerYaw -= 360;
		while(playerYaw < -180)
			playerYaw += 360;
		RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
		mouseOver.getBlockPos();
		if(playerYaw > -45 && playerYaw <= 45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketPlayerTryUseItem(
						new BlockPos(
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() + element[0],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() + element[2]),
						Minecraft.getMinecraft().objectMouseOver.sideHit,
						EnumHand.MAIN_HAND,
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() + element[0]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.yCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.zCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() + element[2])));
		else if(playerYaw > 45 && playerYaw <= 135)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketPlayerTryUseItem(
						new BlockPos(
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() - element[2],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() + element[0]),
						Minecraft.getMinecraft().objectMouseOver.sideHit,
						EnumHand.MAIN_HAND,
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() - element[2]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() + element[0])));
		else if(playerYaw > 135 || playerYaw <= -135)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketPlayerTryUseItem(
						new BlockPos(
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() - element[0],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() - element[2]),
						Minecraft.getMinecraft().objectMouseOver.sideHit,
						EnumHand.MAIN_HAND,
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() - element[0]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() - element[2])));
		else if(playerYaw > -135 && playerYaw <= -45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketPlayerTryUseItem(
						new BlockPos(
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() + element[2],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1],
							Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() - element[0]),
						Minecraft.getMinecraft().objectMouseOver.sideHit,
						EnumHand.MAIN_HAND,
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getX() + element[2]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getY() + element[1]),
						(float)Minecraft
							.getMinecraft().objectMouseOver.hitVec.xCoord
							- (Minecraft.getMinecraft().objectMouseOver
								.getBlockPos().getZ() - element[0])));
	}
	
	public static void buildNext(int[][] building, RayTraceResult mouseOver,
		float playerYaw, int i)
	{
		if(playerYaw > -45 && playerYaw <= 45)
		{// F: 0 South
			BlockUtils.faceBlockPacket(
				new BlockPos(mouseOver.getBlockPos().getX() + building[i][0],
					mouseOver.getBlockPos().getY() + building[i][1],
					mouseOver.getBlockPos().getZ() + building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(
						mouseOver.getBlockPos().getX() + building[i][0],
						mouseOver.getBlockPos().getY() + building[i][1],
						mouseOver.getBlockPos().getZ() + building[i][2]),
					mouseOver.sideHit, EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][0]),
					(float)mouseOver.hitVec.yCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.zCoord
						- (mouseOver.getBlockPos().getZ() + building[i][2])));
		}else if(playerYaw > 45 && playerYaw <= 135)
		{// F: 1 West
			BlockUtils.faceBlockPacket(
				new BlockPos(mouseOver.getBlockPos().getX() - building[i][2],
					mouseOver.getBlockPos().getY() + building[i][1],
					mouseOver.getBlockPos().getZ() + building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(
						mouseOver.getBlockPos().getX() - building[i][2],
						mouseOver.getBlockPos().getY() + building[i][1],
						mouseOver.getBlockPos().getZ() + building[i][0]),
					mouseOver.sideHit, EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() + building[i][0])));
		}else if(playerYaw > 135 || playerYaw <= -135)
		{// F: 2 North
			BlockUtils.faceBlockPacket(
				new BlockPos(mouseOver.getBlockPos().getX() - building[i][0],
					mouseOver.getBlockPos().getY() + building[i][1],
					mouseOver.getBlockPos().getZ() - building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(
						mouseOver.getBlockPos().getX() - building[i][0],
						mouseOver.getBlockPos().getY() + building[i][1],
						mouseOver.getBlockPos().getZ() - building[i][2]),
					mouseOver.sideHit, EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][0]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][2])));
		}else if(playerYaw > -135 && playerYaw <= -45)
		{// F: 3 East
			BlockUtils.faceBlockPacket(
				new BlockPos(mouseOver.getBlockPos().getX() + building[i][2],
					mouseOver.getBlockPos().getY() + building[i][1],
					mouseOver.getBlockPos().getZ() - building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(
						mouseOver.getBlockPos().getX() + building[i][2],
						mouseOver.getBlockPos().getY() + building[i][1],
						mouseOver.getBlockPos().getZ() - building[i][0]),
					mouseOver.sideHit, EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][0])));
		}
	}
	
	public static void advancedBuild(int[][] building)
	{
		float playerYaw = WMinecraft.getPlayer().rotationYaw;
		while(playerYaw > 180)
			playerYaw -= 360;
		while(playerYaw < -180)
			playerYaw += 360;
		if(playerYaw > -45 && playerYaw <= 45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								convertPos(
									1,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[0],
								convertPos(2,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[1],
								convertPos(3,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[2]),
							EnumFacing.getFront(element[3]), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() + element[0]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.yCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.zCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() + element[2])));
		else if(playerYaw > 45 && playerYaw <= 135)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								convertPos(
									1,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									- element[2],
								convertPos(2,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[1],
								convertPos(3,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[0]),
							convertSide(element[3], 1), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() - element[2]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() + element[0])));
		else if(playerYaw > 135 || playerYaw <= -135)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								convertPos(
									1,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									- element[0],
								convertPos(2,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[1],
								convertPos(3,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									- element[2]),
							convertSide(element[3], 2), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() - element[0]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() - element[2])));
		else if(playerYaw > -135 && playerYaw <= -45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								convertPos(
									1,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[2],
								convertPos(2,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									+ element[1],
								convertPos(3,
									Minecraft
										.getMinecraft().objectMouseOver.sideHit
											.getIndex())
									- element[0]),
							convertSide(element[3], 3), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() + element[2]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() - element[0])));
	}
	
	public static void advancedBuildNext(int[][] building,
		RayTraceResult mouseOver, float playerYaw, int i)
	{
		if(playerYaw > -45 && playerYaw <= 45)
		{// F: 0 South
			BlockUtils.faceBlockPacket(
				new BlockPos(convertPosNext(1, mouseOver) + building[i][0],
					convertPosNext(2, mouseOver) + building[i][1],
					convertPosNext(3, mouseOver) + building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(convertPosNext(1, mouseOver) + building[i][0],
						convertPosNext(2, mouseOver) + building[i][1],
						convertPosNext(3, mouseOver) + building[i][2]),
					EnumFacing.getFront(building[i][3]), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][0]),
					(float)mouseOver.hitVec.yCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.zCoord
						- (mouseOver.getBlockPos().getZ() + building[i][2])));
		}else if(playerYaw > 45 && playerYaw <= 135)
		{// F: 1 West
			BlockUtils.faceBlockPacket(
				new BlockPos(convertPosNext(1, mouseOver) - building[i][2],
					convertPosNext(2, mouseOver) + building[i][1],
					convertPosNext(3, mouseOver) + building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(convertPosNext(1, mouseOver) - building[i][2],
						convertPosNext(2, mouseOver) + building[i][1],
						convertPosNext(3, mouseOver) + building[i][0]),
					convertSide(building[i][3], 1), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() + building[i][0])));
		}else if(playerYaw > 135 || playerYaw <= -135)
		{// F: 2 North
			BlockUtils.faceBlockPacket(
				new BlockPos(convertPosNext(1, mouseOver) - building[i][0],
					convertPosNext(2, mouseOver) + building[i][1],
					convertPosNext(3, mouseOver) - building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(convertPosNext(1, mouseOver) - building[i][0],
						convertPosNext(2, mouseOver) + building[i][1],
						convertPosNext(3, mouseOver) - building[i][2]),
					convertSide(building[i][3], 2), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][0]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][2])));
		}else if(playerYaw > -135 && playerYaw <= -45)
		{// F: 3 East
			BlockUtils.faceBlockPacket(
				new BlockPos(convertPosNext(1, mouseOver) + building[i][2],
					convertPosNext(2, mouseOver) + building[i][1],
					convertPosNext(3, mouseOver) - building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos(convertPosNext(1, mouseOver) + building[i][2],
						convertPosNext(2, mouseOver) + building[i][1],
						convertPosNext(3, mouseOver) - building[i][0]),
					convertSide(building[i][3], 3), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][0])));
		}
	}
	
	public static EnumFacing convertSide(int side, int f)
	{
		EnumFacing convertedSide = EnumFacing.DOWN;
		if(side == 0)
			convertedSide = EnumFacing.DOWN;
		else if(side == 1)
			convertedSide = EnumFacing.UP;
		else if(side == 2)
		{
			if(f == 1)
				convertedSide = EnumFacing.EAST;
			else if(f == 2)
				convertedSide = EnumFacing.SOUTH;
			else if(f == 3)
				convertedSide = EnumFacing.WEST;
		}else if(side == 3)
		{
			if(f == 1)
				convertedSide = EnumFacing.WEST;
			else if(f == 2)
				convertedSide = EnumFacing.NORTH;
			else if(f == 3)
				convertedSide = EnumFacing.EAST;
		}else if(side == 4)
		{
			if(f == 1)
				convertedSide = EnumFacing.NORTH;
			else if(f == 2)
				convertedSide = EnumFacing.EAST;
			else if(f == 3)
				convertedSide = EnumFacing.SOUTH;
		}else if(side == 5)
			if(f == 1)
				convertedSide = EnumFacing.SOUTH;
			else if(f == 2)
				convertedSide = EnumFacing.WEST;
			else if(f == 3)
				convertedSide = EnumFacing.NORTH;
		return convertedSide;
	}
	
	public static int convertPos(int xyz, int side)
	{
		int convertedPos = 256;
		if(side == 0)
		{
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY() - 2;
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ();
		}else if(side == 1)
		{
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY();
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ();
		}else if(side == 2)
		{
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ() - 1;
		}else if(side == 3)
		{
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ() + 1;
		}else if(side == 4)
		{
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX() - 1;
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ();
		}else if(side == 5)
			if(xyz == 1)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getX() + 1;
			else if(xyz == 2)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = Minecraft.getMinecraft().objectMouseOver
					.getBlockPos().getZ();
		return convertedPos;
	}
	
	public static int convertPosNext(int xyz, RayTraceResult mouseOver)
	{
		int convertedPos = 256;
		if(mouseOver.sideHit.getIndex() == 0)
		{
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY() - 2;
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ();
		}else if(mouseOver.sideHit.getIndex() == 1)
		{
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY();
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ();
		}else if(mouseOver.sideHit.getIndex() == 2)
		{
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ() - 1;
		}else if(mouseOver.sideHit.getIndex() == 3)
		{
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX();
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ() + 1;
		}else if(mouseOver.sideHit.getIndex() == 4)
		{
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX() - 1;
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ();
		}else if(mouseOver.sideHit.getIndex() == 5)
			if(xyz == 1)
				convertedPos = mouseOver.getBlockPos().getX() + 1;
			else if(xyz == 2)
				convertedPos = mouseOver.getBlockPos().getY() - 1;
			else if(xyz == 3)
				convertedPos = mouseOver.getBlockPos().getZ();
		return convertedPos;
	}
	
	public static int convertPosInBuiling(int xyz, int i, int[][] building,
		RayTraceResult mouseOver)
	{
		int convertedPos = 256;
		if(i < 0)
			i = 0;
		if(mouseOver.sideHit.getIndex() == 0)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1] - 1;
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(mouseOver.sideHit.getIndex() == 1)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1] + 1;
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(mouseOver.sideHit.getIndex() == 2)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2] - 1;
		}else if(mouseOver.sideHit.getIndex() == 3)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2] + 1;
		}else if(mouseOver.sideHit.getIndex() == 4)
		{
			if(xyz == 1)
				convertedPos = building[i][0] - 1;
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(mouseOver.sideHit.getIndex() == 5)
			if(xyz == 1)
				convertedPos = building[i][0] + 1;
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2];
		return convertedPos;
	}
	
	public static int convertPosInAdvancedBuiling(int xyz, int i,
		int[][] building)
	{
		int convertedPos = 256;
		if(i < 0)
			i = 0;
		if(building[i][3] == 0)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1] - 1;
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(building[i][3] == 1)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1] + 1;
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(building[i][3] == 2)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2] - 1;
		}else if(building[i][3] == 3)
		{
			if(xyz == 1)
				convertedPos = building[i][0];
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2] + 1;
		}else if(building[i][3] == 4)
		{
			if(xyz == 1)
				convertedPos = building[i][0] - 1;
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2];
		}else if(building[i][3] == 5)
			if(xyz == 1)
				convertedPos = building[i][0] + 1;
			else if(xyz == 2)
				convertedPos = building[i][1];
			else if(xyz == 3)
				convertedPos = building[i][2];
		return convertedPos;
	}
	
	public static void advancedInstantBuild(int[][] building)
	{
		float playerYaw = WMinecraft.getPlayer().rotationYaw;
		while(playerYaw > 180)
			playerYaw -= 360;
		while(playerYaw < -180)
			playerYaw += 360;
		if(playerYaw > -45 && playerYaw <= 45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								(int)WMinecraft.getPlayer().posX + element[0],
								(int)WMinecraft.getPlayer().posY
									- 2 + element[1],
								(int)WMinecraft.getPlayer().posZ + element[2]),
							EnumFacing.getFront(element[3]), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() + element[0]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.yCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.zCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() + element[2])));
		else if(playerYaw > 45 && playerYaw <= 135)
			for(int[] element : building)
				WMinecraft
					.getPlayer().sendQueue
						.addToSendQueue(
							new CPacketPlayerTryUseItem(
								new BlockPos(
									(int)(WMinecraft.getPlayer().posX
										- element[2]),
									(int)WMinecraft.getPlayer().posY - 2
										+ element[1],
									(int)WMinecraft.getPlayer().posZ
										+ element[0]),
								convertSide(element[3], 1), EnumHand.MAIN_HAND,
								(float)Minecraft
									.getMinecraft().objectMouseOver.hitVec.xCoord
									- (Minecraft.getMinecraft().objectMouseOver
										.getBlockPos().getX() - element[2]),
								(float)Minecraft
									.getMinecraft().objectMouseOver.hitVec.xCoord
									- (Minecraft.getMinecraft().objectMouseOver
										.getBlockPos().getY() + element[1]),
								(float)Minecraft
									.getMinecraft().objectMouseOver.hitVec.xCoord
									- (Minecraft.getMinecraft().objectMouseOver
										.getBlockPos().getZ() + element[0])));
		else if(playerYaw > 135 || playerYaw <= -135)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								(int)WMinecraft.getPlayer().posX - element[0],
								(int)WMinecraft.getPlayer().posY - 2
									+ element[1],
								(int)WMinecraft.getPlayer().posZ - element[2]),
							convertSide(element[3], 2), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() - element[0]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() - element[2])));
		else if(playerYaw > -135 && playerYaw <= -45)
			for(int[] element : building)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(
						new CPacketPlayerTryUseItem(
							new BlockPos(
								(int)WMinecraft.getPlayer().posX + element[2],
								(int)WMinecraft.getPlayer().posY - 2
									+ element[1],
								(int)WMinecraft.getPlayer().posZ - element[0]),
							convertSide(element[3], 3), EnumHand.MAIN_HAND,
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getX() + element[2]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getY() + element[1]),
							(float)Minecraft
								.getMinecraft().objectMouseOver.hitVec.xCoord
								- (Minecraft.getMinecraft().objectMouseOver
									.getBlockPos().getZ() - element[0])));
	}
	
	public static void advancedInstantBuildNext(int[][] building,
		RayTraceResult mouseOver, float playerYaw, double posX, double posY,
		double posZ, int i)
	{
		if(playerYaw > -45 && playerYaw <= 45)
		{// F: 0 South
			BlockUtils.faceBlockPacket(new BlockPos(
				(int)posX - 1 + building[i][0], (int)posY - 2 + building[i][1],
				(int)posZ + building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos((int)posX - 1 + building[i][0],
						(int)posY - 2 + building[i][1],
						(int)posZ + building[i][2]),
					EnumFacing.getFront(building[i][3]), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][0]),
					(float)mouseOver.hitVec.yCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.zCoord
						- (mouseOver.getBlockPos().getZ() + building[i][2])));
		}else if(playerYaw > 45 && playerYaw <= 135)
		{// F: 1 West
			BlockUtils.faceBlockPacket(new BlockPos(
				(int)(posX - 1 - building[i][2]),
				(int)posY - 2 + building[i][1], (int)posZ + building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos((int)(posX - 1 - building[i][2]),
						(int)posY - 2 + building[i][1],
						(int)posZ + building[i][0]),
					convertSide(building[i][3], 1), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() + building[i][0])));
		}else if(playerYaw > 135 || playerYaw <= -135)
		{// F: 2 North
			BlockUtils.faceBlockPacket(new BlockPos(
				(int)posX - 1 - building[i][0], (int)posY - 2 + building[i][1],
				(int)posZ - building[i][2]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos((int)posX - 1 - building[i][0],
						(int)posY - 2 + building[i][1],
						(int)posZ - building[i][2]),
					convertSide(building[i][3], 2), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() - building[i][0]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][2])));
		}else if(playerYaw > -135 && playerYaw <= -45)
		{// F: 3 East
			BlockUtils.faceBlockPacket(new BlockPos(
				(int)posX - 1 + building[i][2], (int)posY - 2 + building[i][1],
				(int)posZ - building[i][0]));
			WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketPlayerTryUseItem(
					new BlockPos((int)posX - 1 + building[i][2],
						(int)posY - 2 + building[i][1],
						(int)posZ - building[i][0]),
					convertSide(building[i][3], 3), EnumHand.MAIN_HAND,
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getX() + building[i][2]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getY() + building[i][1]),
					(float)mouseOver.hitVec.xCoord
						- (mouseOver.getBlockPos().getZ() - building[i][0])));
		}
	}
}
