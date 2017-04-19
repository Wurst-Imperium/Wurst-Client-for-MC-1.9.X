/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.options;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

public class KeybindManager extends TreeMap<String, TreeSet<String>>
{
	public KeybindManager()
	{
		put("B", ".t fastbreak", ".t fastplace");
		put("C", ".t fullbright");
		put("G", ".t flight");
		put("GRAVE", ".t speednuker");
		put("H", ".t /home");
		put("J", ".t jesus");
		put("K", ".t multiaura");
		put("L", ".t nuker");
		put("LCONTROL", ".t navigator");
		put("R", ".t killaura");
		put("RSHIFT", ".t navigator");
		put("U", ".t freecam");
		put("X", ".t x-ray");
		put("Z", ".t sneak");
	}
	
	public void put(String key, String... commands)
	{
		put(key, new TreeSet<>(Arrays.asList(commands)));
	}
}
