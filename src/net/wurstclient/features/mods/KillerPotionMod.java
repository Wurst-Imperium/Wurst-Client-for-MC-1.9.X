/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(category = Mod.Category.EXPLOITS,
	description = "Generates a potion that can kill players in Creative mode.\n"
		+ "Requires Creative mode.",
	name = "KillerPotion",
	tags = "killer potion",
	help = "Mods/KillerPotion")
@Bypasses
public class KillerPotionMod extends Mod
{
	@Override
	public void onEnable()
	{
		if(mc.thePlayer.inventory.getStackInSlot(0) != null)
		{
			ChatUtils.error("Please clear the first slot in your hotbar.");
			setEnabled(false);
			return;
		}else if(!mc.thePlayer.capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		
		ItemStack stack = new ItemStack(Items.splash_potion);
		NBTTagList effects = new NBTTagList();
		NBTTagCompound effect = new NBTTagCompound();
		effect.setInteger("Amplifier", 125);
		effect.setInteger("Duration", 2000);
		effect.setInteger("Id", 6);
		effects.appendTag(effect);
		stack.setTagInfo("CustomPotionEffects", effects);
		stack.setStackDisplayName("§c§lKiller§6§lPotion");
		
		mc.thePlayer.sendQueue
			.addToSendQueue(new CPacketCreativeInventoryAction(36, stack));
		ChatUtils.message("Potion created.");
		setEnabled(false);
	}
}
