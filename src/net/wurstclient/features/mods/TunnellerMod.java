/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(description = "Digs a 3x3 tunnel around you.",
	name = "Tunneller",
	help = "Mods/Tunneller")
@Mod.Bypasses
public final class TunnellerMod extends Mod
	implements RenderListener, UpdateListener
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
	public void onRender(float partialTicks)
	{
		if(blockHitDelay == 0 && shouldRenderESP)
			if(!WMinecraft.getPlayer().capabilities.isCreativeMode
				&& currentBlock.getPlayerRelativeBlockHardness(
					WMinecraft.getWorld().getBlockState(pos),
					WMinecraft.getPlayer(), WMinecraft.getWorld(), pos) < 1)
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
				WMinecraft.getPlayer().inventory.currentItem = oldSlot;
				oldSlot = -1;
			}
			return;
		}
		if(pos == null || !pos.equals(newPos))
			currentDamage = 0;
		pos = newPos;
		currentBlock = WMinecraft.getWorld().getBlockState(pos).getBlock();
		if(blockHitDelay > 0)
		{
			blockHitDelay--;
			return;
		}
		BlockUtils.faceBlockPacket(pos);
		if(currentDamage == 0)
		{
			WMinecraft.getPlayer().sendQueue.addToSendQueue(
				new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, pos,
					side));
			if(wurst.mods.autoToolMod.isActive() && oldSlot == -1)
				oldSlot = WMinecraft.getPlayer().inventory.currentItem;
			if(WMinecraft.getPlayer().capabilities.isCreativeMode
				|| currentBlock.getPlayerRelativeBlockHardness(
					WMinecraft.getWorld().getBlockState(pos),
					WMinecraft.getPlayer(), WMinecraft.getWorld(), pos) >= 1)
			{
				currentDamage = 0;
				if(WMinecraft.getPlayer().capabilities.isCreativeMode
					&& wurst.special.yesCheatSpf.getBypassLevel()
						.ordinal() <= BypassLevel.MINEPLEX.ordinal())
					nukeAll();
				else
				{
					shouldRenderESP = true;
					WPlayer.swingArmClient();
					mc.playerController.onPlayerDestroyBlock(pos);
				}
				return;
			}
		}
		if(wurst.mods.autoToolMod.isActive())
			AutoToolMod.setSlot(pos);
		WPlayer.swingArmPacket();
		shouldRenderESP = true;
		BlockUtils.faceBlockPacket(pos);
		currentDamage += currentBlock.getPlayerRelativeBlockHardness(
			WMinecraft.getWorld().getBlockState(pos), WMinecraft.getPlayer(),
			WMinecraft.getWorld(), pos)
			* (wurst.mods.fastBreakMod.isActive()
				&& wurst.mods.fastBreakMod.getMode() == 0
					? wurst.mods.fastBreakMod.speed : 1);
		WMinecraft.getWorld().sendBlockBreakProgress(
			WMinecraft.getPlayer().getEntityId(), pos,
			(int)(currentDamage * 10.0F) - 1);
		if(currentDamage >= 1)
		{
			WMinecraft.getPlayer().sendQueue.addToSendQueue(
				new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
			mc.playerController.onPlayerDestroyBlock(pos);
			blockHitDelay = (byte)4;
			currentDamage = 0;
		}else if(wurst.mods.fastBreakMod.isActive()
			&& wurst.mods.fastBreakMod.getMode() == 1)
			WMinecraft.getPlayer().sendQueue.addToSendQueue(
				new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		if(oldSlot != -1)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
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
					if(WMinecraft.getPlayer() == null)
						continue;
					int posX =
						(int)(Math.floor(WMinecraft.getPlayer().posX) + x);
					int posY =
						(int)(Math.floor(WMinecraft.getPlayer().posY) + y);
					int posZ =
						(int)(Math.floor(WMinecraft.getPlayer().posZ) + z);
					BlockPos blockPos = new BlockPos(posX, posY, posZ);
					Block block = WMinecraft.getWorld().getBlockState(blockPos)
						.getBlock();
					float xDiff = (float)(WMinecraft.getPlayer().posX - posX);
					float yDiff = (float)(WMinecraft.getPlayer().posY - posY);
					float zDiff = (float)(WMinecraft.getPlayer().posZ - posZ);
					float currentDistance = xDiff + yDiff + zDiff;
					if(Block.getIdFromBlock(block) != 0 && posY >= 0)
					{
						if(wurst.mods.nukerMod.mode.getSelected() == 3
							&& block.getPlayerRelativeBlockHardness(
								WMinecraft.getWorld().getBlockState(blockPos),
								WMinecraft.getPlayer(), WMinecraft.getWorld(),
								blockPos) < 1)
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
					int posX =
						(int)(Math.floor(WMinecraft.getPlayer().posX) + x);
					int posY =
						(int)(Math.floor(WMinecraft.getPlayer().posY) + y);
					int posZ =
						(int)(Math.floor(WMinecraft.getPlayer().posZ) + z);
					BlockPos blockPos = new BlockPos(posX, posY, posZ);
					Block block = WMinecraft.getWorld().getBlockState(blockPos)
						.getBlock();
					if(Block.getIdFromBlock(block) != 0 && posY >= 0)
					{
						if(wurst.mods.nukerMod.mode.getSelected() == 3
							&& block.getPlayerRelativeBlockHardness(
								WMinecraft.getWorld().getBlockState(blockPos),
								WMinecraft.getPlayer(), WMinecraft.getWorld(),
								blockPos) < 1)
							continue;
						side = mc.objectMouseOver.sideHit;
						shouldRenderESP = true;
						BlockUtils.faceBlockPacket(pos);
						WMinecraft.getPlayer().sendQueue.addToSendQueue(
							new CPacketPlayerDigging(Action.START_DESTROY_BLOCK,
								blockPos, side));
						block.onBlockDestroyedByPlayer(WMinecraft.getWorld(),
							blockPos,
							WMinecraft.getWorld().getBlockState(blockPos));
					}
				}
	}
}
