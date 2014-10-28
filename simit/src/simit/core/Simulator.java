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
package simit.core;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Vector;


public class Simulator {
	
	private static Simulator simulator = null;
	private SimulationTime simTime = new SimulationTime();
	
	private Vector<SimulationEvent> events = null;
	
	private Simulation simulation = null;
	
	protected Simulator(){
		events = new Vector<SimulationEvent>();
	}

	public static Simulator getInstance() {
		if(simulator == null){
			simulator = new Simulator();
		}

		return simulator;
	}
	
	public void startSimulation(Simulation simulation){
		this.simulation = simulation;
		simulation.run();
	}
	
	public void stopSimulation(){
		if(this.simulation != null){
			simulation.exit();
		}
	}
	
	public Simulation getSimulation(){
		return simulation;
	}
	
	public void register(SimulationEvent event) {
		events.add(event);
		Collections.sort(events);		
	}
	
	public void unregister(SimulationEvent event) {
		events.removeElement(event);	
		Collections.sort(events);
	}	
	
	public void tick() {
		SimulationEvent eventToFire;
		
		try{
			eventToFire = events.remove(0);	
		}
		catch (NoSuchElementException e) {
			eventToFire = null;
		}
		
		if(eventToFire != null){
			simTime = eventToFire.getEventTime();						
			eventToFire.signalEvent();
		}			
	}
	
	
	public void reset(){
		events.removeAllElements();		
		events = new Vector<SimulationEvent>();		
		simTime = new SimulationTime();
	}
	
	public SimulationTime getTime(){
		return simTime;
	}
	
	public long getSecond(){
		return getTime().getTimeHigh()/1024/1024;
	}
}
