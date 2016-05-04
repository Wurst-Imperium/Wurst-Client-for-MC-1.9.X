/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.EntityUtils;

@Mod.Info(category = Mod.Category.COMBAT,
	description = "Automatically attacks the closest valid entity while teleporting around it.",
	name = "TP-Aura",
	noCheatCompatible = false,
	tags = "TpAura, EnderAura, tp aura, ender aura",
	tutorial = "Mods/TP-Aura")
public class TpAuraMod extends Mod implements UpdateListener
{
	private Random random = new Random();
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.special.targetSpf,
			wurst.mods.killauraMod, wurst.mods.killauraLegitMod,
			wurst.mods.multiAuraMod, wurst.mods.clickAuraMod,
			wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		// TODO: Clean up this mess!
		if(wurst.mods.killauraMod.isEnabled())
			wurst.mods.killauraMod.setEnabled(false);
		if(wurst.mods.killauraLegitMod.isEnabled())
			wurst.mods.killauraLegitMod.setEnabled(false);
		if(wurst.mods.multiAuraMod.isEnabled())
			wurst.mods.multiAuraMod.setEnabled(false);
		if(wurst.mods.clickAuraMod.isEnabled())
			wurst.mods.clickAuraMod.setEnabled(false);
		if(wurst.mods.triggerBotMod.isEnabled())
			wurst.mods.triggerBotMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		EntityLivingBase en = EntityUtils.getClosestEntity(true, true);
		if(en == null)
		{
			EntityUtils.lookChanged = false;
			return;
		}
		EntityUtils.lookChanged = true;
		if(mc.thePlayer.getDistanceToEntity(en) <= wurst.mods.killauraMod.realRange
			&& hasTimePassedS(wurst.mods.killauraMod.realSpeed))
		{
			mc.thePlayer.setPosition(en.posX + random.nextInt(3) * 2 - 2,
				en.posY, en.posZ + random.nextInt(3) * 2 - 2);
			
			if(!wurst.mods.killauraMod.useCooldown.isChecked()
				|| mc.thePlayer.getSwordCooldown(0F) >= 1F)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				wurst.mods.blockHitMod.doBlock();
				EntityUtils.faceEntityPacket(en);
				
				mc.playerController.attackEntity(mc.thePlayer, en);
				mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
				
				mc.thePlayer.resetSwordCooldown();
			}
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		EntityUtils.lookChanged = false;
	}
}
