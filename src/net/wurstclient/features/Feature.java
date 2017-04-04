/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.settings.Setting;

public abstract class Feature
{
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	public abstract String getName();
	
	public abstract String getType();
	
	public abstract String getDescription();
	
	public abstract boolean isEnabled();
	
	public abstract boolean isBlocked();
	
	public abstract String getTags();
	
	public abstract ArrayList<Setting> getSettings();
	
	public abstract ArrayList<PossibleKeybind> getPossibleKeybinds();
	
	public abstract String getPrimaryAction();
	
	public abstract void doPrimaryAction();
	
	public abstract String getHelpPage();
	
	public abstract Feature[] getSeeAlso();
}
