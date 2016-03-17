/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.mods.XRayMod;
import tk.wurst_client.utils.MiscUtils;

@Info(help = "Manages or toggles X-Ray.", name = "xray", syntax = {
	"add (id <block_id>|name <block_name>)",
	"remove (id <block_id>|name <block_name>)", "list [<page>]"})
public class XRayCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length == 0)
			syntaxError();
		else if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				execute(new String[]{"list", "1"});
				return;
			}
			int pages = (int)Math.ceil(XRayMod.xrayBlocks.size() / 8D);
			if(MiscUtils.isInteger(args[1]))
			{
				int page = Integer.valueOf(args[1]);
				if(page > pages || page < 1)
					syntaxError("Invalid page: " + page);
				wurst.chat.message("Current X-Ray blocks: "
					+ XRayMod.xrayBlocks.size());
				wurst.chat.message("X-Ray blocks list (page " + page + "/"
					+ pages + "):");
				Iterator<Block> itr = XRayMod.xrayBlocks.iterator();
				for(int i = 0; itr.hasNext(); i++)
				{
					Block block = itr.next();
					if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
						wurst.chat.message(new ItemStack(Item
							.getItemFromBlock(block)).getDisplayName());
				}
			}else
				syntaxError();
		}else if(args.length < 2)
			syntaxError();
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(args[1].equalsIgnoreCase("id") && MiscUtils.isInteger(args[2]))
			{
				if(tk.wurst_client.mods.XRayMod.xrayBlocks.contains(Block
					.getBlockById(Integer.valueOf(args[2]))))
				{
					wurst.chat.error("\"" + args[2]
						+ "\" is already in your X-Ray blocks list.");
					return;
				}
				tk.wurst_client.mods.XRayMod.xrayBlocks.add(Block
					.getBlockById(Integer.valueOf(args[2])));
				wurst.files.saveXRayBlocks();
				wurst.chat.message("Added block " + args[2] + ".");
				mc.renderGlobal.loadRenderers();
			}else if(args[1].equalsIgnoreCase("name"))
			{
				int newID =
					Block.getIdFromBlock(Block.getBlockFromName(args[2]));
				if(newID == -1)
				{
					wurst.chat.message("The block \"" + args[1]
						+ "\" could not be found.");
					return;
				}
				tk.wurst_client.mods.XRayMod.xrayBlocks.add(Block
					.getBlockById(newID));
				wurst.files.saveXRayBlocks();
				wurst.chat.message("Added block " + newID + " (\"" + args[2]
					+ "\").");
				mc.renderGlobal.loadRenderers();
			}else
				syntaxError();
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(args[1].equalsIgnoreCase("id") && MiscUtils.isInteger(args[2]))
			{
				for(int i = 0; i < tk.wurst_client.mods.XRayMod.xrayBlocks
					.size(); i++)
					if(Integer
						.toString(
							Block
								.getIdFromBlock(tk.wurst_client.mods.XRayMod.xrayBlocks
									.get(i))).toLowerCase()
						.equals(args[2].toLowerCase()))
					{
						tk.wurst_client.mods.XRayMod.xrayBlocks.remove(i);
						wurst.files.saveXRayBlocks();
						wurst.chat.message("Removed block " + args[2] + ".");
						mc.renderGlobal.loadRenderers();
						return;
					}
				wurst.chat.error("Block " + args[2]
					+ " is not in your X-Ray blocks list.");
			}else if(args[1].equalsIgnoreCase("name"))
			{
				int newID =
					Block.getIdFromBlock(Block.getBlockFromName(args[2]));
				if(newID == -1)
				{
					wurst.chat.message("The block \"" + args[2]
						+ "\" could not be found.");
					return;
				}
				for(int i = 0; i < tk.wurst_client.mods.XRayMod.xrayBlocks
					.size(); i++)
					if(Block
						.getIdFromBlock(tk.wurst_client.mods.XRayMod.xrayBlocks
							.get(i)) == newID)
					{
						tk.wurst_client.mods.XRayMod.xrayBlocks.remove(i);
						wurst.files.saveXRayBlocks();
						wurst.chat.message("Removed block " + newID + " (\""
							+ args[2] + "\").");
						mc.renderGlobal.loadRenderers();
						return;
					}
				wurst.chat.error("Block " + newID + " (\"" + args[2]
					+ "\") is not in your X-Ray blocks list.");
			}else
				syntaxError();
		}else
			syntaxError();
	}
}
