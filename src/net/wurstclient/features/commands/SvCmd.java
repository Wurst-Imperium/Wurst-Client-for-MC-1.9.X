/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.features.commands.Cmd.Info;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.utils.ChatUtils;

@Info(
	description = "Shows the version of the server you are currently playing on.",
	name = "sv",
	syntax = {},
	help = "Commands/sv")
public class SvCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 0)
			syntaxError();
		if(mc.isSingleplayer())
			error("Can't check server version in singleplayer.");
		ChatUtils.message(
			"Server version: " + ServerHook.getLastServerData().gameVersion);
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
