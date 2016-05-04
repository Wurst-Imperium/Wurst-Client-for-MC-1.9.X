/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

public class ChatOutputEvent extends CancellableEvent
{
	private String message;
	private boolean automatic;
	
	public ChatOutputEvent(String message, boolean automatic)
	{
		this.message = message;
		this.automatic = automatic;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	@Override
	public String getAction()
	{
		return "sending chat message";
	}
	
	@Override
	public String getComment()
	{
		return "Message: `" + message + "`";
	}
	
	public boolean isAutomatic()
	{
		return automatic;
	}
}
