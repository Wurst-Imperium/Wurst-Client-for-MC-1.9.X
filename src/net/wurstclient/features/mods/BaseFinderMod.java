/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(
	description = "Finds player bases by searching for man-made blocks.\n"
		+ "The blocks that it finds will be highlighted in red.\n"
		+ "Good for finding faction bases.",
	name = "BaseFinder",
	tags = "base finder, factions",
	help = "Mods/BaseFinder")
@Mod.Bypasses
public final class BaseFinderMod extends Mod
	implements UpdateListener, RenderListener
{
	private static final List<Block> NATURAL_BLOCKS = Arrays.<Block> asList(
		Blocks.air, Blocks.stone, Blocks.dirt, Blocks.grass, Blocks.gravel,
		Blocks.sand, Blocks.clay, Blocks.sandstone, Blocks.flowing_water,
		Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.log, Blocks.log2,
		Blocks.leaves, Blocks.leaves2, Blocks.deadbush, Blocks.iron_ore,
		Blocks.coal_ore, Blocks.gold_ore, Blocks.diamond_ore,
		Blocks.emerald_ore, Blocks.redstone_ore, Blocks.lapis_ore,
		Blocks.bedrock, Blocks.mob_spawner, Blocks.mossy_cobblestone,
		Blocks.tallgrass, Blocks.yellow_flower, Blocks.red_flower, Blocks.web,
		Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.snow_layer,
		Blocks.vine, Blocks.waterlily, Blocks.double_plant,
		Blocks.hardened_clay, Blocks.red_sandstone, Blocks.ice,
		Blocks.quartz_ore, Blocks.obsidian, Blocks.monster_egg,
		Blocks.red_mushroom_block, Blocks.brown_mushroom_block);
	
	private final HashSet<BlockPos> matchingBlocks = new HashSet<>();
	private final ArrayList<int[]> vertices = new ArrayList<>();
	
	private int messageTimer = 0;
	private int counter;
	
	@Override
	public String getRenderName()
	{
		String name = getName() + " [";
		
		// counter
		if(counter >= 10000)
			name += "10000+ blocks";
		else if(counter == 1)
			name += "1 block";
		else if(counter == 0)
			name += "nothing";
		else
			name += counter + " blocks";
		
		name += " found]";
		return name;
	}
	
	@Override
	public void onEnable()
	{
		// reset timer
		messageTimer = 0;
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 0F, 0F, 0.15F);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// vertices
		GL11.glBegin(GL11.GL_QUADS);
		{
			for(int[] vertex : vertices)
				GL11.glVertex3d(vertex[0], vertex[1], vertex[2]);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onUpdate()
	{
		int modulo = WMinecraft.getPlayer().ticksExisted % 64;
		
		// reset matching blocks
		if(modulo == 0)
			matchingBlocks.clear();
		
		int startY = 255 - modulo * 4;
		int endY = startY - 4;
		
		BlockPos playerPos = new BlockPos(WMinecraft.getPlayer().posX, 0,
			WMinecraft.getPlayer().posZ);
		
		// search matching blocks
		loop: for(int y = startY; y > endY; y--)
			for(int x = 64; x > -64; x--)
				for(int z = 64; z > -64; z--)
				{
					if(matchingBlocks.size() >= 10000)
						break loop;
					
					BlockPos pos = playerPos.add(x, y, z);
					
					if(NATURAL_BLOCKS.contains(WBlock.getBlock(pos)))
						continue;
					
					matchingBlocks.add(pos);
				}
			
		if(modulo != 63)
			return;
		
		// update timer
		if(matchingBlocks.size() < 10000)
			messageTimer--;
		else
		{
			// show message
			if(messageTimer <= 0)
			{
				ChatUtils.warning("BaseFinder found §lA LOT§r of blocks.");
				ChatUtils.message(
					"To prevent lag, it will only show the first 10000 blocks.");
			}
			
			// reset timer
			messageTimer = 3;
		}
		
		// update counter
		counter = matchingBlocks.size();
		
		// calculate vertices
		vertices.clear();
		for(BlockPos pos : matchingBlocks)
		{
			if(!matchingBlocks.contains(pos.down()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 1, 0, 0);
				addVertex(pos, 1, 0, 1);
				addVertex(pos, 0, 0, 1);
			}
			
			if(!matchingBlocks.contains(pos.up()))
			{
				addVertex(pos, 0, 1, 0);
				addVertex(pos, 0, 1, 1);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 1, 1, 0);
			}
			
			if(!matchingBlocks.contains(pos.north()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 0, 1, 0);
				addVertex(pos, 1, 1, 0);
				addVertex(pos, 1, 0, 0);
			}
			
			if(!matchingBlocks.contains(pos.east()))
			{
				addVertex(pos, 1, 0, 0);
				addVertex(pos, 1, 1, 0);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 1, 0, 1);
			}
			
			if(!matchingBlocks.contains(pos.south()))
			{
				addVertex(pos, 0, 0, 1);
				addVertex(pos, 1, 0, 1);
				addVertex(pos, 1, 1, 1);
				addVertex(pos, 0, 1, 1);
			}
			
			if(!matchingBlocks.contains(pos.west()))
			{
				addVertex(pos, 0, 0, 0);
				addVertex(pos, 0, 0, 1);
				addVertex(pos, 0, 1, 1);
				addVertex(pos, 0, 1, 0);
			}
		}
	}
	
	private void addVertex(BlockPos pos, int x, int y, int z)
	{
		vertices.add(new int[]{pos.getX() + x, pos.getY() + y, pos.getZ() + z});
	}
}
