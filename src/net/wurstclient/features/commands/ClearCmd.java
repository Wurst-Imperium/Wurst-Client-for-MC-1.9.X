/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.features.commands.Cmd.Info;

@Info(description = "Clears the chat completely.",
	name = "clear",
	syntax = {},
	help = "Commands/clear")
public class ClearCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			mc.ingameGUI.getChatGUI().clearChatMessages();
		else
			syntaxError();
	}
}
