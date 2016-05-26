/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer.C05PacketPlayerLook;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

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
		mc.thePlayer.sendQueue
			.addToSendQueue(new C05PacketPlayerLook(Minecraft
				.getMinecraft().thePlayer.rotationYaw, 180F, Minecraft
				.getMinecraft().thePlayer.onGround));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
