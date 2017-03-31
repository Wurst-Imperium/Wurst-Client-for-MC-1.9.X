/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
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
		+ "headless. Looks hilarious!",
	name = "Headless",
	tags = "head less",
	help = "Mods/Headless")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public class HeadlessMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.thePlayer.sendQueue.addToSendQueue(new C05PacketPlayerLook(
			Minecraft.getMinecraft().thePlayer.rotationYaw, 180F,
			Minecraft.getMinecraft().thePlayer.onGround));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
