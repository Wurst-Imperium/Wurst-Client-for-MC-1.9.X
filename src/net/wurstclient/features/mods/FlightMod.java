/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(
	description = "Allows you to you fly.\n"
		+ "Bypasses NoCheat+ if YesCheat+ is enabled.\n"
		+ "Bypasses MAC if AntiMAC is enabled.",
	name = "Flight",
	tags = "FlyHack,fly hack,flying",
	help = "Mods/Flight")
@Mod.Bypasses(ghostMode = false, latestNCP = false)
public final class FlightMod extends Mod implements UpdateListener
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
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.boatFlyMod, wurst.mods.extraElytraMod,
			wurst.mods.jetpackMod, wurst.mods.glideMod, wurst.mods.noFallMod,
			wurst.special.yesCheatSpf};
	}
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.jetpackMod.isEnabled())
			wurst.mods.jetpackMod.setEnabled(false);
		
		if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() >= BypassLevel.MINEPLEX.ordinal())
		{
			double startX = WMinecraft.getPlayer().posX;
			startY = WMinecraft.getPlayer().posY;
			double startZ = WMinecraft.getPlayer().posZ;
			for(int i = 0; i < 4; i++)
			{
				WConnection.sendPacket(new C04PacketPlayerPosition(startX,
					startY + 1.01, startZ, false));
				WConnection.sendPacket(
					new C04PacketPlayerPosition(startX, startY, startZ, false));
			}
			WMinecraft.getPlayer().jump();
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
			if(!WMinecraft.getPlayer().onGround)
				if(mc.gameSettings.keyBindJump.pressed
					&& WMinecraft.getPlayer().posY < startY - 1)
					WMinecraft.getPlayer().motionY = 0.2;
				else
					WMinecraft.getPlayer().motionY = -0.02;
			break;
			
			case ANTICHEAT:
			case MINEPLEX:
			updateMS();
			if(!WMinecraft.getPlayer().onGround)
				if(mc.gameSettings.keyBindJump.pressed && hasTimePassedS(2))
				{
					WMinecraft.getPlayer().setPosition(
						WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 8,
						WMinecraft.getPlayer().posZ);
					updateLastMS();
				}else if(mc.gameSettings.keyBindSneak.pressed)
					WMinecraft.getPlayer().motionY = -0.4;
				else
					WMinecraft.getPlayer().motionY = -0.02;
			WMinecraft.getPlayer().jumpMovementFactor = 0.04F;
			break;
			
			case OFF:
			default:
			WMinecraft.getPlayer().capabilities.isFlying = false;
			WMinecraft.getPlayer().motionX = 0;
			WMinecraft.getPlayer().motionY = 0;
			WMinecraft.getPlayer().motionZ = 0;
			WMinecraft.getPlayer().jumpMovementFactor = speed;
			
			if(mc.gameSettings.keyBindJump.pressed)
				WMinecraft.getPlayer().motionY += speed;
			if(mc.gameSettings.keyBindSneak.pressed)
				WMinecraft.getPlayer().motionY -= speed;
			break;
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
