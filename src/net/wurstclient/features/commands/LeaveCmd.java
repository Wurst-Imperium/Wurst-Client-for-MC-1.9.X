/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.utils.ChatUtils;

@Cmd.Info(
	description = "Leaves the current server or changes the mode of AutoLeave.",
	name = "leave",
	syntax = {"[chars|tp|selfhurt|quit]", "mode chars|tp|selfhurt|quit"},
	help = "Commands/leave")
public class LeaveCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length > 2)
			syntaxError();
		if(mc.isIntegratedServerRunning()
			&& mc.thePlayer.sendQueue.getPlayerInfoMap().size() == 1)
			error("Cannot leave server when in singleplayer.");
		switch(args.length)
		{
			case 0:
			disconnectWithMode(wurst.mods.autoLeaveMod.getMode());
			break;
			case 1:
			if(args[0].equalsIgnoreCase("taco"))
				for(int i = 0; i < 128; i++)
					mc.thePlayer.sendAutomaticChatMessage("Taco!");
			else
				disconnectWithMode(parseMode(args[0]));
			break;
			case 2:
			wurst.mods.autoLeaveMod.setMode(parseMode(args[1]));
			wurst.files.saveOptions();
			ChatUtils.message("AutoLeave mode set to \"" + args[1] + "\".");
			break;
			default:
			break;
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Leave";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".leave", true));
	}
	
	private void disconnectWithMode(int mode)
	{
		switch(mode)
		{
			case 0:
			mc.theWorld.sendQuittingDisconnectingPacket();
			break;
			case 1:
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketChatMessage("§"));
			break;
			case 2:
			mc.thePlayer.sendQueue.addToSendQueue(
				new C04PacketPlayerPosition(3.1e7d, 100, 3.1e7d, false));
			case 3:
			mc.thePlayer.sendQueue.addToSendQueue(
				new CPacketUseEntity(mc.thePlayer, EnumHand.MAIN_HAND));
			break;
			default:
			break;
		}
	}
	
	private int parseMode(String input) throws CmdSyntaxError
	{
		// search mode by name
		String[] modeNames = wurst.mods.autoLeaveMod.getModes();
		for(int i = 0; i < modeNames.length; i++)
			if(input.equals(modeNames[i].toLowerCase()))
				return i;
			
		// syntax error if mode does not exist
		syntaxError("Invalid mode: " + input);
		return 0;
	}
}
