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
import tk.wurst_client.hooks.ServerHook;

@Info(help = "Shows the version of the server you are currently playing on.",
	name = "sv",
	syntax = {})
public class SvCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 0)
			syntaxError();
		if(mc.isSingleplayer())
			error("Can't check server version in singleplayer.");
		wurst.chat.message("Server version: "
			+ ServerHook.getLastServerData().gameVersion);
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Get Server Version";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".sv", true));
	}
}
