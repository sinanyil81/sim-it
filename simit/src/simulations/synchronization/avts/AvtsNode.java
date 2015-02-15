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
package simulations.synchronization.avts;

import simit.core.Simulator;
import simit.hardware.Register32;
import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;
import simit.hardware.transceiver.Packet;
import simit.nodes.Node;
import simit.nodes.Position;
import simit.statistics.Distribution;
import simulations.shared.avt.AdaptiveValueTracker;

public class AvtsNode extends Node implements TimerHandler {

	private static final int BEACON_RATE = 30000000;
	private static final int TOLERANCE = 1;

	LogicalClock logicalClock = new LogicalClock();
	Timer timer0;

	Packet processedMsg = null;
	AvtsMessage outgoingMsg = new AvtsMessage();

	public AvtsNode(int id, Position position) {
		super(id, position);

		CLOCK.setRandomDrift();
		CLOCK.setDynamicDrift();

		timer0 = new Timer(CLOCK, this);

		/* to start clock with a random value */
		CLOCK.setValue(new Register32(Math.abs(Distribution.getRandom()
				.nextInt())));

		outgoingMsg.sequence = 0;
		outgoingMsg.rootid = NODE_ID;
		outgoingMsg.nodeid = NODE_ID;
	}

	int calculateSkew(Packet packet) {
		AvtsMessage msg = (AvtsMessage) packet.getPayload();

		Register32 neighborClock = msg.clock;
		Register32 myClock = logicalClock.getValue(packet.getEventTime());

		return myClock.subtract(neighborClock).toInteger();
	}

	private void adjustClock(Packet packet) {
		logicalClock.update(packet.getEventTime());

		AvtsMessage msg = (AvtsMessage) packet.getPayload();

		if (msg.rootid < outgoingMsg.rootid) {
			outgoingMsg.rootid = msg.rootid;
			outgoingMsg.sequence = msg.sequence;
		} else if (outgoingMsg.rootid == msg.rootid
				&& (msg.sequence - outgoingMsg.sequence) > 0) {
			outgoingMsg.sequence = msg.sequence;
		} else {
			return;
		}

		int skew = calculateSkew(packet);
		logicalClock.setValue(msg.clock, packet.getEventTime());

		if (skew > TOLERANCE) {
			logicalClock.rate.adjustValue(AdaptiveValueTracker.FEEDBACK_LOWER);
		} else if (skew < -TOLERANCE) {
			logicalClock.rate
					.adjustValue(AdaptiveValueTracker.FEEDBACK_GREATER);
		} else {
			logicalClock.rate.adjustValue(AdaptiveValueTracker.FEEDBACK_GOOD);
		}
	}

	void processMsg() {
		adjustClock(processedMsg);
	}

	@Override
	public void receivePacket(Packet packet) {
		processedMsg = packet;
		processMsg();
	}

	@Override
	public void fireEvent(Timer timer) {
		sendMsg();
	}

	private void sendMsg() {
		Register32 localTime, globalTime;

		localTime = CLOCK.getValue();
		globalTime = logicalClock.getValue(localTime);

		if (outgoingMsg.rootid == NODE_ID) {
			outgoingMsg.clock = new Register32(localTime);
		} else {
			outgoingMsg.clock = new Register32(globalTime);
		}

		Packet packet = new Packet(new AvtsMessage(outgoingMsg));
		packet.setEventTime(new Register32(localTime));
		sendPacket(packet);

		if (outgoingMsg.rootid == NODE_ID)
			++outgoingMsg.sequence;
	}

	@Override
	public void on() throws Exception {
		super.on();
		timer0.startPeriodic(BEACON_RATE
				+ ((Distribution.getRandom().nextInt() % 100) + 1) * 10000);
	}

	public Register32 local2Global() {
		return logicalClock.getValue(CLOCK.getValue());
	}

	boolean changed = false;

	public String toString() {
		String s = "" + Simulator.getInstance().getSecond();

		s += " " + NODE_ID;
		s += " " + local2Global().toString();
//		s += " "
//				+ Float.floatToIntBits((float) ((1.0 + logicalClock.rate
//						.getValue()) * (1.0 + CLOCK.getDrift())));

		return s;
	}
}
