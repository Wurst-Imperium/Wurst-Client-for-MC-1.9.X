/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.ModeSetting;

@Mod.Info(description = "Changes all your hits to critical hits.",
	name = "Criticals",
	tags = "Crits",
	help = "Mods/Criticals")
@Mod.Bypasses(ghostMode = false)
public final class CriticalsMod extends Mod implements LeftClickListener
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
	public void onLeftClick(LeftClickEvent event)
	{
		if(mc.objectMouseOver != null
			&& mc.objectMouseOver.entityHit instanceof EntityLivingBase)
			doCritical();
	}
	
	public void doCritical()
	{
		if(!wurst.mods.criticalsMod.isActive())
			return;
		if(!WMinecraft.getPlayer().isInWater()
			&& !WMinecraft.getPlayer().isInsideOfMaterial(Material.lava)
			&& WMinecraft.getPlayer().onGround)
			switch(mode)
			{
				case 0:
				WMinecraft.getPlayer().motionY = 0.1F;
				WMinecraft.getPlayer().fallDistance = 0.1F;
				WMinecraft.getPlayer().onGround = false;
				break;
				case 1:
				double posX = WMinecraft.getPlayer().posX;
				double posY = WMinecraft.getPlayer().posY;
				double posZ = WMinecraft.getPlayer().posZ;
				
				WConnection
					.sendPacket(new Position(posX, posY + 0.0625D, posZ, true));
				WConnection.sendPacket(new Position(posX, posY, posZ, false));
				WConnection.sendPacket(
					new Position(posX, posY + 1.1E-5D, posZ, false));
				WConnection.sendPacket(new Position(posX, posY, posZ, false));
				break;
			}
	}
}
