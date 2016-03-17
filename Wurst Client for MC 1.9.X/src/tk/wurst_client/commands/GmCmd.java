/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;

@Info(help = "Types \"/gamemode <args>\".\nUseful for servers that don't support /gm.",
	name = "gm",
	syntax = {"<gamemode>"})
public class GmCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 1)
			syntaxError();
		mc.thePlayer.sendChatMessage("/gamemode " + args[0]);
	}
}
