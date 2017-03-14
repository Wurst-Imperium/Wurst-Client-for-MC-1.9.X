/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.wurstclient.events.listeners.*;

public final class EventManager
{
	private final HashMap<Class<? extends EventListener>, ArrayList<? extends EventListener>> listenerMap =
		new HashMap<>();
	
	{
		listenerMap.put(ChatInputListener.class,
			new ArrayList<ChatInputListener>());
		listenerMap.put(ChatOutputListener.class,
			new ArrayList<ChatOutputListener>());
		listenerMap.put(DeathListener.class, new ArrayList<DeathListener>());
		listenerMap.put(GUIRenderListener.class,
			new ArrayList<GUIRenderListener>());
		listenerMap.put(LeftClickListener.class,
			new ArrayList<LeftClickListener>());
		listenerMap.put(PacketInputListener.class,
			new ArrayList<PacketInputListener>());
		listenerMap.put(PacketOutputListener.class,
			new ArrayList<PacketOutputListener>());
		listenerMap.put(RenderListener.class, new ArrayList<RenderListener>());
		listenerMap.put(UpdateListener.class, new ArrayList<UpdateListener>());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Event> void fire(T event)
	{
		try
		{
			event.fire(listenerMap.get(event.getListenerType()));
		}catch(Throwable e)
		{
			e.printStackTrace();
			CrashReport crashReport =
				CrashReport.makeCrashReport(e, "Firing Wurst event");
			CrashReportCategory crashreportcategory =
				crashReport.makeCategory("Affected event");
			crashreportcategory.addCrashSectionCallable("Event class", () -> {
				return event.getClass().getName();
			});
			throw new ReportedException(crashReport);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EventListener> void add(Class<T> type, T listener)
	{
		try
		{
			((ArrayList<T>)listenerMap.get(type)).add(listener);
		}catch(Throwable e)
		{
			e.printStackTrace();
			CrashReport crashReport =
				CrashReport.makeCrashReport(e, "Adding Wurst event listener");
			CrashReportCategory crashreportcategory =
				crashReport.makeCategory("Affected listener");
			crashreportcategory.addCrashSectionCallable("Listener type",
				() -> {
					return type.getName();
				});
			crashreportcategory.addCrashSectionCallable("Listener class",
				() -> {
					return listener.getClass().getName();
				});
			throw new ReportedException(crashReport);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EventListener> void remove(Class<T> type, T listener)
	{
		try
		{
			((ArrayList<T>)listenerMap.get(type)).remove(listener);
		}catch(Throwable e)
		{
			e.printStackTrace();
			CrashReport crashReport =
				CrashReport.makeCrashReport(e, "Removing Wurst event listener");
			CrashReportCategory crashreportcategory =
				crashReport.makeCategory("Affected listener");
			crashreportcategory.addCrashSectionCallable("Listener type",
				() -> {
					return type.getName();
				});
			crashreportcategory.addCrashSectionCallable("Listener class",
				() -> {
					return listener.getClass().getName();
				});
			throw new ReportedException(crashReport);
		}
	}
}
