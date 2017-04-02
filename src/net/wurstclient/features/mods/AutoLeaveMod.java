/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.settings.ModeSetting;

@Info(category = Category.COMBAT,
	description = "Automatically leaves the server when your health is low.\n"
		+ "The Chars, TP and SelfHurt modes can bypass CombatLog and similar plugins.",
	name = "AutoLeave",
	tags = "AutoDisconnect, auto leave, auto disconnect",
	help = "Mods/AutoLeave")
@Bypasses
public class AutoLeaveMod extends Mod implements UpdateListener
{
	private int mode = 0;
	private String[] modes = new String[]{"Quit", "Chars", "TP", "SelfHurt"};
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.commands.leaveCmd};
	}
	
	@Override
	public String getRenderName()
	{
		String name = getName() + "[" + modes[mode] + "]";
		return name;
	}
	
	@Override
	public void initSettings()
	{
		settings.add(new ModeSetting("Mode", modes, mode)
		{
			@Override
			public void update()
			{
				mode = getSelected();
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
		if(WMinecraft.getPlayer().getHealth() <= 8.0
			&& !WMinecraft.getPlayer().capabilities.isCreativeMode
			&& (!mc.isIntegratedServerRunning()
				|| WMinecraft.getPlayer().sendQueue.getPlayerInfoMap()
					.size() > 1))
		{
			switch(mode)
			{
				case 0:
				WMinecraft.getWorld().sendQuittingDisconnectingPacket();
				break;
				case 1:
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketChatMessage("§"));
				break;
				case 2:
				WMinecraft.getPlayer().sendQueue.addToSendQueue(
					new C04PacketPlayerPosition(3.1e7d, 100, 3.1e7d, false));
				break;
				case 3:
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketUseEntity(WMinecraft.getPlayer(),
						EnumHand.MAIN_HAND));
				break;
				default:
				break;
			}
			setEnabled(false);
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void setMode(int mode)
	{
		((ModeSetting)settings.get(1)).setSelected(mode);
	}
	
	public String[] getModes()
	{
		return modes;
	}
}
