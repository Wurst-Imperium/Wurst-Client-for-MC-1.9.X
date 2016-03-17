/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.CheckboxSetting;

@Info(category = Category.MOVEMENT,
	description = "Allows you to jump in mid-air.\n"
		+ "Looks as if you had a jetpack.",
	name = "Jetpack",
	noCheatCompatible = false)
public class JetpackMod extends Mod implements UpdateListener
{
	public final CheckboxSetting flightKickBypass = new CheckboxSetting(
		"Flight-Kick-Bypass", false);
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.flightMod.isEnabled())
			wurst.mods.flightMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void initSettings()
	{
		settings.add(flightKickBypass);
	}
	
	@Override
	public String getRenderName()
	{
		return getName()
			+ (flightKickBypass.isChecked() ? "[Kick: "
				+ (wurst.mods.flightMod.flyHeight <= 300 ? "Safe" : "Unsafe")
				+ "]" : "");
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		
		if(mc.gameSettings.keyBindJump.pressed)
			mc.thePlayer.jump();
		
		if(flightKickBypass.isChecked())
		{
			wurst.mods.flightMod.updateFlyHeight();
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayer(true));
			
			if(wurst.mods.flightMod.flyHeight <= 290 && hasTimePassedM(500)
				|| wurst.mods.flightMod.flyHeight > 290 && hasTimePassedM(100))
			{
				wurst.mods.flightMod.goToGround();
				updateLastMS();
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
