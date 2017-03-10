/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import java.util.ArrayList;

import tk.wurst_client.events.listeners.RenderListener;

public class RenderEvent extends Event<RenderListener>
{
	public static final RenderEvent INSTANCE = new RenderEvent();
	
	@Override
	public void fire(ArrayList<RenderListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).onRender();
	}

	@Override
	public Class<RenderListener> getListenerType()
	{
		return RenderListener.class;
	}
}
