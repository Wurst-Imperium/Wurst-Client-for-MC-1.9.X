/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.spam.tag;

import java.util.ArrayList;

import tk.wurst_client.spam.exceptions.InvalidTagException;
import tk.wurst_client.spam.exceptions.SpamException;
import tk.wurst_client.spam.tag.tags.Random;
import tk.wurst_client.spam.tag.tags.Repeat;
import tk.wurst_client.spam.tag.tags.Var;

public class TagManager
{
	private final ArrayList<Tag> activeTags = new ArrayList<Tag>();
	
	public Tag getTagByName(String name, int line) throws SpamException
	{
		for(int i = 0; i < activeTags.size(); i++)
			if(activeTags.get(i).getName().equals(name))
				return activeTags.get(i);
		throw new InvalidTagException(name, line);
	}
	
	public ArrayList<Tag> getActiveTags()
	{
		return activeTags;
	}
	
	public String process(TagData tagData) throws SpamException
	{
		Tag tag = getTagByName(tagData.getTagName(), tagData.getTagLine());
		String processedTag = tag.process(tagData);
		return processedTag;
	}
	
	public TagManager()
	{
		activeTags.add(new Random());
		activeTags.add(new Repeat());
		activeTags.add(new Var());
	}
}
