/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.spam.exceptions;

import tk.wurst_client.spam.tag.Tag;

public class InvalidArgumentException extends TagException
{
	public InvalidArgumentException(String message, int line, Tag tag)
	{
		super(message, line, tag);
	}
}
