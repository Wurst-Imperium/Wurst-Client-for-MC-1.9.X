/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WConnection;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(description = "Allows you to step up full blocks.",
	name = "Step",
	help = "Mods/Step")
@Mod.Bypasses
public final class StepMod extends Mod implements UpdateListener
{
	public SliderSetting height =
		new SliderSetting("Height", 1, 1, 100, 1, ValueDisplay.INTEGER);
	
	@Override
	public void initSettings()
	{
		settings.add(height);
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
		WMinecraft.getPlayer().stepHeight = 0.5F;
	}
	
	@Override
	public void onUpdate()
	{
		if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() >= BypassLevel.ANTICHEAT.ordinal())
		{
			WMinecraft.getPlayer().stepHeight = 0.5F;
			if(WMinecraft.getPlayer().onGround
				&& !WMinecraft.getPlayer().isOnLadder()
				&& (WMinecraft.getPlayer().movementInput.moveForward != 0.0F
					|| WMinecraft.getPlayer().movementInput.moveStrafe != 0.0F)
				&& canStep() && !WMinecraft.getPlayer().movementInput.jump
				&& WMinecraft.getPlayer().isCollidedHorizontally)
			{
				WConnection.sendPacket(
					new CPacketPlayer.Position(WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 0.42D,
						WMinecraft.getPlayer().posZ,
						WMinecraft.getPlayer().onGround));
				WConnection.sendPacket(
					new CPacketPlayer.Position(WMinecraft.getPlayer().posX,
						WMinecraft.getPlayer().posY + 0.753D,
						WMinecraft.getPlayer().posZ,
						WMinecraft.getPlayer().onGround));
				WMinecraft.getPlayer().setPosition(WMinecraft.getPlayer().posX,
					WMinecraft.getPlayer().posY + 1D,
					WMinecraft.getPlayer().posZ);
			}
		}else
			WMinecraft.getPlayer().stepHeight = height.getValueF();
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
			height.unlock();
			break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			height.lock(() -> 1);
			break;
		}
	}
	
	private boolean canStep()
	{
		ArrayList<BlockPos> collisionBlocks = new ArrayList<>();
		
		EntityPlayerSP player = WMinecraft.getPlayer();
		BlockPos pos1 =
			new BlockPos(player.getEntityBoundingBox().minX - 0.001D,
				player.getEntityBoundingBox().minY - 0.001D,
				player.getEntityBoundingBox().minZ - 0.001D);
		BlockPos pos2 =
			new BlockPos(player.getEntityBoundingBox().maxX + 0.001D,
				player.getEntityBoundingBox().maxY + 0.001D,
				player.getEntityBoundingBox().maxZ + 0.001D);
		
		if(WMinecraft.getWorld().isAreaLoaded(pos1, pos2))
			for(int x = pos1.getX(); x <= pos2.getX(); x++)
				for(int y = pos1.getY(); y <= pos2.getY(); y++)
					for(int z = pos1.getZ(); z <= pos2.getZ(); z++)
						if(y > player.posY - 1.0D && y <= player.posY)
							collisionBlocks.add(new BlockPos(x, y, z));
						
		BlockPos belowPlayerPos =
			new BlockPos(player.posX, player.posY - 1.0D, player.posZ);
		for(BlockPos collisionBlock : collisionBlocks)
			if(!(WMinecraft.getWorld()
				.getBlockState(collisionBlock.add(0, 1, 0))
				.getBlock() instanceof BlockFenceGate))
				if(WMinecraft.getWorld()
					.getBlockState(collisionBlock.add(0, 1, 0)).getBlock()
					.getCollisionBoundingBox(
						WMinecraft.getWorld().getBlockState(collisionBlock),
						WMinecraft.getWorld(), belowPlayerPos) != null)
					return false;
				
		return true;
	}
}
