/*
 * Copyright (c) 2014, Ege University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holder nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Kasım Sinan YILDIRIM (sinanyil81@gmail.com)
 *
 */
package simulations.sample;

import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;
import simit.hardware.transceiver.Packet;
import simit.nodes.Node;
import simit.nodes.Position;

public class SampleNode extends Node implements TimerHandler {

	private static final int BEACON_RATE = 30000000; // how often send the
														// beacon msg (in
														// seconds)

	Timer timer0;

	public SampleNode(int id, Position position) {
		super(id, position);
		CLOCK.setRandomDrift();
		CLOCK.setDynamicDrift();

		timer0 = new Timer(CLOCK, this);
	}

	@Override
	public void receivePacket(Packet packet) {
		SampleMessage message = (SampleMessage) packet.getPayload();
		System.out.println("Node " + getID() + " received packet from node "
				+ message.nodeid);
	}

	@Override
	public void fireEvent(Timer timer) {
		Packet packet = new Packet(new SampleMessage(this.getID()));
		sendPacket(packet);
	}

	@Override
	public void on() throws Exception {
		super.on();
		timer0.startPeriodic(BEACON_RATE);
	}
}
