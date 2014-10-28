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
package simit.hardware.clock;

import simit.hardware.Interrupt;
import simit.hardware.InterruptHandler;

/**
 * Simulates Timer which is built on a hardware clock.
 * 
 * @author K. Sinan YILDIRIM
 */
public class Timer implements InterruptHandler{
	
	/** Indicates if timer is periodic */
	private boolean periodic = false;
	/** The period of the timer */
	private long period = 0;
	
	/** The hardware clock on which the timer is built */
	private Clock32 clock;
	private Interrupt interrupt;
	
	/** The object which will be notified for timer events */
	private TimerHandler handler;
		
	public Timer(Clock32 clock, TimerHandler handler){
		this.handler = handler;
		this.clock = clock;
		interrupt = new Interrupt(this);
	}
	
	private int convert(double ticks) {
		long result = (long) (ticks/(1.0 + clock.getDrift()));
		return (int)result;
	}
	
	/**
	 * Starts a one shot timer which will fire when the hardware clock 
	 * progressed given amount of clock ticks.
	 * 
	 * @param ticks
	 */
	public void startOneshot(int ticks){
		
		if(ticks > 0){
			periodic = false;	
			period = convert(ticks);
			
			if(period == 0){
				period = 1;
			}
			
			interrupt.register((int) period);			
		}
	}
	
	/**
	 * Starts a periodic timer which will fire every time the hardware clock 
	 * progressed given amount of clock ticks.
	 * 
	 * @param ticks
	 */
	public void startPeriodic(int ticks){
		
		if(ticks > 0){
			periodic = true;	
			period = convert(ticks);	
			
			if(period == 0){
				period = 1;
			}
				
			interrupt.register((int) period);			
		}
	}
	
	public void stop(){
		interrupt.unregister();
	}

	public int getPeriod() {
		return (int) period;
	}

	@Override
	public void signal(Interrupt interrupt) {
		if(handler != null)
			handler.fireEvent(this);
		
		if(periodic){
			interrupt.register((int) period);
		}		
		
	}
}
