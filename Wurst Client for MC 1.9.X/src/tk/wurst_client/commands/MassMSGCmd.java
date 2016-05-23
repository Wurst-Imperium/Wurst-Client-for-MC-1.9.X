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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import net.minecraft.network.play.client.CPacketChatMessage;

@Info(help = "Allows you to spam other players text.", name = "massmsg", syntax = { "/<command> <message>", "stop" })
public class MassTpaMod extends Cmd {
	private String message = "";
	private float speed = 1F;
	private int i;
	private ArrayList<String> players;
	private Random random = new Random();
	private String name = "";
	private long currentMS = 0L;
	protected long lastMS = -1L;

	@Override
	public void execute(String[] args) throws Error {
		if (args.length < 1) {
			syntaxError();
		}

		// Enable
		if (!args[0].equals("Continue") && !args[0].equalsIgnoreCase("stop")) {

			i = 0;
			Iterator itr = mc.getNetHandler().getPlayerInfoMap().iterator();
			players = new ArrayList<String>();
			while (itr.hasNext())
				players.add(StringUtils.stripControlCodes(((NetworkPlayerInfo) itr.next()).getPlayerNameForReal()));
			Collections.shuffle(players, random);

			message = args[0] + "%%name%%";

			for (int i = 1; i < args.length; i++)
				message += " " + args[i];

			// Start Continue
			mc.thePlayer.sendAutomaticChatMessage(".massmsg Continue");
		} else if (args[0].equalsIgnoreCase("stop")) {
			i = players.size();
		} else if (args[0].equals("Continue")) {
			updateMS();
			if (hasTimePassedS(speed)) {
				name = players.get(i);
				if (name.equals(mc.thePlayer.getName())) {
					i++;
					name = players.get(i);
				}
				mc.thePlayer.sendChatMessage(message.replace("%%name%%", name));

				updateLastMS();
				i++;
				if (i >= players.size()) {
					// end
					message = "";
					i = 0;
					name = "";
					Collections.fill(players, null);
					random = new Random();
				} else {
					// Restart
					mc.thePlayer.sendAutomaticChatMessage(".massmsg Continue");
				}
			}
		} else {
			syntaxError();
		}
	}

	private void updateMS() {
		currentMS = System.currentTimeMillis();
	}

	private boolean hasTimePassedS(float speed) {
		return currentMS >= lastMS + (long) (1000 / speed);
	}

	private void updateLastMS() {
		lastMS = System.currentTimeMillis();
	}
}
