/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;

@Mod.Info(
	description = "Turns your bow into a machine gun.\n"
		+ "Tip: This works with BowAimbot.",
	name = "FastBow",
	tags = "RapidFire, BowSpam, fast bow, rapid fire, bow spam",
	help = "Mods/FastBow")
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class FastBowMod extends Mod implements UpdateListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.bowAimbotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().getHealth() > 0
			&& (WMinecraft.getPlayer().onGround
				|| WMinecraft.getPlayer().capabilities.isCreativeMode)
			&& WMinecraft.getPlayer().inventory.getCurrentItem() != null
			&& WMinecraft.getPlayer().inventory.getCurrentItem()
				.getItem() instanceof ItemBow
			&& mc.gameSettings.keyBindUseItem.pressed)
		{
			WPlayerController.processRightClick();
			WMinecraft.getPlayer().inventory.getCurrentItem().getItem()
				.onItemRightClick(
					WMinecraft.getPlayer().inventory.getCurrentItem(),
					WMinecraft.getWorld(), WMinecraft.getPlayer(),
					EnumHand.MAIN_HAND);
			for(int i = 0; i < 20; i++)
				WConnection.sendPacket(new CPacketPlayer(false));
			WConnection
				.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM,
					new BlockPos(0, 0, 0), EnumFacing.DOWN));
			WMinecraft.getPlayer().inventory.getCurrentItem().getItem()
				.onPlayerStoppedUsing(
					WMinecraft.getPlayer().inventory.getCurrentItem(),
					WMinecraft.getWorld(), WMinecraft.getPlayer(), 10);
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
