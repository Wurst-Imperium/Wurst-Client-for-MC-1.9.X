/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.BlockUtils;

@Info(category = Category.BLOCKS,
	description = "Places random blocks around you.",
	name = "BuildRandom",
	tags = "build random",
	help = "Mods/BuildRandom")
@Bypasses(ghostMode = false)
public class BuildRandomMod extends Mod implements UpdateListener
{
	private float range = 6;
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.autoBuildMod,
			wurst.mods.fastPlaceMod, wurst.mods.autoSwitchMod};
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
			|| wurst.mods.remoteViewMod.isActive()
			|| mc.objectMouseOver == null
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
					if(Block.getIdFromBlock(mc.theWorld.getBlockState(
						new BlockPos((int)(x + mc.thePlayer.posX),
							(int)(y + mc.thePlayer.posY),
							(int)(z + mc.thePlayer.posZ))).getBlock()) != 0
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
		while(distance > range
			|| distance < -range
			|| randomPos == null
			|| Block.getIdFromBlock(mc.theWorld.getBlockState(randomPos)
				.getBlock()) == 0)
		{
			xDiff = (int)(Math.random() * range * 2 - range - 1);
			yDiff = (int)(Math.random() * range * 2 - range);
			zDiff = (int)(Math.random() * range * 2 - range);
			distance = BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
			int randomPosX = (int)(xDiff + mc.thePlayer.posX);
			int randomPosY = (int)(yDiff + mc.thePlayer.posY);
			int randomPosZ = (int)(zDiff + mc.thePlayer.posZ);
			randomPos = new BlockPos(randomPosX, randomPosY, randomPosZ);
		}
		BlockUtils.faceBlockPacket(randomPos);
		mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
		mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayerTryUseItem(
			randomPos, mc.objectMouseOver.sideHit, EnumHand.MAIN_HAND,
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
