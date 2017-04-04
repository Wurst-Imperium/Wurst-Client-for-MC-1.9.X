/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.navigator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

import net.wurstclient.WurstClient;
import net.wurstclient.analytics.AnalyticsManager;
import net.wurstclient.features.Feature;

public final class Navigator
{
	private final ArrayList<Feature> navigatorList = new ArrayList<>();
	private final HashMap<String, Long> preferences = new HashMap<>();
	public AnalyticsManager analytics = new AnalyticsManager("UA-52838431-7",
		"navigator.client.wurstclient.net");
	
	public Navigator()
	{
		navigatorList.addAll(WurstClient.INSTANCE.mods.getAllMods());
		navigatorList.addAll(WurstClient.INSTANCE.commands.getAllCommands());
		navigatorList.addAll(WurstClient.INSTANCE.special.getAllFeatures());
	}
	
	public void copyNavigatorList(ArrayList<Feature> list)
	{
		if(!list.equals(navigatorList))
		{
			list.clear();
			list.addAll(navigatorList);
		}
	}
	
	public void getSearchResults(ArrayList<Feature> list, String query)
	{
		// clear display list
		list.clear();
		
		// add search results
		for(Feature mod : navigatorList)
			if(mod.getName().toLowerCase().contains(query)
				|| mod.getTags().toLowerCase().contains(query)
				|| mod.getDescription().toLowerCase().contains(query))
				list.add(mod);
			
		// sort search results
		list.sort(new Comparator<Feature>()
		{
			@Override
			public int compare(Feature o1, Feature o2)
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
	
	public void forEach(Consumer<Feature> action)
	{
		navigatorList.forEach(action);
	}
	
	public Iterator<Feature> iterator()
	{
		return navigatorList.iterator();
	}
	
	public void sortFeatures()
	{
		navigatorList.sort((o1, o2) -> {
			long preference1 = getPreference(o1.getName());
			long preference2 = getPreference(o2.getName());
			
			if(preference1 < preference2)
				return 1;
			else if(preference1 > preference2)
				return -1;
			else
				return 0;
		});
	}
	
	public int countAllFeatures()
	{
		return navigatorList.size();
	}
}
