/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class WPlayerController
{
	private static PlayerControllerMP getPlayerController()
	{
		return Minecraft.getMinecraft().playerController;
	}
	
	public static void processRightClick()
	{
		getPlayerController().sendUseItem(WMinecraft.getPlayer(),
			WMinecraft.getWorld(),
			WMinecraft.getPlayer().inventory.getCurrentItem(),
			EnumHand.MAIN_HAND);
	}
	
	public static void processRightClickBlock(BlockPos pos, EnumFacing side,
		Vec3d hitVec)
	{
		getPlayerController().func_187099_a(WMinecraft.getPlayer(),
			WMinecraft.getWorld(),
			WMinecraft.getPlayer().inventory.getCurrentItem(), pos, side,
			hitVec, EnumHand.MAIN_HAND);
	}
}
