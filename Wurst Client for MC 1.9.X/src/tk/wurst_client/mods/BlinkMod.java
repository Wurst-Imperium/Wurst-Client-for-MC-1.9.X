/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Suspends all motion updates while enabled.\n"
		+ "Can be used for teleportation, instant picking up of items and more.",
	name = "Blink",
	tutorial = "Mods/Blink")
public class BlinkMod extends Mod
{
	private static ArrayList<Packet> packets = new ArrayList<Packet>();
	private EntityOtherPlayerMP fakePlayer = null;
	private double oldX;
	private double oldY;
	private double oldZ;
	private static long blinkTime;
	private static long lastTime;
	
	@Override
	public String getRenderName()
	{
		return "Blink [" + blinkTime + "ms]";
	}
	
	@Override
	public void onEnable()
	{
		lastTime = System.currentTimeMillis();
		
		oldX = mc.thePlayer.posX;
		oldY = mc.thePlayer.posY;
		oldZ = mc.thePlayer.posZ;
		fakePlayer =
			new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
		fakePlayer.clonePlayer(mc.thePlayer, true);
		fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
		fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
		mc.theWorld.addEntityToWorld(-69, fakePlayer);
	}
	
	@Override
	public void onDisable()
	{
		for(Packet packet : packets)
			mc.thePlayer.sendQueue.addToSendQueue(packet);
		packets.clear();
		mc.theWorld.removeEntityFromWorld(-69);
		fakePlayer = null;
		blinkTime = 0;
	}
	
	public static void addToBlinkQueue(Packet packet)
	{
		if(mc.thePlayer.posX != mc.thePlayer.prevPosX
			|| mc.thePlayer.posZ != Minecraft.getMinecraft().thePlayer.prevPosZ
			|| mc.thePlayer.posY != Minecraft.getMinecraft().thePlayer.prevPosY)
		{
			blinkTime += System.currentTimeMillis() - lastTime;
			packets.add(packet);
		}
		lastTime = System.currentTimeMillis();
	}
	
	public void cancel()
	{
		packets.clear();
		mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ,
			mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		setEnabled(false);
	}
}
