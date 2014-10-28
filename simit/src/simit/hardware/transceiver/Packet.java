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

package simit.hardware.transceiver;
import simit.hardware.Register32;

public class Packet {
	
	private Object payload;	
	private Register32 timestamp = new Register32();
	private Register32 eventTime = new Register32();
	
	public Packet(Object payload){
		this.payload = payload;
	}
	
	public Packet(Packet packet){
		this.payload = packet.getPayload();
		this.timestamp = new Register32(packet.getTimestamp());
		this.eventTime = new Register32(packet.getEventTime());
	}

	public void setPayload(Object payload){
		this.payload = payload;
	}
	
	public Object getPayload(){
		return payload;
	}
	
	public void setTimestamp(Register32 timestamp){
		this.timestamp = new Register32(timestamp);
	}
	
	public Register32 getTimestamp(){
		return timestamp;
	}
	
	public void setEventTime(Register32 eventTime){
		this.eventTime = new Register32(eventTime);
	}
	
	public Register32 getEventTime(){
		return eventTime;
	}
	
	public boolean equals(Packet packet){
		if(payload == packet.getPayload())
			return true;
		
		return false;
	}
}
