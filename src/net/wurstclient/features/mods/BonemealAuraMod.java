/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Automatically uses bone meal on specific types of plants.\n"
		+ "Use the checkboxes to specify the types of plants.",
	name = "BonemealAura",
	tags = "bonemeal aura, bone meal aura, AutoBone, auto bone",
	help = "Mods/BonemealAura")
@Mod.Bypasses
public final class BonemealAuraMod extends Mod implements UpdateListener
{
	public final SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final CheckboxSetting saplings =
		new CheckboxSetting("Saplings", true);
	private final CheckboxSetting crops = new CheckboxSetting("Crops", true);
	private final CheckboxSetting stems = new CheckboxSetting("Stems", true);
	private final CheckboxSetting cocoa = new CheckboxSetting("Cocoa", true);
	private final CheckboxSetting other = new CheckboxSetting("Other", false);
	
	@Override
	public void initSettings()
	{
		settings.add(range);
		settings.add(saplings);
		settings.add(crops);
		settings.add(stems);
		settings.add(cocoa);
		settings.add(other);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// wait for right click timer
		if(mc.rightClickDelayTimer > 0)
			return;
		
		// check held item
		ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(InventoryUtils.isEmptySlot(stack)
			|| !(stack.getItem() instanceof ItemDye)
			|| stack.getMetadata() != 15)
			return;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks = BlockUtils
			.getValidBlocks(range.getValue(), (p) -> isCorrectBlock(p));
		
		// check bypass level
		if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() > BypassLevel.MINEPLEX.ordinal())
		{
			// use bone meal on next valid block
			for(BlockPos pos : validBlocks)
				if(BlockUtils.rightClickBlockLegit(pos))
					break;
				
		}else
		{
			boolean shouldSwing = false;
			
			// use bone meal on all valid blocks
			for(BlockPos pos : validBlocks)
				if(BlockUtils.rightClickBlockSimple(pos))
					shouldSwing = true;
				
			// swing arm
			if(shouldSwing)
				WPlayer.swingArmClient();
		}
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
			range.resetUsableMax();
			break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			range.setUsableMax(4.25);
			break;
		}
	}
	
	private boolean isCorrectBlock(BlockPos pos)
	{
		Block block = WBlock.getBlock(pos);
		
		if(!(block instanceof IGrowable) || block instanceof BlockGrass
			|| !((IGrowable)block).canGrow(WMinecraft.getWorld(), pos,
				WBlock.getState(pos), false))
			return false;
		
		if(block instanceof BlockSapling)
			return saplings.isChecked();
		else if(block instanceof BlockCrops)
			return crops.isChecked();
		else if(block instanceof BlockStem)
			return stems.isChecked();
		else if(block instanceof BlockCocoa)
			return cocoa.isChecked();
		else
			return other.isChecked();
	}
}
