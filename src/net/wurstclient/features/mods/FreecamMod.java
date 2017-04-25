/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(tags = "free cam, spectator", help = "Mods/Freecam")
@Mod.Bypasses
@Mod.DontSaveState
public final class FreecamMod extends Mod implements UpdateListener
{
	private EntityOtherPlayerMP fakePlayer = null;
	private double oldX;
	private double oldY;
	private double oldZ;
	
	public FreecamMod()
	{
		super("Freecam", "Allows you to fly out of your body.\n"
			+ "Looks similar to spectator mode.");
	}
	
	@Override
	public void onEnable()
	{
		oldX = WMinecraft.getPlayer().posX;
		oldY = WMinecraft.getPlayer().posY;
		oldZ = WMinecraft.getPlayer().posZ;
		fakePlayer = new EntityOtherPlayerMP(WMinecraft.getWorld(),
			WMinecraft.getPlayer().getGameProfile());
		fakePlayer.clonePlayer(WMinecraft.getPlayer(), true);
		fakePlayer.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
		fakePlayer.rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
		WMinecraft.getWorld().addEntityToWorld(-69, fakePlayer);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		WMinecraft.getPlayer().motionX = 0;
		WMinecraft.getPlayer().motionY = 0;
		WMinecraft.getPlayer().motionZ = 0;
		WMinecraft.getPlayer().jumpMovementFactor =
			wurst.mods.flightMod.speed / 10;
		if(mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().motionY += wurst.mods.flightMod.speed;
		if(mc.gameSettings.keyBindSneak.pressed)
			WMinecraft.getPlayer().motionY -= wurst.mods.flightMod.speed;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		WMinecraft.getPlayer().setPositionAndRotation(oldX, oldY, oldZ,
			WMinecraft.getPlayer().rotationYaw,
			WMinecraft.getPlayer().rotationPitch);
		WMinecraft.getWorld().removeEntityFromWorld(-69);
		fakePlayer = null;
		mc.renderGlobal.loadRenderers();
	}
}
