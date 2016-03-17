/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

@Cmd.Info(help = "Enables, disables or cancels Blink.",
	name = "blink",
	syntax = {"[(on|off|cancel)]"})
public class BlinkCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length > 1)
			syntaxError();
		if(args.length == 0)
			wurst.mods.blinkMod.toggle();
		else if(args[0].equalsIgnoreCase("on"))
		{
			if(!wurst.mods.blinkMod.isEnabled())
				wurst.mods.blinkMod.setEnabled(true);
		}else if(args[0].equalsIgnoreCase("off"))
			wurst.mods.blinkMod.setEnabled(false);
		else if(args[0].equalsIgnoreCase("cancel"))
		{
			if(wurst.mods.blinkMod.isEnabled())
				wurst.mods.blinkMod.cancel();
		}else
			syntaxError();
	}
}
