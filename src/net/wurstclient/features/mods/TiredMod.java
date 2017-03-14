/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer.C05PacketPlayerLook;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.FUN,
	description = "While this is active, other people will think you are\n"
		+ "extremely tired and about to fall asleep!",
	name = "Tired",
	help = "Mods/Tired")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public class TiredMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.thePlayer.sendQueue.addToSendQueue(new C05PacketPlayerLook(Minecraft
			.getMinecraft().thePlayer.rotationYaw,
			mc.thePlayer.ticksExisted % 100, mc.thePlayer.onGround));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
