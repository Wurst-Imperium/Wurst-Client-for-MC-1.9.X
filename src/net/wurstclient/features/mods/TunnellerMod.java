/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.PostUpdateListener;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.RenderUtils;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(description = "Digs a 3x3 tunnel around you.",
	name = "Tunneller",
	help = "Mods/Tunneller")
@Mod.Bypasses
public final class TunnellerMod extends Mod
	implements UpdateListener, PostUpdateListener, RenderListener
{
	private BlockPos currentBlock;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.nukerMod, wurst.mods.nukerLegitMod,
			wurst.mods.speedNukerMod, wurst.mods.fastBreakMod,
			wurst.mods.autoMineMod, wurst.mods.autoToolMod,
			wurst.special.yesCheatSpf};
	}
	
	@Override
	public void onEnable()
	{
		// disable other nukers
		wurst.mods.nukerMod.setEnabled(false);
		wurst.mods.nukerLegitMod.setEnabled(false);
		wurst.mods.speedNukerMod.setEnabled(false);
		
		// add listeners
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PostUpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		// remove listeners
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PostUpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		
		// resets
		mc.playerController.resetBlockRemoving();
		currentBlock = null;
	}
	
	@Override
	public void onUpdate()
	{
		boolean legit = wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() > BypassLevel.MINEPLEX.ordinal();
		
		currentBlock = null;
		
		// get valid blocks
		Iterable<BlockPos> validBlocks =
			BlockUtils.getValidBlocks(1, (p) -> true);
		
		// nuke all
		if(WMinecraft.getPlayer().capabilities.isCreativeMode && !legit)
		{
			mc.playerController.resetBlockRemoving();
			
			// prepare distance check
			Vec3d eyesPos = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
			double closestDistanceSq = Double.POSITIVE_INFINITY;
			
			// break all blocks
			for(BlockPos pos : validBlocks)
			{
				BlockUtils.breakBlockPacketSpam(pos);
				
				// find closest block
				double currentDistanceSq =
					eyesPos.squareDistanceTo(new Vec3d(pos));
				if(currentDistanceSq < closestDistanceSq)
				{
					closestDistanceSq = currentDistanceSq;
					currentBlock = pos;
				}
			}
			
			return;
		}
		
		// find valid block
		for(BlockPos pos : validBlocks)
		{
			boolean successful;
			
			// break block
			if(legit)
				successful = BlockUtils.prepareToBreakBlockLegit(pos);
			else
				successful = BlockUtils.breakBlockSimple(pos);
			
			// set currentBlock if successful
			if(successful)
			{
				currentBlock = pos;
				break;
			}
		}
		
		// reset if no block was found
		if(currentBlock == null)
			mc.playerController.resetBlockRemoving();
	}
	
	@Override
	public void afterUpdate()
	{
		boolean legit = wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() > BypassLevel.MINEPLEX.ordinal();
		
		// break block
		if(currentBlock != null && legit)
			BlockUtils.breakBlockLegit(currentBlock);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(currentBlock == null)
			return;
		
		// check if block can be destroyed instantly
		if(WMinecraft.getPlayer().capabilities.isCreativeMode
			|| WBlock.getHardness(currentBlock) >= 1)
			RenderUtils.nukerBox(currentBlock, 1);
		else
			RenderUtils.nukerBox(currentBlock,
				mc.playerController.curBlockDamageMP);
	}
}
