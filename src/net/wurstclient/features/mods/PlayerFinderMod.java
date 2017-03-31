/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.awt.Color;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.events.PacketInputEvent;
import net.wurstclient.events.listeners.PacketInputListener;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;

@Info(category = Category.RENDER,
	description = "Finds far players during thunderstorms.",
	name = "PlayerFinder",
	tags = "player finder",
	help = "Mods/PlayerFinder")
@Bypasses
public class PlayerFinderMod extends Mod
	implements PacketInputListener, RenderListener
{
	private BlockPos blockPos;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.playerEspMod, wurst.mods.tracersMod};
	}
	
	@Override
	public void onEnable()
	{
		blockPos = null;
		wurst.events.add(PacketInputListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		if(blockPos == null)
			return;
		float red = (1F - (float)Math.sin(
			(float)(System.currentTimeMillis() % 1000L) / 1000L * Math.PI * 2))
			/ 2F;
		float green = (1F - (float)Math
			.sin((float)((System.currentTimeMillis() + 333L) % 1000L) / 1000L
				* Math.PI * 2))
			/ 2F;
		float blue = (1F - (float)Math
			.sin((float)((System.currentTimeMillis() + 666L) % 1000L) / 1000L
				* Math.PI * 2))
			/ 2F;
		Color color = new Color(red, green, blue);
		RenderUtils.tracerLine(blockPos.getX(), blockPos.getY(),
			blockPos.getZ(), color);
		RenderUtils.blockESPBox(blockPos);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(PacketInputListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onReceivedPacket(PacketInputEvent event)
	{
		if(mc.thePlayer == null)
			return;
		Packet packet = event.getPacket();
		if(packet instanceof SPacketEffect)
		{
			SPacketEffect effect = (SPacketEffect)packet;
			BlockPos pos = effect.getSoundPos();
			if(BlockUtils.getPlayerBlockDistance(pos) >= 160)
				blockPos = pos;
		}else if(packet instanceof SPacketSoundEffect)
		{
			SPacketSoundEffect sound = (SPacketSoundEffect)packet;
			BlockPos pos =
				new BlockPos(sound.getX(), sound.getY(), sound.getZ());
			if(BlockUtils.getPlayerBlockDistance(pos) >= 160)
				blockPos = pos;
		}else if(packet instanceof SPacketSpawnGlobalEntity)
		{
			SPacketSpawnGlobalEntity lightning =
				(SPacketSpawnGlobalEntity)packet;
			BlockPos pos = new BlockPos(lightning.getX() / 32D,
				lightning.getY() / 32D, lightning.getZ() / 32D);
			if(BlockUtils.getPlayerBlockDistance(pos) >= 160)
				blockPos = pos;
		}
	}
}
