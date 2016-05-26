/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

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

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.events.listeners.LeftClickListener;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.ModeSetting;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.BlockUtils;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.BLOCKS,
	description = "Destroys blocks around you.\n"
		+ "Use .nuker mode <mode> to change the mode.",
	name = "Nuker",
	help = "Mods/Nuker")
@Bypasses
public class NukerMod extends Mod implements LeftClickListener, RenderListener,
	UpdateListener
{
	public float normalRange = 5F;
	public float yesCheatRange = 4.25F;
	private float realRange;
	private static Block currentBlock;
	private float currentDamage;
	private EnumFacing side = EnumFacing.UP;
	private byte blockHitDelay = 0;
	public static int id = 0;
	private BlockPos pos;
	private boolean shouldRenderESP;
	private int oldSlot = -1;
	private int mode = 0;
	private String[] modes = new String[]{"Normal", "ID", "Flat", "Smash"};
	
	@Override
	public String getRenderName()
	{
		switch(mode)
		{
			case 0:
				return "Nuker";
			case 1:
				return "IDNuker [" + id + "]";
			default:
				return modes[mode] + "Nuker";
		}
	}
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Range", normalRange, 1, 6, 0.05,
			ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				normalRange = (float)getValue();
				yesCheatRange = Math.min(normalRange, 4.25F);
			}
		});
		settings.add(new ModeSetting("Mode", modes, mode)
		{
			@Override
			public void update()
			{
				mode = getSelected();
			}
		});
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.nukerLegitMod,
			wurst.mods.speedNukerMod, wurst.mods.tunnellerMod,
			wurst.mods.fastBreakMod, wurst.mods.autoMineMod,
			wurst.mods.overlayMod};
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
	public void onRender()
	{
		if(blockHitDelay == 0 && shouldRenderESP)
			if(!mc.thePlayer.capabilities.isCreativeMode
				&& currentBlock.getPlayerRelativeBlockHardness(
					mc.theWorld.getBlockState(pos), mc.thePlayer, mc.theWorld,
					pos) < 1)
				RenderUtils.nukerBox(pos, currentDamage);
			else
				RenderUtils.nukerBox(pos, 1);
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.mods.yesCheatMod.isActive())
			realRange = yesCheatRange;
		else
			realRange = normalRange;
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
			if(mc.thePlayer.capabilities.isCreativeMode
				|| currentBlock.getPlayerRelativeBlockHardness(
					mc.theWorld.getBlockState(pos), mc.thePlayer, mc.theWorld,
					pos) >= 1)
			{
				currentDamage = 0;
				if(mc.thePlayer.capabilities.isCreativeMode
					&& !wurst.mods.yesCheatMod.isActive())
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
		mc.thePlayer.sendQueue.addToSendQueue(new CPacketAnimation(
			EnumHand.MAIN_HAND));
		shouldRenderESP = true;
		BlockUtils.faceBlockPacket(pos);
		currentDamage +=
			currentBlock.getPlayerRelativeBlockHardness(
				mc.theWorld.getBlockState(pos), mc.thePlayer, mc.theWorld, pos)
				* (wurst.mods.fastBreakMod.isActive()
					&& wurst.mods.fastBreakMod.getMode() == 0
					? wurst.mods.fastBreakMod.speed : 1);
		mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos,
			(int)(currentDamage * 10.0F) - 1);
		if(currentDamage >= 1)
		{
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayerDigging(
				Action.STOP_DESTROY_BLOCK, pos, side));
			mc.playerController.onPlayerDestroyBlock(pos);
			blockHitDelay = (byte)4;
			currentDamage = 0;
		}else if(wurst.mods.fastBreakMod.isActive()
			&& wurst.mods.fastBreakMod.getMode() == 1)
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayerDigging(
				Action.STOP_DESTROY_BLOCK, pos, side));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		if(oldSlot != -1)
		{
			mc.thePlayer.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		currentDamage = 0;
		shouldRenderESP = false;
		id = 0;
		wurst.files.saveOptions();
	}
	
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(mode == 1
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.air)
		{
			id =
				Block.getIdFromBlock(mc.theWorld.getBlockState(
					mc.objectMouseOver.getBlockPos()).getBlock());
			wurst.files.saveOptions();
		}
	}
	
	private BlockPos find()
	{
		LinkedList<BlockPos> queue = new LinkedList<BlockPos>();
		HashSet<BlockPos> alreadyProcessed = new HashSet<BlockPos>();
		queue.add(new BlockPos(mc.thePlayer));
		while(!queue.isEmpty())
		{
			BlockPos currentPos = queue.poll();
			if(alreadyProcessed.contains(currentPos))
				continue;
			alreadyProcessed.add(currentPos);
			if(BlockUtils.getPlayerBlockDistance(currentPos) > realRange)
				continue;
			int currentID =
				Block.getIdFromBlock(mc.theWorld.getBlockState(currentPos)
					.getBlock());
			if(currentID != 0)
				switch(mode)
				{
					case 1:
						if(currentID == id)
							return currentPos;
						break;
					case 2:
						if(currentPos.getY() >= mc.thePlayer.posY)
							return currentPos;
						break;
					case 3:
						if(mc.theWorld
							.getBlockState(currentPos)
							.getBlock()
							.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(pos), mc.thePlayer,
								mc.theWorld, currentPos) >= 1)
							return currentPos;
						break;
					default:
						return currentPos;
				}
			if(!wurst.mods.yesCheatMod.isActive()
				|| !mc.theWorld.getBlockState(currentPos).getBlock()
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
		for(int y = (int)realRange; y >= (mode == 2 ? 0 : -realRange); y--)
			for(int x = (int)realRange; x >= -realRange - 1; x--)
				for(int z = (int)realRange; z >= -realRange; z--)
				{
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
						&& currentDistance <= realRange)
					{
						if(mode == 1 && Block.getIdFromBlock(block) != id)
							continue;
						if(mode == 3
							&& block.getPlayerRelativeBlockHardness(
								mc.theWorld.getBlockState(blockPos),
								mc.thePlayer, mc.theWorld, blockPos) < 1)
							continue;
						side = mc.objectMouseOver.sideHit;
						shouldRenderESP = true;
						BlockUtils.faceBlockPacket(pos);
						mc.thePlayer.sendQueue
							.addToSendQueue(new CPacketPlayerDigging(
								Action.START_DESTROY_BLOCK, blockPos, side));
						block.onBlockDestroyedByPlayer(mc.theWorld, blockPos,
							mc.theWorld.getBlockState(blockPos));
					}
				}
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void setMode(int mode)
	{
		((ModeSetting)settings.get(1)).setSelected(mode);
	}
	
	public String[] getModes()
	{
		return modes;
	}
}
