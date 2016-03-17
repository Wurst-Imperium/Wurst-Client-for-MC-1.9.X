/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.events.ChatOutputEvent;

@Cmd.Info(help = "Makes you jump once.", name = "jump", syntax = {})
public class JumpCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 0)
			syntaxError();
		if(!mc.thePlayer.onGround)
			error("Can't jump in mid-air.");
		mc.thePlayer.jump();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Jump";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".jump", true));
	}
}
