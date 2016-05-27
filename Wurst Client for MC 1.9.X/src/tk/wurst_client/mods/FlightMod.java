/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.special.YesCheatSpf.BypassLevel;

@Info(category = Category.MOVEMENT,
	description = "Allows you to you fly.\n"
		+ "Bypasses NoCheat+ if YesCheat+ is enabled.\n"
		+ "Bypasses MAC if AntiMAC is enabled.",
	name = "Flight",
	tags = "FlyHack,fly hack,flying",
	help = "Mods/Flight")
@Bypasses(ghostMode = false, latestNCP = false)
public class FlightMod extends Mod implements UpdateListener
{
	public float speed = 1F;
	private double startY;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Speed", speed, 0.05, 5, 0.05,
			ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				speed = (float)getValue();
			}
		});
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.jetpackMod, wurst.mods.glideMod,
			wurst.mods.noFallMod, wurst.special.yesCheatSpf,
			wurst.mods.antiMacMod};
	}
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.jetpackMod.isEnabled())
			wurst.mods.jetpackMod.setEnabled(false);
		
		if(wurst.special.yesCheatSpf.getBypassLevel().ordinal() >= BypassLevel.MINEPLEX_ANTICHEAT
			.ordinal())
		{
			double startX = mc.thePlayer.posX;
			startY = mc.thePlayer.posY;
			double startZ = mc.thePlayer.posZ;
			for(int i = 0; i < 4; i++)
			{
				mc.thePlayer.sendQueue
					.addToSendQueue(new C04PacketPlayerPosition(startX,
						startY + 1.01, startZ, false));
				mc.thePlayer.sendQueue
					.addToSendQueue(new C04PacketPlayerPosition(startX, startY,
						startZ, false));
			}
			mc.thePlayer.jump();
		}
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		switch(wurst.special.yesCheatSpf.getBypassLevel())
		{
			case LATEST_NCP:
			case OLDER_NCP:
				if(!mc.thePlayer.onGround)
					if(mc.gameSettings.keyBindJump.pressed
						&& mc.thePlayer.posY < startY - 1)
						mc.thePlayer.motionY = 0.2;
					else
						mc.thePlayer.motionY = -0.02;
				break;
			
			case ANTICHEAT:
			case MINEPLEX_ANTICHEAT:
				updateMS();
				if(!mc.thePlayer.onGround)
					if(mc.gameSettings.keyBindJump.pressed && hasTimePassedS(2))
					{
						mc.thePlayer.setPosition(mc.thePlayer.posX,
							mc.thePlayer.posY + 8, mc.thePlayer.posZ);
						updateLastMS();
					}else if(mc.gameSettings.keyBindSneak.pressed)
						mc.thePlayer.motionY = -0.4;
					else
						mc.thePlayer.motionY = -0.02;
				mc.thePlayer.jumpMovementFactor = 0.04F;
				break;
			
			case OFF:
			default:
				mc.thePlayer.capabilities.isFlying = false;
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionY = 0;
				mc.thePlayer.motionZ = 0;
				mc.thePlayer.jumpMovementFactor = speed;
				
				if(mc.gameSettings.keyBindJump.pressed)
					mc.thePlayer.motionY += speed;
				if(mc.gameSettings.keyBindSneak.pressed)
					mc.thePlayer.motionY -= speed;
				break;
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
