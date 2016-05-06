/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;
import org.darkstorm.minecraft.gui.util.RenderUtil;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.CheckboxSetting;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.BlockUtils;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.MISC, description = "Automatically uses bone mole.", name = "AutoBone", tags = "auto bone", tutorial = "Mods/AutoBone")
public class AutoBoneMod extends Mod implements UpdateListener {

	public float normalRange = 5F;
	public float yesCheatRange = 4.25F;
	private String[] targets = new String[] { "All (all growable block)", "Sapling", "Carrot + Potato + Wheat",
			"Melon + Pumpkin", "Cocoa" };
	private CheckboxSetting[] targetsEnabled = new CheckboxSetting[targets.length];

	@Override
	public void initSettings() {
		settings.add(new SliderSetting("Range", normalRange, 1, 6, 0.05, ValueDisplay.DECIMAL) {
			@Override
			public void update() {
				normalRange = (float) getValue();
				yesCheatRange = Math.min(normalRange, 4.25F);
			}
		});

		for (int i = 0; i < targets.length; i++) {
			targetsEnabled[i] = new CheckboxSetting(targets[i], false);
			settings.add(targetsEnabled[i]);
		}
	}

	@Override
	public NavigatorItem[] getSeeAlso() {
		return new NavigatorItem[] { wurst.mods.buildRandomMod };
	}

	@Override
	public void onEnable() {
		wurst.events.add(UpdateListener.class, this);
	}

	@Override
	public void onUpdate() {
		ItemStack item = mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem);
		if (!isBoneMole(item))
			return;

		int range = (int) (wurst.mods.yesCheatMod.isActive() ? yesCheatRange : normalRange);
		BlockPos pos = mc.thePlayer.getPosition();
		for (int y = -range; y < range; y++) {
			for (int x = -range; x < range; x++) {
				for (int z = -range; z < range; z++) {
					BlockPos currentPos = pos.add(x, y, z);
					if (isCorrectBlock(currentPos, mc.theWorld.getBlockState(currentPos))) {
						BlockUtils.faceBlockPacket(currentPos);
						mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayerTryUseItem(currentPos, EnumFacing.UP,
								EnumHand.MAIN_HAND, 0.5F, 1F, 0.5F));
					}
				}
			}
		}
	}

	private boolean isCorrectBlock(BlockPos position, IBlockState blockState) {
		Block block = blockState.getBlock();

		if (block instanceof IGrowable && !(block instanceof BlockGrass)) {
			boolean flag = false;

			if (targetsEnabled[0].isChecked()) {
				flag = true;
			} else if (block instanceof BlockSapling && targetsEnabled[1].isChecked()) {
				flag = true;
			} else if (block instanceof BlockCrops && targetsEnabled[2].isChecked()) {
				flag = true;
			} else if (block instanceof BlockStem && targetsEnabled[3].isChecked()) {
				flag = true;
			} else if (block instanceof BlockCocoa && targetsEnabled[4].isChecked()) {
				flag = true;
			}

			return flag && ((IGrowable) block).canGrow(mc.theWorld, position, blockState, false);
		}

		return false;
	}

	private boolean isBoneMole(ItemStack itemStack) {
		if (itemStack == null || !(itemStack.getItem() instanceof ItemDye)) {
			return false;
		}

		return itemStack.getMetadata() == 15;
	}

	@Override
	public void onDisable() {
		wurst.events.remove(UpdateListener.class, this);
	}

}
