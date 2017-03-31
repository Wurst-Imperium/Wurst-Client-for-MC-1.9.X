/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.List;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.wurstclient.features.commands.Cmd.Info;
import net.wurstclient.utils.MiscUtils;

@Info(description = "Changes the effects of the held potion.",
	name = "potion",
	syntax = {"add (<effect> <amplifier> <duration>)...",
		"set (<effect> <amplifier> <duration>)...", "remove <effect>"},
	help = "Commands/potion")
public class PotionCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		if(!mc.thePlayer.capabilities.isCreativeMode)
			error("Creative mode only.");
		
		ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
		if(currentItem == null || !(currentItem.getItem() instanceof ItemPotion
			|| currentItem.getItem() instanceof ItemSplashPotion))
			error("You are not holding a potion in your hand.");
		
		NBTTagList newEffects = new NBTTagList();
		
		// remove
		if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length != 2)
				syntaxError();
			int id = 0;
			id = parsePotionEffectId(args[1]);
			List<PotionEffect> oldEffects =
				PotionUtils.getEffectsFromStack(currentItem);
			if(oldEffects != null)
				for(int i = 0; i < oldEffects.size(); i++)
				{
					PotionEffect temp = oldEffects.get(i);
					if(Potion.getIdFromPotion(temp.getPotion()) != id)
					{
						NBTTagCompound effect = new NBTTagCompound();
						effect.setInteger("Id",
							Potion.getIdFromPotion(temp.getPotion()));
						effect.setInteger("Amplifier", temp.getAmplifier());
						effect.setInteger("Duration", temp.getDuration());
						newEffects.appendTag(effect);
					}
				}
			currentItem.setTagInfo("CustomPotionEffects", newEffects);
			return;
		}else if((args.length - 1) % 3 != 0)
			syntaxError();
		
		// add
		if(args[0].equalsIgnoreCase("add"))
		{
			List<PotionEffect> oldEffects =
				PotionUtils.getEffectsFromStack(currentItem);
			if(oldEffects != null)
				for(int i = 0; i < oldEffects.size(); i++)
				{
					PotionEffect temp = oldEffects.get(i);
					NBTTagCompound effect = new NBTTagCompound();
					effect.setInteger("Id",
						Potion.getIdFromPotion(temp.getPotion()));
					effect.setInteger("Amplifier", temp.getAmplifier());
					effect.setInteger("Duration", temp.getDuration());
					newEffects.appendTag(effect);
				}
		}else if(!args[0].equalsIgnoreCase("set"))
			syntaxError();
		
		// add & set
		for(int i = 0; i < (args.length - 1) / 3; i++)
		{
			int id = parsePotionEffectId(args[1 + i * 3]);
			int amplifier = 0;
			int duration = 0;
			
			if(MiscUtils.isInteger(args[2 + i * 3])
				&& MiscUtils.isInteger(args[3 + i * 3]))
			{
				amplifier = Integer.parseInt(args[2 + i * 3]) - 1;
				duration = Integer.parseInt(args[3 + i * 3]);
			}else
				syntaxError();
			
			NBTTagCompound effect = new NBTTagCompound();
			effect.setInteger("Id", id);
			effect.setInteger("Amplifier", amplifier);
			effect.setInteger("Duration", duration * 20);
			newEffects.appendTag(effect);
		}
		System.out.println(newEffects);
		currentItem.setTagInfo("CustomPotionEffects", newEffects);
	}
	
	public int parsePotionEffectId(String input) throws CmdSyntaxError
	{
		int id = 0;
		try
		{
			id = Integer.parseInt(input);
		}catch(NumberFormatException var11)
		{
			try
			{
				id = Potion.getIdFromPotion(
					Potion.getPotionFromResourceLocation(input));
			}catch(NullPointerException e)
			{
				syntaxError();
			}
		}
		if(id < 1)
			syntaxError();
		return id;
	}
}
