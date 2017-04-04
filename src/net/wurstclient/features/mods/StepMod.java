/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(description = "Allows you to step up full blocks.",
	name = "Step",
	help = "Mods/Step")
@Mod.Bypasses
public final class StepMod extends Mod implements UpdateListener
{
	
	public SliderSetting height =
		new SliderSetting("Height", 1, 1, 100, 1, ValueDisplay.INTEGER);
	public ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Jump", "Packet"}, 1)
		{
			@Override
			public void update()
			{
				if(getSelected() == 0)
					height.lockToValue(1);
				else if(wurst.special.yesCheatSpf.getBypassLevel()
					.ordinal() < BypassLevel.ANTICHEAT.ordinal())
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
			WMinecraft.getPlayer().stepHeight = 0.5F;
			if(WMinecraft.getPlayer().isCollidedHorizontally
				&& WMinecraft.getPlayer().onGround)
				WMinecraft.getPlayer().jump();
		}else if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() >= BypassLevel.ANTICHEAT.ordinal())
		{
			WMinecraft.getPlayer().stepHeight = 0.5F;
			if(WMinecraft.getPlayer().isCollidedHorizontally
				&& WMinecraft.getPlayer().onGround)
			{
				mc.getNetHandler()
					.addToSendQueue(new CPacketPlayer.C04PacketPlayerPosition(
						WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 0.42D,
						WMinecraft.getPlayer().posZ,
						WMinecraft.getPlayer().onGround));
				mc.getNetHandler()
					.addToSendQueue(new CPacketPlayer.C04PacketPlayerPosition(
						WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 0.753D,
						WMinecraft.getPlayer().posZ,
						WMinecraft.getPlayer().onGround));
				WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX,
					WMinecraft.getPlayer().posY + 1D,
					WMinecraft.getPlayer().posZ);
			}
		}else
			WMinecraft.getPlayer().stepHeight = height.getValueF();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		WMinecraft.getPlayer().stepHeight = 0.5F;
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
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
