/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import net.minecraft.network.Packet;

public class PacketInputEvent extends CancellableEvent
{
	private Packet packet;
	
	public PacketInputEvent(Packet packet)
	{
		this.packet = packet;
	}
	
	public Packet getPacket()
	{
		return packet;
	}
	
	public void setPacket(Packet packet)
	{
		this.packet = packet;
	}
	
	@Override
	public String getAction()
	{
		return "receiving packet";
	}
	
	@Override
	public String getComment()
	{
		return "Packet: " + packet != null ? packet.getClass().getSimpleName()
			: "null";
	}
}
