/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import net.minecraft.client.Minecraft;

public class RenderEvent extends Event
{
	@Override
	public String getAction()
	{
		return "rendering GUI";
	}
	
	@Override
	public String getComment()
	{
		String comment = "GUI screen: ";
		if(Minecraft.getMinecraft().currentScreen != null)
			comment +=
				Minecraft.getMinecraft().currentScreen.getClass()
					.getSimpleName();
		else
			comment += "null";
		return comment;
	}
}
