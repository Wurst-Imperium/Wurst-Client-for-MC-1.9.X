/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@Info(category = Category.BLOCKS,
	description = "Destroys blocks around you.\n"
		+ "Use .nuker mode <mode> to change the mode.",
	name = "Nuker",
	help = "Mods/Nuker")
@Bypasses
public class NukerMod extends Mod
	implements LeftClickListener, RenderListener, UpdateListener
{
	private static Block currentBlock;
	private float currentDamage;
	private EnumFacing side = EnumFacing.UP;
	private byte blockHitDelay = 0;
	public static int id = 0;
	private BlockPos pos;
	private boolean shouldRenderESP;
	private int oldSlot = -1;
	
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "ID", "Flat", "Smash"}, 0);
	
	@Override
	public String getRenderName()
	{
		switch(mode.getSelected())
		{
			case 0:
			return "Nuker";
			case 1:
			return "IDNuker [" + id + "]";
			default:
			return mode.getSelectedMode() + "Nuker";
		}
	}
	
	@Override
	public void initSettings()
	{
		settings.add(range);
		settings.add(mode);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerLegitMod, wurst.mods.speedNukerMod,
			wurst.mods.tunnellerMod, wurst.mods.fastBreakMod,
			wurst.mods.autoMineMod, wurst.mods.overlayMod};
	}
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.nukerLegitMod.isEnabled())
			wurst.mods.nukerLegitMod.setEnabled(false);
		if(wurst.mods.speedNukerMod.isEnabled())
			wurst.mods.speedNukerMod.setEnabled(false);
		if(wurst.mods.tunnellerMod.isEnabled())
			wurst.mods.tunnellerMod.setEnabled(false);
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
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
						.ordinal() <= BypassLevel.MINEPLEX_ANTICHEAT.ordinal())
					nukeAll();
				else
				{
					shouldRenderESP = true;
					WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
					mc.playerController.onPlayerDestroyBlock(pos);
				}
				return;
			}
		}
		if(wurst.mods.autoToolMod.isActive())
			AutoToolMod.setSlot(pos);
		WMinecraft.getPlayer().sendQueue
			.addToSendQueue(new CPacketAnimation(EnumHand.MAIN_HAND));
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
	public void onLeftClick(LeftClickEvent event)
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(mode.getSelected() == 1 && WMinecraft.getWorld()
			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()
			.getMaterial(null) != Material.air)
		{
			id = Block.getIdFromBlock(WMinecraft.getWorld()
				.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
			wurst.files.saveOptions();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		if(oldSlot != -1)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		currentDamage = 0;
		shouldRenderESP = false;
		id = 0;
		wurst.files.saveOptions();
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX_ANTICHEAT:
			range.unlock();
			break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			range.lockToMax(4.25);
			break;
		}
	}
	
	private BlockPos find()
	{
		LinkedList<BlockPos> queue = new LinkedList<>();
		HashSet<BlockPos> alreadyProcessed = new HashSet<>();
		queue.add(new BlockPos(WMinecraft.getPlayer()));
		while(!queue.isEmpty())
		{
			BlockPos currentPos = queue.poll();
			if(alreadyProcessed.contains(currentPos))
				continue;
			alreadyProcessed.add(currentPos);
			if(BlockUtils.getPlayerBlockDistance(currentPos) > range
				.getValueF())
				continue;
			int currentID = Block.getIdFromBlock(
				WMinecraft.getWorld().getBlockState(currentPos).getBlock());
			if(currentID != 0)
				switch(mode.getSelected())
				{
					case 1:
					if(currentID == id)
						return currentPos;
					break;
					case 2:
					if(currentPos.getY() >= WMinecraft.getPlayer().posY)
						return currentPos;
					break;
					case 3:
					if(WMinecraft.getWorld().getBlockState(currentPos)
						.getBlock().getPlayerRelativeBlockHardness(
							WMinecraft.getWorld().getBlockState(pos),
							WMinecraft.getPlayer(), WMinecraft.getWorld(),
							currentPos) >= 1)
						return currentPos;
					break;
					default:
					return currentPos;
				}
			if(wurst.special.yesCheatSpf.getBypassLevel()
				.ordinal() <= BypassLevel.MINEPLEX_ANTICHEAT.ordinal()
				|| !WMinecraft.getWorld().getBlockState(currentPos).getBlock()
					.getMaterial(null).blocksMovement())
			{
				queue.add(currentPos.add(0, 0, -1));// north
				queue.add(currentPos.add(0, 0, 1));// south
				queue.add(currentPos.add(-1, 0, 0));// west
				queue.add(currentPos.add(1, 0, 0));// east
				queue.add(currentPos.add(0, -1, 0));// down
				queue.add(currentPos.add(0, 1, 0));// up
			}
		}
		return null;
	}
	
	private void nukeAll()
	{
		for(int y = (int)range.getValueF(); y >= (mode.getSelected() == 2 ? 0
			: -range.getValueF()); y--)
			for(int x = (int)range.getValueF(); x >= -range.getValueF()
				- 1; x--)
				for(int z =
					(int)range.getValueF(); z >= -range.getValueF(); z--)
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
					float xDiff = (float)(WMinecraft.getPlayer().posX - posX);
					float yDiff = (float)(WMinecraft.getPlayer().posY - posY);
					float zDiff = (float)(WMinecraft.getPlayer().posZ - posZ);
					float currentDistance =
						BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
					if(Block.getIdFromBlock(block) != 0 && posY >= 0
						&& currentDistance <= range.getValueF())
					{
						if(mode.getSelected() == 1
							&& Block.getIdFromBlock(block) != id)
							continue;
						if(mode.getSelected() == 3
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
