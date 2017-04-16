/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.utils.InventoryUtils;

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
		// check if right-clicking
		if(!mc.gameSettings.keyBindUseItem.pressed)
			return;
		
		// check fly-kick
		if(!WMinecraft.getPlayer().onGround
			&& !WMinecraft.getPlayer().capabilities.isCreativeMode)
			return;
		
		// check health
		if(WMinecraft.getPlayer().getHealth() <= 0)
			return;
		
		// check held item
		ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(InventoryUtils.isEmptySlot(stack)
			|| !(stack.getItem() instanceof ItemBow))
			return;
		
		WPlayerController.processRightClick();
		
		for(int i = 0; i < 20; i++)
			WConnection.sendPacket(new CPacketPlayer(false));
		
		mc.playerController.onStoppedUsingItem(WMinecraft.getPlayer());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
