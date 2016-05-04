/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Automatically sneaks all the time.",
	name = "Sneak",
	tags = "AutoSneaking",
	tutorial = "Mods/Sneak")
public class SneakMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.mods.yesCheatMod.isActive())
		{
			NetHandlerPlayClient sendQueue = mc.thePlayer.sendQueue;
			sendQueue.addToSendQueue(new CPacketEntityAction(Minecraft
				.getMinecraft().thePlayer, Action.START_SNEAKING));
			sendQueue.addToSendQueue(new CPacketEntityAction(Minecraft
				.getMinecraft().thePlayer, Action.STOP_SNEAKING));
		}else
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketEntityAction(
				Minecraft.getMinecraft().thePlayer, Action.START_SNEAKING));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindSneak.pressed = false;
		mc.thePlayer.sendQueue.addToSendQueue(new CPacketEntityAction(
			mc.thePlayer, Action.STOP_SNEAKING));
	}
}
