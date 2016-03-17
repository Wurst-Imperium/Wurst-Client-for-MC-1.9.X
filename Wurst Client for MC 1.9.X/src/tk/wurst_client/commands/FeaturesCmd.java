/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.events.ChatOutputEvent;
import tk.wurst_client.mods.Mod;
import tk.wurst_client.special.Spf;

@Info(help = "Shows the feature count and some over statistics.",
	name = "features",
	syntax = {})
public class FeaturesCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 0)
			syntaxError();
		
		wurst.chat.message("> All features: "
			+ wurst.navigator.countAllFeatures());
		wurst.chat.message("> Mods: " + wurst.mods.countMods());
		wurst.chat.message("> Commands: " + wurst.commands.countCommands());
		wurst.chat.message("> Special features: "
			+ wurst.special.countFeatures());
		int settings = 0, bypasses = 0;
		for(Mod mod : wurst.mods.getAllMods())
		{
			settings += mod.getSettings().size();
			if(mod.getClass().getAnnotation(Mod.Info.class).noCheatCompatible())
				bypasses++;
		}
		wurst.chat.message("> NoCheat bypasses (mods only): " + bypasses);
		for(Cmd cmd : wurst.commands.getAllCommands())
			settings += cmd.getSettings().size();
		for(Spf spf : wurst.special.getAllFeatures())
			settings += spf.getSettings().size();
		wurst.chat.message("> Settings: " + settings);
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Show Statistics";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".features", true));
	}
}
