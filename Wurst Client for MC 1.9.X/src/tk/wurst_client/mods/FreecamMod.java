/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.RENDER,
	description = "Allows you to fly out of your body.\n"
		+ "Looks similar to spectator mode.",
	name = "Freecam",
	tags = "free cam, spectator",
	tutorial = "Mods/Freecam")
public class FreecamMod extends Mod implements UpdateListener
{
	private EntityOtherPlayerMP fakePlayer = null;
	private double oldX;
	private double oldY;
	private double oldZ;
	
	@Override
	public void onEnable()
	{
		oldX = mc.thePlayer.posX;
		oldY = mc.thePlayer.posY;
		oldZ = mc.thePlayer.posZ;
		fakePlayer =
			new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
		fakePlayer.clonePlayer(mc.thePlayer, true);
		fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
		fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
		mc.theWorld.addEntityToWorld(-69, fakePlayer);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.jumpMovementFactor = wurst.mods.flightMod.speed / 10;
		if(mc.gameSettings.keyBindJump.pressed)
			mc.thePlayer.motionY += wurst.mods.flightMod.speed;
		if(mc.gameSettings.keyBindSneak.pressed)
			mc.thePlayer.motionY -= wurst.mods.flightMod.speed;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ,
			mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		mc.theWorld.removeEntityFromWorld(-69);
		fakePlayer = null;
		mc.renderGlobal.loadRenderers();
	}
}
