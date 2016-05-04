/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

public class ChatInputEvent extends CancellableEvent
{
	private ITextComponent component;
	private List<ChatLine> chatLines;
	
	public ChatInputEvent(ITextComponent component, List<ChatLine> chatLines)
	{
		this.component = component;
		this.chatLines = chatLines;
	}
	
	public ITextComponent getComponent()
	{
		return component;
	}
	
	public void setComponent(ITextComponent component)
	{
		this.component = component;
	}
	
	public List<ChatLine> getChatLines()
	{
		return chatLines;
	}
	
	@Override
	public String getAction()
	{
		return "receiving chat message";
	}
	
	@Override
	public String getComment()
	{
		return "Message: `" + component.getUnformattedText() + "`";
	}
}
