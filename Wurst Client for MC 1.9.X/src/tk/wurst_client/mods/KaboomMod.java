/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.BlockUtils;

@Info(category = Category.BLOCKS,
	description = "Breaks blocks around you like an explosion.\n"
		+ "This can be a lot faster than Nuker if the server\n"
		+ "doesn't have NoCheat+. It works best with fast tools\n"
		+ "and weak blocks.\n" + "Note that this is not an actual explosion.",
	name = "Kaboom",
	noCheatCompatible = false,
	tutorial = "Mods/Kaboom")
public class KaboomMod extends Mod implements UpdateListener
{
	private int range = 6;
	public int power = 128;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Power", power, 32, 512, 32,
			ValueDisplay.INTEGER)
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
		if(mc.thePlayer.capabilities.isCreativeMode)
		{
			wurst.chat.error("Surivival mode only.");
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
					new Explosion(mc.theWorld, mc.thePlayer, mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, 6F, false, true)
						.doExplosionB(true);
					for(int x = range; x >= -range - 1; x--)
						for(int z = range; z >= -range; z--)
						{
							int posX = (int)(Math.floor(mc.thePlayer.posX) + x);
							int posY = (int)(Math.floor(mc.thePlayer.posY) + y);
							int posZ = (int)(Math.floor(mc.thePlayer.posZ) + z);
							if(x == 0 && y == -1 && z == 0)
								continue;
							BlockPos pos = new BlockPos(posX, posY, posZ);
							Block block =
								mc.theWorld.getBlockState(pos).getBlock();
							float xDiff = (float)(mc.thePlayer.posX - posX);
							float yDiff = (float)(mc.thePlayer.posY - posY);
							float zDiff = (float)(mc.thePlayer.posZ - posZ);
							float currentDistance =
								BlockUtils
									.getBlockDistance(xDiff, yDiff, zDiff);
							if(Block.getIdFromBlock(block) != 0 && posY >= 0
								&& currentDistance <= range)
							{
								if(!mc.thePlayer.onGround)
									continue;
								EnumFacing side = mc.objectMouseOver.sideHit;
								BlockUtils.faceBlockPacket(pos);
								mc.thePlayer.sendQueue
									.addToSendQueue(new CPacketAnimation(EnumHand.MAIN_HAND));
								mc.thePlayer.sendQueue
									.addToSendQueue(new CPacketPlayerDigging(
										Action.START_DESTROY_BLOCK, pos, side));
								for(int i = 0; i < power; i++)
									mc.thePlayer.sendQueue
										.addToSendQueue(new CPacketPlayerDigging(
											Action.STOP_DESTROY_BLOCK, pos,
											side));
								block.onBlockDestroyedByPlayer(
									Minecraft.getMinecraft().theWorld, pos,
									mc.theWorld.getBlockState(pos));
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
