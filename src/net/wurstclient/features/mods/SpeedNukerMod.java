/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(description = "Faster Nuker that cannot bypass NoCheat+.",
	name = "SpeedNuker",
	tags = "FastNuker, speed nuker, fast nuker",
	help = "Mods/SpeedNuker")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class SpeedNukerMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private static Block currentBlock;
	private BlockPos pos;
	private int oldSlot = -1;
	
	public CheckboxSetting useNuker =
		new CheckboxSetting("Use Nuker settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					NukerMod nuker = wurst.mods.nukerMod;
					range.lockToValue(nuker.range.getValue());
					mode.lock(nuker.mode.getSelected());
				}else
				{
					range.unlock();
					mode.unlock();
				}
			};
		};
	public final SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public final ModeSetting mode = new ModeSetting("Mode",
		new String[]{"Normal", "ID", "Flat", "Smash"}, 0);
	
	@Override
	public void initSettings()
	{
		settings.add(useNuker);
		settings.add(range);
		settings.add(mode);
	}
	
	@Override
	public String getRenderName()
	{
		switch(mode.getSelected())
		{
			case 0:
			return "SpeedNuker";
			case 1:
			return "IDSpeedNuker [" + NukerMod.id + "]";
			default:
			return mode.getSelectedMode() + "SpeedNuker";
		}
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.nukerLegitMod,
			wurst.mods.tunnellerMod, wurst.mods.fastBreakMod,
			wurst.mods.autoMineMod};
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
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error(getName() + " doesn't work in creative mode.");
			setEnabled(false);
			ChatUtils
				.message("Switching to " + wurst.mods.nukerMod.getName() + ".");
			wurst.mods.nukerMod.setEnabled(true);
			return;
		}
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
		pos = newPos;
		currentBlock = WMinecraft.getWorld().getBlockState(pos).getBlock();
		if(wurst.mods.autoToolMod.isActive() && oldSlot == -1)
			oldSlot = WMinecraft.getPlayer().inventory.currentItem;
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode
			&& wurst.mods.autoToolMod.isActive()
			&& currentBlock.getPlayerRelativeBlockHardness(
				WMinecraft.getWorld().getBlockState(pos),
				WMinecraft.getPlayer(), WMinecraft.getWorld(), pos) < 1)
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
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		NukerMod.id = 0;
		ConfigFiles.OPTIONS.save();
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
			NukerMod.id = Block.getIdFromBlock(WMinecraft.getWorld()
				.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
			ConfigFiles.OPTIONS.save();
		}
	}
	
	private BlockPos find()
	{
		BlockPos closest = null;
		float closestDistance = range.getValueF() + 1;
		int nukerMode = mode.getSelected();
		for(int y = (int)range.getValueF(); y >= (nukerMode == 2 ? 0
			: -range.getValueF()); y--)
			for(int x = (int)range.getValueF(); x >= -range.getValueF()
				- 1; x--)
				for(int z =
					(int)range.getValueF(); z >= -range.getValueF(); z--)
				{
					if(WMinecraft.getPlayer() == null)
						continue;
					if(x == 0 && y == -1 && z == 0)
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
					float currentDistance =
						BlockUtils.getBlockDistance(xDiff, yDiff, zDiff);
					if(Block.getIdFromBlock(block) != 0 && posY >= 0
						&& currentDistance <= range.getValueF())
					{
						if(nukerMode == 1
							&& Block.getIdFromBlock(block) != NukerMod.id)
							continue;
						if(nukerMode == 3
							&& block.getPlayerRelativeBlockHardness(
								WMinecraft.getWorld().getBlockState(blockPos),
								WMinecraft.getPlayer(), WMinecraft.getWorld(),
								blockPos) < 1)
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
		int nukerMode = mode.getSelected();
		for(int y = (int)range.getValueF(); y >= (nukerMode == 2 ? 0
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
					if(x == 0 && y == -1 && z == 0)
						continue;
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
						if(nukerMode == 1
							&& Block.getIdFromBlock(block) != NukerMod.id)
							continue;
						if(nukerMode == 3
							&& block.getPlayerRelativeBlockHardness(
								WMinecraft.getWorld().getBlockState(blockPos),
								WMinecraft.getPlayer(), WMinecraft.getWorld(),
								blockPos) < 1)
							continue;
						if(!WMinecraft.getPlayer().onGround)
							continue;
						EnumFacing side = mc.objectMouseOver.sideHit;
						WConnection.sendPacket(new CPacketPlayerDigging(
							Action.START_DESTROY_BLOCK, blockPos, side));
						WConnection.sendPacket(new CPacketPlayerDigging(
							Action.STOP_DESTROY_BLOCK, blockPos, side));
					}
				}
	}
}
