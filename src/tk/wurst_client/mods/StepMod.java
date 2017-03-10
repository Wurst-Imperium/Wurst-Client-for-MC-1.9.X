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
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.ModeSetting;
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
	
	public SliderSetting height = new SliderSetting("Height", 1, 1, 100, 1,
		ValueDisplay.INTEGER);
	public ModeSetting mode = new ModeSetting("Mode", new String[]{"Jump",
		"Packet"}, 1)
	{
		@Override
		public void update()
		{
			if(getSelected() == 0)
				height.lockToValue(1);
			else if(wurst.special.yesCheatSpf.getBypassLevel().ordinal() < BypassLevel.ANTICHEAT
				.ordinal())
				height.unlock();
		};
	};
	
	@Override
	public void initSettings()
	{
		settings.add(height);
		settings.add(mode);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mode.getSelected() == 0)
		{
			mc.thePlayer.stepHeight = 0.5F;
			if(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround)
				mc.thePlayer.jump();
		}else if(wurst.special.yesCheatSpf.getBypassLevel().ordinal() >= BypassLevel.ANTICHEAT
			.ordinal())
		{
			mc.thePlayer.stepHeight = 0.5F;
			if(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround)
			{
				mc.getNetHandler().addToSendQueue(
					new CPacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.42D,
						mc.thePlayer.posZ, mc.thePlayer.onGround));
				mc.getNetHandler().addToSendQueue(
					new CPacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.753D,
						mc.thePlayer.posZ, mc.thePlayer.onGround));
				mc.thePlayer.setPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 1D, mc.thePlayer.posZ);
			}
		}else
			mc.thePlayer.stepHeight = height.getValueF();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.thePlayer.stepHeight = 0.5F;
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX_ANTICHEAT:
				height.unlock();
				mode.unlock();
				break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
				height.lockToValue(1);
				mode.unlock();
				break;
			case GHOST_MODE:
				mode.lock(0);
				break;
		}
	}
}
