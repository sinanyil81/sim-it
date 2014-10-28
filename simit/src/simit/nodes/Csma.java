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
 * Carrier Sense Multiple Access MAC implementation.
 * 
 */
package simit.nodes;

import simit.hardware.clock.Clock32;
import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;
import simit.hardware.transceiver.Packet;
import simit.statistics.Distribution;

public class Csma implements TimerHandler {


	protected Packet sendingPacket = null;

	protected static int sendMinWaitingTime = 200;
	protected static int sendRandomWaitingTime = 5000;
	protected static int sendMinBackOffTime = 100;
	protected static int sendRandomBackOffTime = 30;

	protected Channel channel;
	protected Timer timer;

	public Csma(Channel channel) {		
		this.timer = new Timer(new Clock32(),this);
		this.channel = channel;
	}
	
	public boolean sendPacket(Packet packet) {
		sendingPacket = packet;
		timer.startOneshot(generateWaitingTime());
		return true;
	}

	@Override
	public void fireEvent(Timer timer) {
		if (channel.ClearChannelAssessment())
			channel.transmit(sendingPacket);
		else
			timer.startOneshot(generateBackOffTime());
	}

	public static int generateWaitingTime() {
		return sendMinWaitingTime
				+ (int) (Distribution.getRandom().nextDouble() * sendRandomWaitingTime);
	}

	protected static int generateBackOffTime() {
		return sendMinBackOffTime
				+ (int) (Distribution.getRandom().nextDouble() * sendRandomBackOffTime);
	}
}
