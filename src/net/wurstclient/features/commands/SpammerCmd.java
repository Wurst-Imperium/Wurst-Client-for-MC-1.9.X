/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.features.commands.Cmd.Info;
import net.wurstclient.features.mods.SpammerMod;
import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@Info(description = "Changes the delay of Spammer or spams spam from a file.",
	name = "spammer",
	syntax = {"delay <delay_in_ms>", "spam <file>"},
	help = "Commands/spammer")
public class SpammerCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
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
			ChatUtils.message("Spammer delay set to " + newDelay + "ms.");
		}else if(args[0].equalsIgnoreCase("spam"))
			if(!SpamProcessor.runSpam(args[1]))
				ChatUtils.error("File does not exist.");
	}
}
