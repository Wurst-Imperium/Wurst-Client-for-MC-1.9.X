/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.CheckboxSetting;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.special.YesCheatSpf.BypassLevel;
import tk.wurst_client.utils.BlockUtils;

@Info(category = Category.MISC,
	description = "Automatically uses bone meal on specific types of plants.\n"
		+ "Use the checkboxes to specify the types of plants.",
	name = "BonemealAura",
	tags = "bonemeal aura, bone meal aura, AutoBone, auto bone",
	help = "Mods/BonemealAura")
@Bypasses(ghostMode = false)
public class BonemealAuraMod extends Mod implements UpdateListener
{
	public float normalRange = 5F;
	public float yesCheatRange = 4.25F;
	private final CheckboxSetting saplings = new CheckboxSetting("Saplings",
		true);
	private final CheckboxSetting crops = new CheckboxSetting(
		"Carrots, Potatoes & Wheat", true);
	private final CheckboxSetting stems = new CheckboxSetting(
		"Melons & Pumpkins", true);
	private final CheckboxSetting cocoa = new CheckboxSetting("Cocoa", true);
	private final CheckboxSetting other = new CheckboxSetting("Other", false);
	
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
	public void onUpdate()
	{
		ItemStack item =
			mc.thePlayer.inventory
				.getStackInSlot(mc.thePlayer.inventory.currentItem);
		if(item == null || !(item.getItem() instanceof ItemDye)
			|| item.getMetadata() != 15)
			return;
		
		float range =
			wurst.special.yesCheatSpf.getBypassLevel().ordinal() >= BypassLevel.ANTICHEAT
				.ordinal() ? yesCheatRange : normalRange;
		BlockPos pos = mc.thePlayer.getPosition();
		for(int y = (int)-range - 1; y < (int)range + 1; y++)
			for(int x = (int)-range - 1; x < (int)range + 1; x++)
				for(int z = (int)-range - 1; z < (int)range + 1; z++)
				{
					BlockPos currentPos = pos.add(x, y, z);
					if(BlockUtils.getPlayerBlockDistance(currentPos) > range
						|| !isCorrectBlock(currentPos))
						continue;
					
					BlockUtils.faceBlockPacket(currentPos);
					mc.thePlayer.sendQueue
						.addToSendQueue(new CPacketPlayerTryUseItem(currentPos,
							EnumFacing.UP, EnumHand.MAIN_HAND, 0.5F, 1F, 0.5F));
				}
	}
	
	private boolean isCorrectBlock(BlockPos pos)
	{
		IBlockState state = mc.theWorld.getBlockState(pos);
		Block block = state.getBlock();
		
		if(!(block instanceof IGrowable) || block instanceof BlockGrass
			|| !((IGrowable)block).canGrow(mc.theWorld, pos, state, false))
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
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
