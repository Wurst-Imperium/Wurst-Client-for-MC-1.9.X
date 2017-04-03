/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.utils.EntityUtils;

@Mod.Info(
	description = "Pushes mobs like crazy.\n" + "They'll literally fly away!\n"
		+ "Can sometimes get you kicked for \"Flying is not enabled\".",
	name = "ForcePush",
	tags = "force push",
	help = "Mods/ForcePush")
@Bypasses
public class ForcePushMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		EntityLivingBase en = EntityUtils.getClosestEntity(true, 360, false);
		if(WMinecraft.getPlayer().onGround && en != null
			&& en.getDistanceToEntity(WMinecraft.getPlayer()) < 1)
			for(int i = 0; i < 1000; i++)
				WMinecraft.getPlayer().sendQueue
					.addToSendQueue(new CPacketPlayer(true));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
