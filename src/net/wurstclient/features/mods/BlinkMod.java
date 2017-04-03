/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.PacketOutputEvent;
import net.wurstclient.events.listeners.PacketOutputListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Info;

@Info(
	description = "Suspends all motion updates while enabled.\n"
		+ "Can be used for teleportation, instant picking up of items and more.",
	name = "Blink",
	help = "Mods/Blink")
@Bypasses
public class BlinkMod extends Mod implements PacketOutputListener
{
	private static ArrayList<Packet> packets = new ArrayList<>();
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
		
		oldX = WMinecraft.getPlayer().posX;
		oldY = WMinecraft.getPlayer().posY;
		oldZ = WMinecraft.getPlayer().posZ;
		fakePlayer = new EntityOtherPlayerMP(WMinecraft.getWorld(),
			WMinecraft.getPlayer().getGameProfile());
		fakePlayer.clonePlayer(WMinecraft.getPlayer(), true);
		fakePlayer.copyLocationAndAnglesFrom(WMinecraft.getPlayer());
		fakePlayer.rotationYawHead = WMinecraft.getPlayer().rotationYawHead;
		WMinecraft.getWorld().addEntityToWorld(-69, fakePlayer);
		
		wurst.events.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		Packet packet = event.getPacket();
		if(packet instanceof CPacketPlayer)
		{
			if(WMinecraft.getPlayer().posX != WMinecraft.getPlayer().prevPosX
				|| WMinecraft.getPlayer().posZ != WMinecraft
					.getPlayer().prevPosZ
				|| WMinecraft.getPlayer().posY != WMinecraft
					.getPlayer().prevPosY)
			{
				blinkTime += System.currentTimeMillis() - lastTime;
				packets.add(packet);
			}
			lastTime = System.currentTimeMillis();
			event.cancel();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(PacketOutputListener.class, this);
		
		for(Packet packet : packets)
			WMinecraft.getPlayer().sendQueue.addToSendQueue(packet);
		packets.clear();
		WMinecraft.getWorld().removeEntityFromWorld(-69);
		fakePlayer = null;
		blinkTime = 0;
	}
	
	public void cancel()
	{
		packets.clear();
		WMinecraft.getPlayer().setPositionAndRotation(oldX, oldY, oldZ,
			WMinecraft.getPlayer().rotationYaw,
			WMinecraft.getPlayer().rotationPitch);
		setEnabled(false);
	}
}
