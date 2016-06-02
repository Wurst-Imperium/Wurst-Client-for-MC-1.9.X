/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.MathHelper;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.CheckboxSetting;

@Info(category = Category.MOVEMENT,
	description = "Eases use of elytra.",
	name = "ExtraElytra",
	tags = "extra elytra",
	help = "Mods/ExtraElytra")
public class ExtraElytraMod extends Mod implements UpdateListener
{
	private CheckboxSetting stopInWater = new CheckboxSetting(
		"Stop fly in water", true);
	private CheckboxSetting instantFly = new CheckboxSetting("Instant fly",
		true);
	private CheckboxSetting easyFly = new CheckboxSetting("Easy fly", true);
	
	@Override
	public void initSettings()
	{
		settings.add(easyFly);
		settings.add(stopInWater);
		settings.add(instantFly);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		
		ItemStack itemstack =
			mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		
		if(itemstack == null || itemstack.getItem() != Items.elytra)
			return;
		
		if(mc.thePlayer.func_184613_cA())
		{
			if(stopInWater.isChecked() && mc.thePlayer.isInWater())
				toggleElytra();
			
			if(easyFly.isChecked())
				if(mc.gameSettings.keyBindJump.pressed)
					mc.thePlayer.motionY += 0.08;
				else if(mc.gameSettings.keyBindSneak.pressed)
					mc.thePlayer.motionY -= 0.04;
				else if(mc.gameSettings.keyBindForward.pressed
					&& mc.thePlayer.getPosition().getY() < 256)
				{
					mc.thePlayer.motionX -=
						MathHelper.sin(mc.thePlayer.rotationYaw * 0.017453292F) * 0.05F;
					mc.thePlayer.motionZ +=
						MathHelper.cos(mc.thePlayer.rotationYaw * 0.017453292F) * 0.05F;
				}else if(mc.gameSettings.keyBindBack.pressed
					&& mc.thePlayer.getPosition().getY() < 256)
				{
					mc.thePlayer.motionX +=
						MathHelper.sin(mc.thePlayer.rotationYaw * 0.017453292F) * 0.05F;
					mc.thePlayer.motionZ -=
						MathHelper.cos(mc.thePlayer.rotationYaw * 0.017453292F) * 0.05F;
				}
		}else if(instantFly.isChecked() && ItemElytra.isBroken(itemstack)
			&& mc.gameSettings.keyBindJump.pressed)
		{
			if(hasTimePassedM(1000))
			{
				updateLastMS();
				mc.thePlayer.setJumping(false);
				mc.thePlayer.setSprinting(true);
				mc.thePlayer.jump();
			}
			toggleElytra();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	private void toggleElytra()
	{
		mc.thePlayer.sendQueue.addToSendQueue(new CPacketEntityAction(
			mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
	}
}
