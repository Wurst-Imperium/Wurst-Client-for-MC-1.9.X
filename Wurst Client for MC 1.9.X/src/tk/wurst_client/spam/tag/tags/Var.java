/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.spam.tag.tags;

import tk.wurst_client.spam.SpamProcessor;
import tk.wurst_client.spam.exceptions.InvalidArgumentException;
import tk.wurst_client.spam.exceptions.MissingArgumentException;
import tk.wurst_client.spam.exceptions.SpamException;
import tk.wurst_client.spam.tag.Tag;
import tk.wurst_client.spam.tag.TagData;

public class Var extends Tag
{
	public Var()
	{
		super("var", "Defines a new variable.", "<var name>value</var>",
			"<var link>example.com</var><!--\n"
				+ "-->Check out my website: §link;");
	}
	
	@Override
	public String process(TagData tagData) throws SpamException
	{
		if(tagData.getTagArgs().length == 0)
			throw new MissingArgumentException(
				"The <var> tag requires at least one argument.",
				tagData.getTagLine(), this);
		if(tagData.getTagArgs()[0].startsWith("_"))
			throw new InvalidArgumentException(
				"You cannot define variables that start with \"_\".",
				tagData.getTagLine(), this);
		SpamProcessor.varManager.addUserVar(tagData.getTagArgs()[0],
			tagData.getTagContent());
		return "";
	}
}
