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

@Info(help = "Does nothing. Useful for scripting.",
	name = "nothing",
	syntax = {})
public class NothingCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{	
		
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Do Nothing";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".nothing", true));
	}
}
