/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(category = Mod.Category.BLOCKS,
	description = "Digs a 3x3 tunnel around you.",
	name = "Tunneller",
	help = "Mods/Tunneller")
@Bypasses
public class TunnellerMod extends Mod implements RenderListener, UpdateListener
{
	private static Block currentBlock;
	private float currentDamage;
	private EnumFacing side = EnumFacing.UP;
	private byte blockHitDelay = 0;
	private BlockPos pos;
	private boolean shouldRenderESP;
	private int oldSlot = -1;
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.nukerMod.isEnabled())
			wurst.mods.nukerMod.setEnabled(false);
		if(wurst.mods.nukerLegitMod.isEnabled())
			wurst.mods.nukerLegitMod.setEnabled(false);
		if(wurst.mods.speedNukerMod.isEnabled())
			wurst.mods.speedNukerMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.nukerLegitMod,
			wurst.mods.speedNukerMod, wurst.mods.fastBreakMod,
			wurst.mods.autoMineMod};
	}
	
	@Override
	public void onRender()
	{
		if(blockHitDelay == 0 && shouldRenderESP)
			if(!mc.thePlayer.capabilities.isCreativeMode && currentBlock
				.getPlayerRelativeBlockHardness(mc.theWorld.getBlockState(pos),
					mc.thePlayer, mc.theWorld, pos) < 1)
				RenderUtils.nukerBox(pos, currentDamage);
			else
				RenderUtils.nukerBox(pos, 1);
	}
	
	@Override
	public void onUpdate()
	{
		shouldRenderESP = false;
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
		if(pos == null || !pos.equals(newPos))
			currentDamage = 0;
		pos = newPos;
		currentBlock = mc.theWorld.getBlockState(pos).getBlock();
		if(blockHitDelay > 0)
		{
			blockHitDelay--;
			return;
		}
		BlockUtils.faceBlockPacket(pos);
		if(currentDamage == 0)
		{
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayerDigging(
				Action.START_DESTROY_BLOCK, pos, side));
			if(wurst.mods.autoToolMod.isActive() && oldSlot == -1)
				oldSlot = mc.thePlayer.inventory.currentItem;
			if(mc.thePlayer.capabilities.isCreativeMode || currentBlock
				.getPlayerRelativeBlockHardness(mc.theWorld.getBlockState(pos),
					mc.thePlayer, mc.theWorld, pos) >= 1)
			{
				currentDamage = 0;
				if(mc.thePlayer.capabilities.isCreativeMode
					&& wurst.special.yesCheatSpf.getBypassLevel()
						.ordinal() <= BypassLevel.MINEPLEX_ANTICHEAT.ordinal())
					nukeAll();
				else
				{
					shouldRenderESP = true;
					mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
					mc.playerController.onPlayerDestroyBlock(pos);
				}
				return;
			}
		}
		if(wurst.mods.autoToolMod.isActive())
			AutoToolMod.setSlot(pos);
		mc.thePlayer.sendQueue
			.addToSendQueue(new CPacketAnimation(EnumHand.MAIN_HAND));
		shouldRenderESP = true;
		BlockUtils.faceBlockPacket(pos);
		currentDamage += currentBlock.getPlayerRelativeBlockHardness(
			mc.theWorld.getBlockState(pos), mc.thePlayer, mc.theWorld, pos)
			* (wurst.mods.fastBreakMod.isActive()
				&& wurst.mods.fastBreakMod.getMode() == 0
					? wurst.mods.fastBreakMod.speed : 1);
		mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos,
			(int)(currentDamage * 10.0F) - 1);
		if(currentDamage >= 1)
		{
			mc.thePlayer.sendQueue.addToSendQueue(
				new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
			mc.playerController.onPlayerDestroyBlock(pos);
			blockHitDelay = (byte)4;
			currentDamage = 0;
		}else if(wurst.mods.fastBreakMod.isActive()
			&& wurst.mods.fastBreakMod.getMode() == 1)
			mc.thePlayer.sendQueue.addToSendQueue(
				new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		if(oldSlot != -1)
		{
			mc.thePlayer.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		currentDamage = 0;
		shouldRenderESP = false;
	}
	
	private BlockPos find()
	{
		BlockPos closest = null;
		float closestDistance = 16;
		for(int y = 2; y >= 0; y--)
			for(int x = 1; x >= -1; x--)
				for(int z = 1; z >= -1; z--)
				{
					if(mc.thePlayer == null)
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
					float currentDistance = xDiff + yDiff + zDiff;
					if(Block.getIdFromBlock(block) != 0 && posY >= 0)
					{
						if(wurst.mods.nukerMod.mode.getSelected() == 3
							&& block.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(blockPos),
								mc.thePlayer, mc.theWorld, blockPos) < 1)
							continue;
						side = mc.objectMouseOver.sideHit;
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
		for(int y = 2; y >= 0; y--)
			for(int x = 1; x >= -1; x--)
				for(int z = 1; z >= -1; z--)
				{
					int posX = (int)(Math.floor(mc.thePlayer.posX) + x);
					int posY = (int)(Math.floor(mc.thePlayer.posY) + y);
					int posZ = (int)(Math.floor(mc.thePlayer.posZ) + z);
					BlockPos blockPos = new BlockPos(posX, posY, posZ);
					Block block =
						mc.theWorld.getBlockState(blockPos).getBlock();
					if(Block.getIdFromBlock(block) != 0 && posY >= 0)
					{
						if(wurst.mods.nukerMod.mode.getSelected() == 3
							&& block.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(blockPos),
								mc.thePlayer, mc.theWorld, blockPos) < 1)
							continue;
						side = mc.objectMouseOver.sideHit;
						shouldRenderESP = true;
						BlockUtils.faceBlockPacket(pos);
						mc.thePlayer.sendQueue.addToSendQueue(
							new CPacketPlayerDigging(Action.START_DESTROY_BLOCK,
								blockPos, side));
						block.onBlockDestroyedByPlayer(mc.theWorld, blockPos,
							mc.theWorld.getBlockState(blockPos));
					}
				}
	}
}
