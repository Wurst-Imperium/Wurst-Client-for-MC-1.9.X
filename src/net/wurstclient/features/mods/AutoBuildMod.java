/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.utils.BuildUtils;
import net.wurstclient.utils.RenderUtils;

@Info(category = Category.AUTOBUILD,
	description = "Automatically builds the selected template whenever\n"
		+ "you place a block.\n"
		+ "This mod can bypass NoCheat+ while YesCheat+ is\n" + "enabled.",
	name = "AutoBuild",
	tags = "AutoBridge, AutoFloor, AutoNazi, AutoPenis, AutoPillar, AutoWall, AutoWurst, auto build",
	help = "Mods/AutoBuild")
@Bypasses(ghostMode = false)
public class AutoBuildMod extends Mod implements UpdateListener, RenderListener
{
	public static ArrayList<String> names = new ArrayList<>();
	public static ArrayList<int[][]> templates = new ArrayList<>();
	private int template = 1;
	
	private float speed = 5;
	private int blockIndex;
	private boolean shouldBuild;
	private float playerYaw;
	private RayTraceResult mouseOver;
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + names.get(template) + "]";
	}
	
	public void initTemplateSetting()
	{
		settings.add(new ModeSetting("Template",
			names.toArray(new String[names.size()]), template)
		{
			@Override
			public void update()
			{
				template = getSelected();
			}
		});
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.buildRandomMod,
			wurst.mods.fastPlaceMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(templates.get(template)[0].length == 4)
			renderAdvanced();
		else
			renderSimple();
	}
	
	@Override
	public void onUpdate()
	{
		if(templates.get(template)[0].length == 4)
			buildAdvanced();
		else
			buildSimple();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		shouldBuild = false;
	}
	
	// TODO: Clean up
	
	private void renderAdvanced()
	{
		if(shouldBuild && blockIndex < templates.get(template).length
			&& blockIndex >= 0)
			if(playerYaw > -45 && playerYaw <= 45)
			{// F: 0 South
				double renderX = BuildUtils.convertPosNext(1, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(1, blockIndex,
						templates.get(template));
				double renderY = BuildUtils.convertPosNext(2, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(2, blockIndex,
						templates.get(template));
				double renderZ = BuildUtils.convertPosNext(3, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(3, blockIndex,
						templates.get(template));
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > 45 && playerYaw <= 135)
			{// F: 1 West
				double renderX = BuildUtils.convertPosNext(1, mouseOver)
					- BuildUtils.convertPosInAdvancedBuiling(3, blockIndex,
						templates.get(template));
				double renderY = BuildUtils.convertPosNext(2, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(2, blockIndex,
						templates.get(template));
				double renderZ = BuildUtils.convertPosNext(3, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(1, blockIndex,
						templates.get(template));
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > 135 || playerYaw <= -135)
			{// F: 2 North
				double renderX = BuildUtils.convertPosNext(1, mouseOver)
					- BuildUtils.convertPosInAdvancedBuiling(1, blockIndex,
						templates.get(template));
				double renderY = BuildUtils.convertPosNext(2, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(2, blockIndex,
						templates.get(template));
				double renderZ = BuildUtils.convertPosNext(3, mouseOver)
					- BuildUtils.convertPosInAdvancedBuiling(3, blockIndex,
						templates.get(template));
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > -135 && playerYaw <= -45)
			{// F: 3 East
				double renderX = BuildUtils.convertPosNext(1, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(3, blockIndex,
						templates.get(template));
				double renderY = BuildUtils.convertPosNext(2, mouseOver)
					+ BuildUtils.convertPosInAdvancedBuiling(2, blockIndex,
						templates.get(template));
				double renderZ = BuildUtils.convertPosNext(3, mouseOver)
					- BuildUtils.convertPosInAdvancedBuiling(1, blockIndex,
						templates.get(template));
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}
		if(shouldBuild && mouseOver != null)
		{
			double renderX = BuildUtils.convertPosNext(1, mouseOver);
			double renderY = BuildUtils.convertPosNext(2, mouseOver) + 1;
			double renderZ = BuildUtils.convertPosNext(3, mouseOver);
			RenderUtils
				.emptyBlockESPBox(new BlockPos(renderX, renderY, renderZ));
		}
		for(int i = 0; i < templates.get(template).length; i++)
			if(shouldBuild && mouseOver != null)
				if(playerYaw > -45 && playerYaw <= 45)
				{// F: 0 South
					double renderX = BuildUtils.convertPosNext(1, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(1, i,
							templates.get(template));
					double renderY = BuildUtils.convertPosNext(2, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(2, i,
							templates.get(template));
					double renderZ = BuildUtils.convertPosNext(3, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(3, i,
							templates.get(template));
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > 45 && playerYaw <= 135)
				{// F: 1 West
					double renderX = BuildUtils.convertPosNext(1, mouseOver)
						- BuildUtils.convertPosInAdvancedBuiling(3, i,
							templates.get(template));
					double renderY = BuildUtils.convertPosNext(2, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(2, i,
							templates.get(template));
					double renderZ = BuildUtils.convertPosNext(3, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(1, i,
							templates.get(template));
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > 135 || playerYaw <= -135)
				{// F: 2 North
					double renderX = BuildUtils.convertPosNext(1, mouseOver)
						- BuildUtils.convertPosInAdvancedBuiling(1, i,
							templates.get(template));
					double renderY = BuildUtils.convertPosNext(2, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(2, i,
							templates.get(template));
					double renderZ = BuildUtils.convertPosNext(3, mouseOver)
						- BuildUtils.convertPosInAdvancedBuiling(3, i,
							templates.get(template));
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > -135 && playerYaw <= -45)
				{// F: 3 East
					double renderX = BuildUtils.convertPosNext(1, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(3, i,
							templates.get(template));
					double renderY = BuildUtils.convertPosNext(2, mouseOver)
						+ BuildUtils.convertPosInAdvancedBuiling(2, i,
							templates.get(template));
					double renderZ = BuildUtils.convertPosNext(3, mouseOver)
						- BuildUtils.convertPosInAdvancedBuiling(1, i,
							templates.get(template));
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}
	}
	
	private void renderSimple()
	{
		if(shouldBuild && blockIndex < templates.get(template).length
			&& blockIndex >= 0)
			if(playerYaw > -45 && playerYaw <= 45)
			{// F: 0 South
				double renderX = mouseOver.getBlockPos().getX()
					+ BuildUtils.convertPosInBuiling(1, blockIndex,
						templates.get(template), mouseOver);
				double renderY = mouseOver.getBlockPos().getY()
					+ BuildUtils.convertPosInBuiling(2, blockIndex,
						templates.get(template), mouseOver);
				double renderZ = mouseOver.getBlockPos().getZ()
					+ BuildUtils.convertPosInBuiling(3, blockIndex,
						templates.get(template), mouseOver);
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > 45 && playerYaw <= 135)
			{// F: 1 West
				double renderX = mouseOver.getBlockPos().getX()
					- BuildUtils.convertPosInBuiling(3, blockIndex,
						templates.get(template), mouseOver);
				double renderY = mouseOver.getBlockPos().getY()
					+ BuildUtils.convertPosInBuiling(2, blockIndex,
						templates.get(template), mouseOver);
				double renderZ = mouseOver.getBlockPos().getZ()
					+ BuildUtils.convertPosInBuiling(1, blockIndex,
						templates.get(template), mouseOver);
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > 135 || playerYaw <= -135)
			{// F: 2 North
				double renderX = mouseOver.getBlockPos().getX()
					- BuildUtils.convertPosInBuiling(1, blockIndex,
						templates.get(template), mouseOver);
				double renderY = mouseOver.getBlockPos().getY()
					+ BuildUtils.convertPosInBuiling(2, blockIndex,
						templates.get(template), mouseOver);
				double renderZ = mouseOver.getBlockPos().getZ()
					- BuildUtils.convertPosInBuiling(3, blockIndex,
						templates.get(template), mouseOver);
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}else if(playerYaw > -135 && playerYaw <= -45)
			{// F: 3 East
				double renderX = mouseOver.getBlockPos().getX()
					+ BuildUtils.convertPosInBuiling(3, blockIndex,
						templates.get(template), mouseOver);
				double renderY = mouseOver.getBlockPos().getY()
					+ BuildUtils.convertPosInBuiling(2, blockIndex,
						templates.get(template), mouseOver);
				double renderZ = mouseOver.getBlockPos().getZ()
					- BuildUtils.convertPosInBuiling(1, blockIndex,
						templates.get(template), mouseOver);
				RenderUtils
					.blockESPBox(new BlockPos(renderX, renderY, renderZ));
			}
		if(shouldBuild && mouseOver != null)
		{
			double renderX = BuildUtils.convertPosNext(1, mouseOver);
			double renderY = BuildUtils.convertPosNext(2, mouseOver) + 1;
			double renderZ = BuildUtils.convertPosNext(3, mouseOver);
			RenderUtils
				.emptyBlockESPBox(new BlockPos(renderX, renderY, renderZ));
		}
		for(int i = 0; i < templates.get(template).length; i++)
			if(shouldBuild && mouseOver != null)
				if(playerYaw > -45 && playerYaw <= 45)
				{// F: 0 South
					double renderX = mouseOver.getBlockPos().getX()
						+ BuildUtils.convertPosInBuiling(1, i,
							templates.get(template), mouseOver);
					double renderY = mouseOver.getBlockPos().getY()
						+ BuildUtils.convertPosInBuiling(2, i,
							templates.get(template), mouseOver);
					double renderZ = mouseOver.getBlockPos().getZ()
						+ BuildUtils.convertPosInBuiling(3, i,
							templates.get(template), mouseOver);
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > 45 && playerYaw <= 135)
				{// F: 1 West
					double renderX = mouseOver.getBlockPos().getX()
						- BuildUtils.convertPosInBuiling(3, i,
							templates.get(template), mouseOver);
					double renderY = mouseOver.getBlockPos().getY()
						+ BuildUtils.convertPosInBuiling(2, i,
							templates.get(template), mouseOver);
					double renderZ = mouseOver.getBlockPos().getZ()
						+ BuildUtils.convertPosInBuiling(1, i,
							templates.get(template), mouseOver);
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > 135 || playerYaw <= -135)
				{// F: 2 North
					double renderX = mouseOver.getBlockPos().getX()
						- BuildUtils.convertPosInBuiling(1, i,
							templates.get(template), mouseOver);
					double renderY = mouseOver.getBlockPos().getY()
						+ BuildUtils.convertPosInBuiling(2, i,
							templates.get(template), mouseOver);
					double renderZ = mouseOver.getBlockPos().getZ()
						- BuildUtils.convertPosInBuiling(3, i,
							templates.get(template), mouseOver);
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}else if(playerYaw > -135 && playerYaw <= -45)
				{// F: 3 East
					double renderX = mouseOver.getBlockPos().getX()
						+ BuildUtils.convertPosInBuiling(3, i,
							templates.get(template), mouseOver);
					double renderY = mouseOver.getBlockPos().getY()
						+ BuildUtils.convertPosInBuiling(2, i,
							templates.get(template), mouseOver);
					double renderZ = mouseOver.getBlockPos().getZ()
						- BuildUtils.convertPosInBuiling(1, i,
							templates.get(template), mouseOver);
					RenderUtils.emptyBlockESPBox(
						new BlockPos(renderX, renderY, renderZ));
				}
	}
	
	private void buildAdvanced()
	{
		updateMS();
		if(!shouldBuild
			&& (mc.rightClickDelayTimer == 4
				|| wurst.mods.fastPlaceMod.isActive())
			&& mc.gameSettings.keyBindUseItem.pressed
			&& mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.air)
		{
			if(wurst.mods.fastPlaceMod.isActive())
				speed = 1000000000;
			else
				speed = 5;
			if(wurst.special.yesCheatSpf.getBypassLevel()
				.ordinal() >= BypassLevel.ANTICHEAT.ordinal())
			{
				blockIndex = 0;
				shouldBuild = true;
				mouseOver = mc.objectMouseOver;
				playerYaw = mc.thePlayer.rotationYaw;
				while(playerYaw > 180)
					playerYaw -= 360;
				while(playerYaw < -180)
					playerYaw += 360;
			}else
				BuildUtils.advancedBuild(templates.get(template));
			updateLastMS();
			return;
		}
		if(shouldBuild)
			if((hasTimePassedS(speed) || wurst.mods.fastPlaceMod.isActive())
				&& blockIndex < templates.get(template).length)
			{
				BuildUtils.advancedBuildNext(templates.get(template), mouseOver,
					playerYaw, blockIndex);
				if(playerYaw > -45 && playerYaw <= 45)
					try
					{
						if(Block.getIdFromBlock(mc.theWorld
							.getBlockState(new BlockPos(
								BuildUtils.convertPosNext(1, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(1,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(2, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(2,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(3, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(3,
										blockIndex, templates.get(template))))
							.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > 45 && playerYaw <= 135)
					try
					{
						if(Block.getIdFromBlock(mc.theWorld
							.getBlockState(new BlockPos(
								BuildUtils.convertPosNext(1, mouseOver)
									- BuildUtils.convertPosInAdvancedBuiling(3,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(2, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(2,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(3, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(1,
										blockIndex, templates.get(template))))
							.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > 135 || playerYaw <= -135)
					try
					{
						if(Block.getIdFromBlock(mc.theWorld
							.getBlockState(new BlockPos(
								BuildUtils.convertPosNext(1, mouseOver)
									- BuildUtils
										.convertPosInAdvancedBuiling(1,
											blockIndex,
											templates.get(template)),
								BuildUtils.convertPosNext(2, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(2,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(3, mouseOver)
									- BuildUtils.convertPosInAdvancedBuiling(3,
										blockIndex, templates.get(template))))
							.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > -135 && playerYaw <= -45)
					try
					{
						if(Block.getIdFromBlock(mc.theWorld
							.getBlockState(new BlockPos(
								BuildUtils.convertPosNext(1, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(3,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(2, mouseOver)
									+ BuildUtils.convertPosInAdvancedBuiling(2,
										blockIndex, templates.get(template)),
								BuildUtils.convertPosNext(3, mouseOver)
									- BuildUtils.convertPosInAdvancedBuiling(1,
										blockIndex, templates.get(template))))
							.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				updateLastMS();
			}else if(blockIndex == templates.get(template).length)
				shouldBuild = false;
	}
	
	private void buildSimple()
	{
		updateMS();
		if(!shouldBuild
			&& (mc.rightClickDelayTimer == 4
				|| wurst.mods.fastPlaceMod.isActive())
			&& mc.gameSettings.keyBindUseItem.pressed
			&& mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.air)
		{
			if(wurst.mods.fastPlaceMod.isActive())
				speed = 1000000000;
			else
				speed = 5;
			if(wurst.special.yesCheatSpf.getBypassLevel()
				.ordinal() >= BypassLevel.ANTICHEAT.ordinal())
			{
				blockIndex = 0;
				shouldBuild = true;
				mouseOver = mc.objectMouseOver;
				playerYaw = mc.thePlayer.rotationYaw;
				while(playerYaw > 180)
					playerYaw -= 360;
				while(playerYaw < -180)
					playerYaw += 360;
			}else
				BuildUtils.build(templates.get(template));
			updateLastMS();
			return;
		}
		if(shouldBuild)
			if((hasTimePassedS(speed) || wurst.mods.fastPlaceMod.isActive())
				&& blockIndex < templates.get(template).length)
			{
				BuildUtils.buildNext(templates.get(template), mouseOver,
					playerYaw, blockIndex);
				if(playerYaw > -45 && playerYaw <= 45)
					try
					{
						if(Block
							.getIdFromBlock(mc.theWorld
								.getBlockState(new BlockPos(
									mouseOver.getBlockPos().getX()
										+ BuildUtils.convertPosInBuiling(
											1, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getY() + BuildUtils
										.convertPosInBuiling(2, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getZ()
										+ BuildUtils.convertPosInBuiling(3,
											blockIndex, templates.get(template),
											mouseOver)))
								.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > 45 && playerYaw <= 135)
					try
					{
						if(Block
							.getIdFromBlock(mc.theWorld
								.getBlockState(new BlockPos(
									mouseOver.getBlockPos().getX()
										- BuildUtils.convertPosInBuiling(
											3, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getY() + BuildUtils
										.convertPosInBuiling(2, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getZ()
										+ BuildUtils.convertPosInBuiling(1,
											blockIndex, templates.get(template),
											mouseOver)))
								.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > 135 || playerYaw <= -135)
					try
					{
						if(Block
							.getIdFromBlock(mc.theWorld
								.getBlockState(new BlockPos(
									mouseOver.getBlockPos().getX()
										- BuildUtils.convertPosInBuiling(
											1, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getY() + BuildUtils
										.convertPosInBuiling(2, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getZ()
										- BuildUtils.convertPosInBuiling(3,
											blockIndex, templates.get(template),
											mouseOver)))
								.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				else if(playerYaw > -135 && playerYaw <= -45)
					try
					{
						if(Block
							.getIdFromBlock(mc.theWorld
								.getBlockState(new BlockPos(
									mouseOver.getBlockPos().getX()
										+ BuildUtils.convertPosInBuiling(
											3, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getY() + BuildUtils
										.convertPosInBuiling(2, blockIndex,
											templates.get(template), mouseOver),
									mouseOver.getBlockPos().getZ()
										- BuildUtils.convertPosInBuiling(1,
											blockIndex, templates.get(template),
											mouseOver)))
								.getBlock()) != 0)
							blockIndex += 1;
					}catch(NullPointerException e)
					{}// If the current item is null.
				updateLastMS();
			}else if(blockIndex == templates.get(template).length)
				shouldBuild = false;
	}
	
	public int getTemplate()
	{
		return template;
	}
	
	public void setTemplate(int template)
	{
		((ModeSetting)settings.get(0)).setSelected(template);
	}
}
