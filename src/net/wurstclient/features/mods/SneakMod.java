/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;

@Info(category = Category.MOVEMENT,
	description = "Automatically sneaks all the time.",
	name = "Sneak",
	tags = "AutoSneaking",
	help = "Mods/Sneak")
@Bypasses(ghostMode = false)
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
		if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() >= BypassLevel.OLDER_NCP.ordinal())
		{
			NetHandlerPlayClient sendQueue = WMinecraft.getPlayer().sendQueue;
			sendQueue.addToSendQueue(new CPacketEntityAction(
				WMinecraft.getPlayer(), Action.START_SNEAKING));
			sendQueue.addToSendQueue(new CPacketEntityAction(
				WMinecraft.getPlayer(), Action.STOP_SNEAKING));
		}else
			WMinecraft.getPlayer().sendQueue
				.addToSendQueue(new CPacketEntityAction(WMinecraft.getPlayer(),
					Action.START_SNEAKING));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindSneak.pressed = false;
		WMinecraft.getPlayer().sendQueue.addToSendQueue(new CPacketEntityAction(
			WMinecraft.getPlayer(), Action.STOP_SNEAKING));
	}
}
