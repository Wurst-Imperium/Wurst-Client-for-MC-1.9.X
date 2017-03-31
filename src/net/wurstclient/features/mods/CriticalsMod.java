/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.material.Material;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.settings.ModeSetting;

@Info(category = Category.COMBAT,
	description = "Changes all your hits to critical hits.",
	name = "Criticals",
	tags = "Crits",
	help = "Mods/Criticals")
@Bypasses(ghostMode = false)
public class CriticalsMod extends Mod implements LeftClickListener
{
	private int mode = 1;
	private String[] modes = new String[]{"Jump", "Packet"};
	
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
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.killauraMod, wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver != null
			&& mc.objectMouseOver.entityHit instanceof EntityLivingBase)
			doCritical();
	}
	
	public void doCritical()
	{
		if(!wurst.mods.criticalsMod.isActive())
			return;
		if(!mc.thePlayer.isInWater()
			&& !mc.thePlayer.isInsideOfMaterial(Material.lava)
			&& mc.thePlayer.onGround)
		{
			switch(mode)
			{
				case 0:
				mc.thePlayer.motionY = 0.1F;
				mc.thePlayer.fallDistance = 0.1F;
				mc.thePlayer.onGround = false;
				break;
				case 1:
				double posX = mc.thePlayer.posX;
				double posY = mc.thePlayer.posY;
				double posZ = mc.thePlayer.posZ;
				NetHandlerPlayClient sendQueue = mc.thePlayer.sendQueue;
				
				sendQueue.addToSendQueue(new C04PacketPlayerPosition(posX,
					posY + 0.0625D, posZ, true));
				sendQueue.addToSendQueue(
					new C04PacketPlayerPosition(posX, posY, posZ, false));
				sendQueue.addToSendQueue(new C04PacketPlayerPosition(posX,
					posY + 1.1E-5D, posZ, false));
				sendQueue.addToSendQueue(
					new C04PacketPlayerPosition(posX, posY, posZ, false));
				break;
			}
			
		}
	}
}
