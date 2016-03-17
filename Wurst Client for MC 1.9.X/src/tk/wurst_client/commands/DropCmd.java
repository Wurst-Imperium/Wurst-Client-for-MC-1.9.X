/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import java.util.Random;

import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.events.listeners.UpdateListener;

@Info(help = "Drops all your items on the ground.",
	name = "drop",
	syntax = {"[infinite]"})
public class DropCmd extends Cmd implements UpdateListener
{
	private int timer;
	private int counter;
	private boolean infinite;
	
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length > 1)
			syntaxError();
		if(args.length == 1)
			if(args[0].equalsIgnoreCase("infinite"))
				infinite = !infinite;
			else
				syntaxError();
		else
			infinite = false;
		timer = 0;
		counter = 9;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(infinite)
		{
			Item item = null;
			while(item == null)
				item = Item.getItemById(new Random().nextInt(431));
			mc.thePlayer.sendQueue
				.addToSendQueue(new CPacketCreativeInventoryAction(-1,
					new ItemStack(item, 64)));
			return;
		}
		if(wurst.mods.yesCheatMod.isActive())
		{
			timer++;
			if(timer >= 5)
			{
				mc.playerController.windowClick(0, counter, 1, ClickType.THROW,
					mc.thePlayer);
				counter++;
				timer = 0;
				if(counter >= 45)
					wurst.events.remove(UpdateListener.class, this);
			}
		}else
		{
			for(int i = 9; i < 45; i++)
				mc.playerController.windowClick(0, i, 1, ClickType.THROW,
					mc.thePlayer);
			wurst.events.remove(UpdateListener.class, this);
		}
	}
}
