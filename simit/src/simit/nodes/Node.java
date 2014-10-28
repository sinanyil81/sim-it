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
package simit.nodes;

import simit.hardware.clock.Clock32;
import simit.hardware.transceiver.Packet;
import simit.hardware.transceiver.Transceiver;

public abstract class Node {
	protected int NODE_ID;
	protected Clock32 CLOCK = new Clock32(); // local clock of the node
	protected Channel CHANNEL = new Channel(this); // communication channel
	protected Transceiver TRANSCEIVER = new Transceiver(CLOCK, CHANNEL); // transciever

	protected boolean running = false;
	protected Position position = null;

	protected Csma csmaMAC = new Csma(CHANNEL);

	public Node(int id) {
		this.NODE_ID = id;
	}

	public Node(int id, Position position) {
		this.NODE_ID = id;
		this.position = position;
	}

	public Clock32 getClock() {
		return CLOCK;
	}

	public Transceiver getTransceiver() {
		return TRANSCEIVER;
	}

	public Channel getChannel() {
		return CHANNEL;
	}

	public double getDistance(Node other) {
		return position.distanceTo(other.getPosition());
	}

	public double getDistanceSquare(Node other) {
		return position.squareDistanceTo(other.getPosition());
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position p) {
		position.set(p);
	}

	public int getID() {
		return NODE_ID;
	}

	public void on() throws Exception {

		if (running)
			throw new Exception("Node started previously");

		running = true;
		CLOCK.start();
		TRANSCEIVER.turnOn();
	}

	public void off() {
		running = false;
		CLOCK.stop();
		TRANSCEIVER.turnOff();
	}

	public boolean isRunning() {
		return running;
	}

	public String toString() {
		String s = Integer.toString(NODE_ID);

		return s;
	}

	public void sendPacket(Packet packet) {
			csmaMAC.sendPacket(packet);
	}

	public abstract void receivePacket(Packet packet);
}
