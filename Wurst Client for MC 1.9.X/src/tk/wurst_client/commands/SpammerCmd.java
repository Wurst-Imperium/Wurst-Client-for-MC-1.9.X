/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.mods.SpammerMod;
import tk.wurst_client.spam.SpamProcessor;
import tk.wurst_client.utils.MiscUtils;

@Info(help = "Changes the delay of Spammer or spams spam from a file.",
	name = "spammer",
	syntax = {"delay <delay_in_ms>", "spam <file>"})
public class SpammerCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 2)
			syntaxError();
		if(args[0].equalsIgnoreCase("delay"))
		{
			if(!MiscUtils.isInteger(args[1]))
				syntaxError();
			int newDelay = Integer.parseInt(args[1]);
			if(newDelay % 50 > 0)
				newDelay = newDelay - newDelay % 50;
			wurst.options.spamDelay = newDelay;
			SpammerMod.updateDelaySpinner();
			wurst.chat.message("Spammer delay set to " + newDelay + "ms.");
		}else if(args[0].equalsIgnoreCase("spam"))
			if(!SpamProcessor.runSpam(args[1]))
				wurst.chat.error("File does not exist.");
	}
}
