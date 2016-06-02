/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import java.util.EventListener;
import java.util.concurrent.Callable;

import javax.swing.event.EventListenerList;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import tk.wurst_client.events.listeners.*;

public final class EventManager
{
	private static final EventListenerList listenerList =
		new EventListenerList();
	
	public synchronized <T extends Event> void fireEvent(Class<T> type, T event)
	{
		try
		{
			// TODO: A more efficient way to process the type
			if(type == GUIRenderEvent.class)
				fireGuiRender();
			else if(type == RenderEvent.class)
				fireRender();
			else if(type == PacketInputEvent.class)
				firePacketInput((PacketInputEvent)event);
			else if(type == UpdateEvent.class)
				fireUpdate();
			else if(type == ChatInputEvent.class)
				fireChatInput((ChatInputEvent)event);
			else if(type == ChatOutputEvent.class)
				fireChatOutput((ChatOutputEvent)event);
			else if(type == LeftClickEvent.class)
				fireLeftClick();
			else if(type == DeathEvent.class)
				fireDeath();
			else
				throw new IllegalArgumentException("Invalid event type: "
					+ type.getName());
		}catch(Throwable e)
		{
			CrashReport crashReport =
				CrashReport.makeCrashReport(e, "Processing Wurst event");
			CrashReportCategory crashreportcategory =
				crashReport.makeCategory("Affected event");
			crashreportcategory.addCrashSectionCallable("Event type",
				new Callable<String>()
				{
					@Override
					public String call() throws Exception
					{
						return type.getName();
					}
				});
			throw new ReportedException(crashReport);
		}
	}
	
	private void fireChatInput(ChatInputEvent event)
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == ChatInputListener.class)
				((ChatInputListener)listeners[i + 1]).onReceivedMessage(event);
			if(event.isCancelled())
				break;
		}
	}
	
	private void fireChatOutput(ChatOutputEvent event)
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == ChatOutputListener.class)
				((ChatOutputListener)listeners[i + 1]).onSentMessage(event);
			if(event.isCancelled())
				break;
		}
	}
	
	private void fireDeath()
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == DeathListener.class)
				((DeathListener)listeners[i + 1]).onDeath();
	}
	
	private void fireGuiRender()
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == GUIRenderListener.class)
				((GUIRenderListener)listeners[i + 1]).onRenderGUI();
	}
	
	private void fireLeftClick()
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == LeftClickListener.class)
				((LeftClickListener)listeners[i + 1]).onLeftClick();
	}
	
	private void firePacketInput(PacketInputEvent event)
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == PacketInputListener.class)
				((PacketInputListener)listeners[i + 1]).onReceivedPacket(event);
	}
	
	private void fireRender()
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == RenderListener.class)
				((RenderListener)listeners[i + 1]).onRender();
	}
	
	private void fireUpdate()
	{
		Object[] listeners = listenerList.getListenerList();
		for(int i = listeners.length - 2; i >= 0; i -= 2)
			if(listeners[i] == UpdateListener.class)
				((UpdateListener)listeners[i + 1]).onUpdate();
	}
	
	public <T extends EventListener> void add(Class<T> type, T listener)
	{
		listenerList.add(type, listener);
	}
	
	public <T extends EventListener> void remove(Class<T> type, T listener)
	{
		listenerList.remove(type, listener);
	}
}
