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

import simit.core.Simulation;
import simit.core.Simulator;
import simit.deployment.DeploymentArea;
import simit.deployment.NodeFactory;
import simit.gui.GUI;
import simit.nodes.mobility.MobilityManager;

/**
 * Simulation class that will implement simulation specific steps.
 * 
 * @author Kasım Sinan YILDIRIM (sinanyil81@gmail.com)
 *
 */
public class SampleSimulation extends Simulation{
	
	public static void main(String[] args) {
		Simulator.getInstance().startSimulation(new SampleSimulation(20000));
	}

	
	public SampleSimulation(int durationTime){
		super(durationTime);
		
		/* create nodes that are randomly deployed on the given 2D area */
		NodeFactory.createNodes("simulations.sample.SampleNode", 20,
				new DeploymentArea(300, 300, 0, 2));

		/* create mobility policy that will move nodes */
		/* COMMENT TO TURN OFF MOBILITY */
		new MobilityManager("simit.nodes.mobility.RandomWayPoint");

		/* start user interface */
		/* COMMENT TO TURN OFF */
		GUI.start();

		/* Turn on  all nodes and start simulation */
		NodeFactory.turnOnNodes();		
	}
	
	@Override
	public void exit() {	
		System.out.println("Simulation finished!");
	}
}