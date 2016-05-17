/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.LeftClickListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.BlockUtils;

@Info(category = Category.BLOCKS,
	description = "Faster Nuker that cannot bypass NoCheat+.",
	name = "SpeedNuker",
	tags = "FastNuker, speed nuker, fast nuker",
	help = "Mods/SpeedNuker")
public class SpeedNukerMod extends Mod implements LeftClickListener,
	UpdateListener
{
	private static Block currentBlock;
	private BlockPos pos;
	private int oldSlot = -1;
	
	@Override
	public String getRenderName()
	{
		NukerMod nuker = wurst.mods.nukerMod;
		switch(nuker.getMode())
		{
			case 0:
				return "SpeedNuker";
			case 1:
				return "IDSpeedNuker [" + NukerMod.id + "]";
			default:
				return nuker.getModes()[nuker.getMode()] + "SpeedNuker";
		}
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.nukerMod,
			wurst.mods.nukerLegitMod, wurst.mods.tunnellerMod,
			wurst.mods.fastBreakMod, wurst.mods.autoMineMod};
	}
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.nukerMod.isEnabled())
			wurst.mods.nukerMod.setEnabled(false);
		if(wurst.mods.nukerLegitMod.isEnabled())
			wurst.mods.nukerLegitMod.setEnabled(false);
		if(wurst.mods.tunnellerMod.isEnabled())
			wurst.mods.tunnellerMod.setEnabled(false);
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.mods.yesCheatMod.isActive())
		{
			noCheatMessage();
			setEnabled(false);
			wurst.chat.message("Switching to " + wurst.mods.nukerMod.getName()
				+ ".");
			wurst.mods.nukerMod.setEnabled(true);
			return;
		}
		if(mc.thePlayer.capabilities.isCreativeMode)
		{
			wurst.chat.error(getName() + " doesn't work in creative mode.");
			setEnabled(false);
			wurst.chat.message("Switching to " + wurst.mods.nukerMod.getName()
				+ ".");
			wurst.mods.nukerMod.setEnabled(true);
			return;
		}
		BlockPos newPos = find();
		if(newPos == null)
		{
			if(oldSlot != -1)
			{
				mc.thePlayer.inventory.currentItem = oldSlot;
				oldSlot = -1;
			}
			return;
		}
		pos = newPos;
		currentBlock = mc.theWorld.getBlockState(pos).getBlock();
		if(wurst.mods.autoToolMod.isActive() && oldSlot == -1)
			oldSlot = mc.thePlayer.inventory.currentItem;
		if(!mc.thePlayer.capabilities.isCreativeMode
			&& wurst.mods.autoToolMod.isActive()
			&& currentBlock.getPlayerRelativeBlockHardness(
				mc.theWorld.getBlockState(pos), mc.thePlayer, mc.theWorld, pos) < 1)
			AutoToolMod.setSlot(pos);
		nukeAll();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		if(oldSlot != -1)
		{
			mc.thePlayer.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		NukerMod.id = 0;
		wurst.files.saveOptions();
	}
	
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(wurst.mods.nukerMod.getMode() == 1
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.air)
		{
			NukerMod.id =
				Block.getIdFromBlock(mc.theWorld.getBlockState(
					mc.objectMouseOver.getBlockPos()).getBlock());
			wurst.files.saveOptions();
		}
	}
	
	private BlockPos find()
	{
		BlockPos closest = null;
		float closestDistance = wurst.mods.nukerMod.yesCheatRange + 1;
		int nukerMode = wurst.mods.nukerMod.getMode();
		for(int y = (int)wurst.mods.nukerMod.yesCheatRange; y >= (nukerMode == 2
			? 0 : -wurst.mods.nukerMod.yesCheatRange); y--)
			for(int x = (int)wurst.mods.nukerMod.yesCheatRange; x >= -wurst.mods.nukerMod.yesCheatRange - 1; x--)
				for(int z = (int)wurst.mods.nukerMod.yesCheatRange; z >= -wurst.mods.nukerMod.yesCheatRange; z--)
				{
					if(mc.thePlayer == null)
						continue;
					if(x == 0 && y == -1 && z == 0)
						continue;
					int posX = (int)(Math.floor(mc.thePlayer.posX) + x);
					int posY = (int)(Math.floor(mc.thePlayer.posY) + y);
					int posZ = (int)(Math.floor(mc.thePlayer.posZ) + z);
					BlockPos blockPos = new BlockPos(posX, posY, posZ);
					Block block =
						mc.theWorld.getBlockState(blockPos).getBlock();
					float xDiff = (float)(mc.thePlayer.posX - posX);
					float yDiff = (float)(mc.thePlayer.posY - posY);
					float zDiff = (float)(mc.thePlayer.posZ - posZ);
					float currentDistance =
						BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
					if(Block.getIdFromBlock(block) != 0 && posY >= 0
						&& currentDistance <= wurst.mods.nukerMod.yesCheatRange)
					{
						if(nukerMode == 1
							&& Block.getIdFromBlock(block) != NukerMod.id)
							continue;
						if(nukerMode == 3
							&& block.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(blockPos),
								mc.thePlayer, mc.theWorld, blockPos) < 1)
							continue;
						if(closest == null)
						{
							closest = blockPos;
							closestDistance = currentDistance;
						}else if(currentDistance < closestDistance)
						{
							closest = blockPos;
							closestDistance = currentDistance;
						}
					}
				}
		return closest;
	}
	
	private void nukeAll()
	{
		int nukerMode = wurst.mods.nukerMod.getMode();
		for(int y = (int)wurst.mods.nukerMod.normalRange; y >= (nukerMode == 2
			? 0 : -wurst.mods.nukerMod.normalRange); y--)
			for(int x = (int)wurst.mods.nukerMod.normalRange; x >= -wurst.mods.nukerMod.normalRange - 1; x--)
				for(int z = (int)wurst.mods.nukerMod.normalRange; z >= -wurst.mods.nukerMod.normalRange; z--)
				{
					int posX = (int)(Math.floor(mc.thePlayer.posX) + x);
					int posY = (int)(Math.floor(mc.thePlayer.posY) + y);
					int posZ = (int)(Math.floor(mc.thePlayer.posZ) + z);
					if(x == 0 && y == -1 && z == 0)
						continue;
					BlockPos blockPos = new BlockPos(posX, posY, posZ);
					Block block =
						mc.theWorld.getBlockState(blockPos).getBlock();
					float xDiff = (float)(mc.thePlayer.posX - posX);
					float yDiff = (float)(mc.thePlayer.posY - posY);
					float zDiff = (float)(mc.thePlayer.posZ - posZ);
					float currentDistance =
						BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
					if(Block.getIdFromBlock(block) != 0 && posY >= 0
						&& currentDistance <= wurst.mods.nukerMod.normalRange)
					{
						if(nukerMode == 1
							&& Block.getIdFromBlock(block) != NukerMod.id)
							continue;
						if(nukerMode == 3
							&& block.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(blockPos),
								mc.thePlayer, mc.theWorld, blockPos) < 1)
							continue;
						if(!mc.thePlayer.onGround)
							continue;
						EnumFacing side = mc.objectMouseOver.sideHit;
						mc.thePlayer.sendQueue
							.addToSendQueue(new CPacketPlayerDigging(
								Action.START_DESTROY_BLOCK, blockPos, side));
						mc.thePlayer.sendQueue
							.addToSendQueue(new CPacketPlayerDigging(
								Action.STOP_DESTROY_BLOCK, blockPos, side));
					}
				}
	}
}
