/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(
	description = "Breaks blocks around you like an explosion.\n"
		+ "This can be a lot faster than Nuker if the server\n"
		+ "doesn't have NoCheat+. It works best with fast tools\n"
		+ "and weak blocks.\n" + "Note that this is not an actual explosion.",
	name = "Kaboom",
	help = "Mods/Kaboom")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class KaboomMod extends Mod implements UpdateListener
{
	private int range = 6;
	public int power = 128;
	
	@Override
	public void initSettings()
	{
		settings.add(
			new SliderSetting("Power", power, 32, 512, 32, ValueDisplay.INTEGER)
			{
				@Override
				public void update()
				{
					power = (int)getValue();
				}
			});
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Survival mode only.");
			setEnabled(false);
			return;
		}
		new Thread("Kaboom")
		{
			@Override
			public void run()
			{
				for(int y = range; y >= -range; y--)
				{
					new Explosion(WMinecraft.getWorld(), WMinecraft.getPlayer(),
						WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY,
						WMinecraft.getPlayer().posZ, 6F, false, true)
							.doExplosionB(true);
					for(int x = range; x >= -range - 1; x--)
						for(int z = range; z >= -range; z--)
						{
							int posX =
								(int)(Math.floor(WMinecraft.getPlayer().posX)
									+ x);
							int posY =
								(int)(Math.floor(WMinecraft.getPlayer().posY)
									+ y);
							int posZ =
								(int)(Math.floor(WMinecraft.getPlayer().posZ)
									+ z);
							if(x == 0 && y == -1 && z == 0)
								continue;
							BlockPos pos = new BlockPos(posX, posY, posZ);
							Block block = WMinecraft.getWorld()
								.getBlockState(pos).getBlock();
							float xDiff =
								(float)(WMinecraft.getPlayer().posX - posX);
							float yDiff =
								(float)(WMinecraft.getPlayer().posY - posY);
							float zDiff =
								(float)(WMinecraft.getPlayer().posZ - posZ);
							float currentDistance = BlockUtils
								.getBlockDistance(xDiff, yDiff, zDiff);
							if(Block.getIdFromBlock(block) != 0 && posY >= 0
								&& currentDistance <= range)
							{
								if(!WMinecraft.getPlayer().onGround)
									continue;
								EnumFacing side = mc.objectMouseOver.sideHit;
								BlockUtils.faceBlockPacket(pos);
								WPlayer.swingArmPacket();
								WMinecraft.getPlayer().sendQueue
									.addToSendQueue(new CPacketPlayerDigging(
										Action.START_DESTROY_BLOCK, pos, side));
								for(int i = 0; i < power; i++)
									WMinecraft.getPlayer().sendQueue
										.addToSendQueue(
											new CPacketPlayerDigging(
												Action.STOP_DESTROY_BLOCK, pos,
												side));
								block.onBlockDestroyedByPlayer(
									WMinecraft.getWorld(), pos,
									WMinecraft.getWorld().getBlockState(pos));
							}
						}
				}
			}
		}.start();
		setEnabled(false);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
