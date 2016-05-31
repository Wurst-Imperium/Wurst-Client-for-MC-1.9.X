/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.navigator.settings.SliderSetting.ValueDisplay;
import tk.wurst_client.special.YesCheatSpf.BypassLevel;

@Info(category = Category.MOVEMENT,
	description = "Allows you to step up full blocks.",
	name = "Step",
	help = "Mods/Step")
@Bypasses
public class StepMod extends Mod implements UpdateListener
{
	public float height = 1F;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Height", height, 1, 100, 1,
			ValueDisplay.INTEGER)
		{
			@Override
			public void update()
			{
				height = (float)getValue();
			}
		});
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.special.yesCheatSpf.getBypassLevel().ordinal() >= BypassLevel.ANTICHEAT
			.ordinal())
		{
			mc.thePlayer.stepHeight = 0.5F;
			if(mc.thePlayer.isCollidedHorizontally & mc.thePlayer.isCollidedVertically & mc.thePlayer.isCollided & mc.thePlayer.onGround)
			{
				mc.getNetHandler().addToSendQueue(new CPacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ, mc.thePlayer.onGround));
				mc.getNetHandler().addToSendQueue(new CPacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.753D, mc.thePlayer.posZ, mc.thePlayer.onGround));
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1D, mc.thePlayer.posZ);
			}
		}else
			mc.thePlayer.stepHeight = isEnabled() ? height : 0.5F;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.thePlayer.stepHeight = 0.5F;
	}
}
