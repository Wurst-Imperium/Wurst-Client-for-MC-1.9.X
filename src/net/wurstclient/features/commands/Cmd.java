/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.WurstClient;
import net.wurstclient.features.Feature;
import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.settings.Setting;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.MiscUtils;

public abstract class Cmd implements Feature
{
	private String name = getClass().getAnnotation(Info.class).name();
	private String description =
		getClass().getAnnotation(Info.class).description();
	private String[] syntax = getClass().getAnnotation(Info.class).syntax();
	private String tags = getClass().getAnnotation(Info.class).tags();
	private String help = getClass().getAnnotation(Info.class).help();
	
	protected static final WurstClient wurst = WurstClient.INSTANCE;
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Info
	{
		String name();
		
		String description();
		
		String[] syntax();
		
		String tags() default "";
		
		String help() default "";
	}
	
	public class CmdSyntaxError extends CmdError
	{
		public CmdSyntaxError()
		{
			super();
		}
		
		public CmdSyntaxError(String message)
		{
			super(message);
		}
	}
	
	public class CmdError extends Throwable
	{
		public CmdError()
		{
			super();
		}
		
		public CmdError(String message)
		{
			super(message);
		}
	}
	
	public final String getCmdName()
	{
		return name;
	}
	
	public final String[] getSyntax()
	{
		return syntax;
	}
	
	@Override
	public final String getName()
	{
		return "." + name;
	}
	
	@Override
	public final String getType()
	{
		return "Command";
	}
	
	@Override
	public final String getDescription()
	{
		String description = this.description;
		if(syntax.length > 0)
			description += "\n\nSyntax:";
		for(String element : syntax)
			description += "\n  ." + name + " " + element;
		return description;
	}
	
	@Override
	public final boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public final boolean isBlocked()
	{
		return false;
	}
	
	@Override
	public final String getTags()
	{
		return tags;
	}
	
	@Override
	public final ArrayList<Setting> getSettings()
	{
		return new ArrayList<Setting>();
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		return new ArrayList<>();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{
		
	}
	
	@Override
	public final String getHelpPage()
	{
		return help;
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[0];
	}
	
	public final void printHelp()
	{
		for(String line : description.split("\n"))
			ChatUtils.message(line);
	}
	
	public final void printSyntax()
	{
		String output = "§o." + name + "§r";
		if(syntax.length != 0)
		{
			output += " " + syntax[0];
			for(int i = 1; i < syntax.length; i++)
				output += "\n    " + syntax[i];
		}
		for(String line : output.split("\n"))
			ChatUtils.message(line);
	}
	
	protected final int[] argsToPos(String... args) throws Cmd.CmdError
	{
		int[] pos = new int[3];
		if(args.length == 3)
		{
			int[] playerPos =
				new int[]{(int)Minecraft.getMinecraft().thePlayer.posX,
					(int)Minecraft.getMinecraft().thePlayer.posY,
					(int)Minecraft.getMinecraft().thePlayer.posZ};
			for(int i = 0; i < args.length; i++)
				if(MiscUtils.isInteger(args[i]))
					pos[i] = Integer.parseInt(args[i]);
				else if(args[i].startsWith("~"))
					if(args[i].equals("~"))
						pos[i] = playerPos[i];
					else if(MiscUtils.isInteger(args[i].substring(1)))
						pos[i] = playerPos[i]
							+ Integer.parseInt(args[i].substring(1));
					else
						syntaxError("Invalid coordinates.");
				else
					syntaxError("Invalid coordinates.");
		}else if(args.length == 1)
		{
			EntityLivingBase entity =
				EntityUtils.searchEntityByNameRaw(args[0]);
			if(entity == null)
				error("Entity \"" + args[0] + "\" could not be found.");
			BlockPos blockPos = new BlockPos(entity);
			pos = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()};
		}else
			syntaxError("Invalid coordinates.");
		return pos;
	}
	
	protected final void syntaxError() throws CmdSyntaxError
	{
		throw new CmdSyntaxError();
	}
	
	protected final void syntaxError(String message) throws CmdSyntaxError
	{
		throw new CmdSyntaxError(message);
	}
	
	protected final void error(String message) throws CmdError
	{
		throw new CmdError(message);
	}
	
	public abstract void execute(String[] args) throws CmdError;
}
