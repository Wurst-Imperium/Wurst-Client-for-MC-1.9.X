/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.navigator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

import tk.wurst_client.WurstClient;
import tk.wurst_client.analytics.AnalyticsManager;
import tk.wurst_client.commands.CmdManager;
import tk.wurst_client.mods.ModManager;
import tk.wurst_client.special.SpfManager;

public class Navigator
{
	private ArrayList<NavigatorItem> navigatorList = new ArrayList<>();
	private final HashMap<String, Long> preferences = new HashMap<>();
	public AnalyticsManager analytics = new AnalyticsManager("UA-52838431-7",
		"navigator.client.wurst-client.tk");
	
	public Navigator()
	{
		// add mods
		Field[] modFields = ModManager.class.getFields();
		try
		{
			for(Field field : modFields)
			{
				if(field.getName().endsWith("Mod"))
					navigatorList.add((NavigatorItem)field
						.get(WurstClient.INSTANCE.mods));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// add commands
		Field[] cmdFields = CmdManager.class.getFields();
		try
		{
			for(Field field : cmdFields)
			{
				if(field.getName().endsWith("Cmd"))
					navigatorList.add((NavigatorItem)field
						.get(WurstClient.INSTANCE.commands));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// add special features
		Field[] specialFields = SpfManager.class.getFields();
		try
		{
			for(Field field : specialFields)
			{
				if(field.getName().endsWith("Spf"))
					navigatorList.add((NavigatorItem)field
						.get(WurstClient.INSTANCE.special));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void copyNavigatorList(ArrayList<NavigatorItem> list)
	{
		if(!list.equals(navigatorList))
		{
			list.clear();
			list.addAll(navigatorList);
		}
	}
	
	public void getSearchResults(ArrayList<NavigatorItem> list, String query)
	{
		// clear display list
		list.clear();
		
		// add search results
		for(NavigatorItem mod : navigatorList)
			if(mod.getName().toLowerCase().contains(query)
				|| mod.getTags().toLowerCase().contains(query)
				|| mod.getDescription().toLowerCase().contains(query))
				list.add(mod);
		
		// sort search results
		list.sort(new Comparator<NavigatorItem>()
		{
			@Override
			public int compare(NavigatorItem o1, NavigatorItem o2)
			{
				// compare names
				int result = compareNext(o1.getName(), o2.getName());
				if(result != 0)
					return result;
				
				// compare tags
				result = compareNext(o1.getTags(), o2.getTags());
				if(result != 0)
					return result;
				
				// compare descriptions
				result = compareNext(o1.getDescription(), o2.getDescription());
				return result;
			}
			
			private int compareNext(String o1, String o2)
			{
				int index1 = o1.toLowerCase().indexOf(query);
				int index2 = o2.toLowerCase().indexOf(query);
				
				if(index1 == index2)
					return 0;
				else if(index1 == -1)
					return 1;
				else if(index2 == -1)
					return -1;
				else
					return index1 - index2;
			}
		});
	}
	
	public long getPreference(String feature)
	{
		Long preference = preferences.get(feature);
		if(preference == null)
			preference = 0L;
		return preference;
	}
	
	public void addPreference(String feature)
	{
		Long preference = preferences.get(feature);
		if(preference == null)
			preference = 0L;
		preference++;
		preferences.put(feature, preference);
	}
	
	public void setPreference(String feature, long preference)
	{
		preferences.put(feature, preference);
	}
	
	public void forEach(Consumer<NavigatorItem> action)
	{
		navigatorList.forEach(action);
	}
	
	public Iterator<NavigatorItem> iterator()
	{
		return navigatorList.iterator();
	}

	public void sortFeatures()
	{
		navigatorList.sort(new Comparator<NavigatorItem>()
		{
			@Override
			public int compare(NavigatorItem o1, NavigatorItem o2)
			{
				long preference1 = getPreference(o1.getName());
				long preference2 = getPreference(o2.getName());
				if(preference1 < preference2)
					return 1;
				else if(preference1 > preference2)
					return -1;
				else
					return 0;
			}
		});
	}
	
	public int countAllFeatures()
	{
		return navigatorList.size();
	}
}
