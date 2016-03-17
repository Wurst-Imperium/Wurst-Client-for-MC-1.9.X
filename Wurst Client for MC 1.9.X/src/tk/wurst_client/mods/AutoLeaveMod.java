/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.ModeSetting;

@Info(category = Category.COMBAT,
	description = "Automatically leaves the server when your health is low.\n"
		+ "The Chars, TP and SelfHurt modes can bypass CombatLog and similar plugins.",
	name = "AutoLeave")
public class AutoLeaveMod extends Mod implements UpdateListener
{
	private int mode = 0;
	private String[] modes = new String[]{"Quit", "Chars", "TP", "SelfHurt"};
	
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
		if(mc.thePlayer.getHealth() <= 8.0
			&& !mc.thePlayer.capabilities.isCreativeMode
			&& (!mc.isIntegratedServerRunning() || Minecraft.getMinecraft().thePlayer.sendQueue
				.getPlayerInfoMap().size() > 1))
		{
			switch(mode)
			{
				case 0:
					mc.theWorld.sendQuittingDisconnectingPacket();
					break;
				case 1:
					mc.thePlayer.sendQueue
						.addToSendQueue(new CPacketChatMessage("§"));
					break;
				case 2:
					mc.thePlayer.sendQueue
						.addToSendQueue(new C04PacketPlayerPosition(
							3.1e7d, 100, 3.1e7d, false));
					break;
				case 3:
					mc.thePlayer.sendQueue
						.addToSendQueue(new CPacketUseEntity(mc.thePlayer,
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
