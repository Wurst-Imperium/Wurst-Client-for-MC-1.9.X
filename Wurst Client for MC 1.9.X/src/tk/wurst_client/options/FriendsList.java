/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.options;

import java.util.TreeSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import tk.wurst_client.WurstClient;

public class FriendsList extends TreeSet<String>
{
	public void middleClick(Entity entityHit)
	{
		if(entityHit != null && entityHit instanceof EntityPlayer)
		{
			WurstClient wurst = WurstClient.INSTANCE;
			if(wurst.options.middleClickFriends)
			{
				FriendsList friends = wurst.friends;
				String entityName = entityHit.getName();
				if(friends.contains(entityName))
					friends.remove(entityName);
				else
					friends.add(entityName);
				wurst.files.saveFriends();
			}
		}
	}
}
