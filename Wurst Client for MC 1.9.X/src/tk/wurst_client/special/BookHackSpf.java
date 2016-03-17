/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.special;

import tk.wurst_client.navigator.NavigatorItem;

@Spf.Info(description = "Allows you to insert links that execute commands into writable books. This can be used to\n"
	+ "trick other people (including admins) into executing commands like \"/op YourName\" or\n"
	+ "\"/kill\".",
	name = "BookHack",
	tags = "Force OP,Book Hack,OP Book,command book",
	tutorial = "Special_Features/Force_OP_(BookHack)")
public class BookHackSpf extends Spf
{
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.opSignMod, wurst.mods.forceOpMod,
			wurst.special.sessionStealerSpf};
	}
}
