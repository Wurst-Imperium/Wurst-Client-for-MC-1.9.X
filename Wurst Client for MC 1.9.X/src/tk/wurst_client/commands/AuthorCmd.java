/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import tk.wurst_client.commands.Cmd.Info;

@Info(help = "Changes the held book's author.",
	name = "author",
	syntax = {"<author>"})
public class AuthorCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Cmd.Error
	{
		if(args.length == 0)
			syntaxError();
		if(!mc.thePlayer.capabilities.isCreativeMode)
			error("Creative mode only.");
		ItemStack item = mc.thePlayer.inventory.getCurrentItem();
		if(item == null || Item.getIdFromItem(item.getItem()) != 387)
			error("You are not holding a written book in your hand.");
		String author = args[0];
		for(int i = 1; i < args.length; i++)
			author += " " + args[i];
		item.setTagInfo("author", new NBTTagString(author));
	}
}
