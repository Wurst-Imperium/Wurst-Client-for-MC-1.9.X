/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.utils.BlockUtils;

@Mod.Info(description = "Places random blocks around you.",
	name = "BuildRandom",
	tags = "build random",
	help = "Mods/BuildRandom")
@Mod.Bypasses(ghostMode = false)
public final class BuildRandomMod extends Mod implements UpdateListener
{
	private float range = 6;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoBuildMod, wurst.mods.fastPlaceMod,
			wurst.mods.autoSwitchMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.mods.freecamMod.isActive()
			|| wurst.mods.remoteViewMod.isActive() || mc.objectMouseOver == null
			|| mc.objectMouseOver.typeOfHit != Type.BLOCK)
			return;
		if(mc.rightClickDelayTimer > 0 && !wurst.mods.fastPlaceMod.isActive())
			return;
		float xDiff = 0;
		float yDiff = 0;
		float zDiff = 0;
		float distance = range + 1;
		boolean hasBlocks = false;
		for(int y = (int)range; y >= -range; y--)
		{
			for(int x = (int)range; x >= -range - 1; x--)
			{
				for(int z = (int)range; z >= -range; z--)
					if(Block
						.getIdFromBlock(WMinecraft.getWorld()
							.getBlockState(new BlockPos(
								(int)(x + WMinecraft.getPlayer().posX),
								(int)(y + WMinecraft.getPlayer().posY),
								(int)(z + WMinecraft.getPlayer().posZ)))
							.getBlock()) != 0
						&& BlockUtils.getBlockDistance(x, y, z) <= range)
					{
						hasBlocks = true;
						break;
					}
				if(hasBlocks)
					break;
			}
			if(hasBlocks)
				break;
		}
		if(!hasBlocks)
			return;
		BlockPos randomPos = null;
		while(distance > range || distance < -range || randomPos == null
			|| Block.getIdFromBlock(
				WMinecraft.getWorld().getBlockState(randomPos).getBlock()) == 0)
		{
			xDiff = (int)(Math.random() * range * 2 - range - 1);
			yDiff = (int)(Math.random() * range * 2 - range);
			zDiff = (int)(Math.random() * range * 2 - range);
			distance = BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
			int randomPosX = (int)(xDiff + WMinecraft.getPlayer().posX);
			int randomPosY = (int)(yDiff + WMinecraft.getPlayer().posY);
			int randomPosZ = (int)(zDiff + WMinecraft.getPlayer().posZ);
			randomPos = new BlockPos(randomPosX, randomPosY, randomPosZ);
		}
		BlockUtils.faceBlockPacket(randomPos);
		WPlayer.swingArmClient();
		WConnection.sendPacket(new CPacketPlayerTryUseItem(randomPos,
			mc.objectMouseOver.sideHit, EnumHand.MAIN_HAND,
			(float)mc.objectMouseOver.hitVec.xCoord
				- mc.objectMouseOver.getBlockPos().getX(),
			(float)mc.objectMouseOver.hitVec.yCoord
				- mc.objectMouseOver.getBlockPos().getY(),
			(float)mc.objectMouseOver.hitVec.zCoord
				- mc.objectMouseOver.getBlockPos().getZ()));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
