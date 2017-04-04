/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;

@Mod.Info(
	description = "Breaks blocks around you like an explosion.\n"
		+ "This can be a lot faster than Nuker if the server doesn't have NoCheat+.\n"
		+ "It works best with fast tools and weak blocks.\n"
		+ "Note that this is not an actual explosion.",
	name = "Kaboom",
	help = "Mods/Kaboom")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false,
	mineplex = false)
public final class KaboomMod extends Mod implements UpdateListener
{
	public final SliderSetting power =
		new SliderSetting("Power", 128, 32, 512, 32, ValueDisplay.INTEGER);
	
	@Override
	public void initSettings()
	{
		settings.add(power);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check fly-kick
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode
			&& !WMinecraft.getPlayer().onGround)
			return;
		
		// do explosion particles
		new Explosion(WMinecraft.getWorld(), WMinecraft.getPlayer(),
			WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY,
			WMinecraft.getPlayer().posZ, 6F, false, true).doExplosionB(true);
		
		// get valid blocks
		Iterable<BlockPos> validBlocks =
			BlockUtils.getValidBlocksByDistanceReversed(6, true, (p) -> true);
		
		// break all blocks
		for(BlockPos pos : validBlocks)
			for(int i = 0; i < power.getValueI(); i++)
				BlockUtils.breakBlockPacketSpam(pos);
			
		// disable
		setEnabled(false);
	}
}
