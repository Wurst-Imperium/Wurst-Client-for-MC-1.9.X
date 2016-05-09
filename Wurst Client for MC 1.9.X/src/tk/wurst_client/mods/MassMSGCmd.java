/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;
import tk.wurst_client.commands.Cmd.Info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import net.minecraft.network.play.client.CPacketChatMessage;

@Info(help = "Allows you to spam other players text.",
	name = "massmsg",
	syntax = {"/<command> <message>"},
public class MassTpaMod extends Cmd
{
  private string message = "";

	private float speed = 1F;
	private int i;
	private ArrayList<String> players;
	private Random random = new Random();
  private string name = "";
  
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length < 1)
		SynatxError();
		
		
		// Enable
		if(args[0] != "Continue"){
		
		i = 0;
		Iterator itr = mc.getNetHandler().getPlayerInfoMap().iterator();
		players = new ArrayList<String>();
		while(itr.hasNext())
			players.add(StringUtils.stripControlCodes(((NetworkPlayerInfo)itr
				.next()).getPlayerNameForReal()));
		Collections.shuffle(players, random);
		
		message = args[0] + "⊇" // I made this ⊇ because players can't use this.
		
		for(int i = 1; i < args.length; i++)
				message += " " + args[i];
		
		
		// Start Continue
		mc.thePlayer
		.sendAutomaticChatMessage(".massmsg Continue");
			}else if(args[0] = "Continue"){
			updateMS();
		if(hasTimePassedS(speed))
		{
			name = players.get(i);
			if(name.equals(mc.thePlayer.getName())){
			i++;
			name = players.get(i);
			}
			//	mc.thePlayer.sendChatMessage(args[0] + name);
		//		message = (args[0] + name);
				
		//		for(int i = 1; i < args.length; i++)
			//	message += " " + args[i];
		//	mc.thePlayer.sendQueue.addToSendQueue(new CPacketChatMessage(message));
		
		
			mc.thePlayer.sendChatMessage(message.replace("⊇", name));
		
			updateLastMS();
			i++;
			if(i >= players.size()){
				
				// end
				message = "";
				i = 0;
				name = "";
				Arrays.fill(players, null);
				random = new Random();
				}else{
				// Restart
				mc.thePlayer
		.sendAutomaticChatMessage(".massmsg Continue");
				}
		}
			
			}else
			SyntaxError();
			
		
	}
}
