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
package simulations.synchronization.ftsp;

import simit.core.Simulation;
import simit.core.Simulator;
import simit.deployment.DeploymentArea;
import simit.deployment.NodeFactory;
import simit.gui.GUI;
import simit.hardware.clock.Clock32;
import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;
import simit.statistics.Distribution;
import simulations.shared.Logger;
import simulations.shared.topology.LineTopology;

public class FtspSimulation extends Simulation implements TimerHandler {
	
	private int PERIOD = 20000000;
	protected Logger logger;
	Timer timer = new Timer(new Clock32(),this);
	
	public static void main(String[] args) {
		Simulator.getInstance().startSimulation(new FtspSimulation(20000));
	}
	
	public FtspSimulation(int durationTime){
		super(durationTime);
		
		DeploymentArea area = new DeploymentArea(300,300,0,2);
		
		/* create nodes that are randomly deployed on the given area*/
		NodeFactory.createNodes("simulations.synchronization.ftsp.FtspNode", 20,area);

		/* create mobility policy that will move nodes */
//		new MobilityManager("simit.nodes.mobility.RandomWayPoint");

		/* start user interface */
		GUI.start();
		
		/* re-adjust the positions of the nodes to align them on the line */
		new LineTopology(area);
		
		NodeFactory.turnOnNodes();
		logger = new Logger("logFile.txt");
		timer.startOneshot((int) (PERIOD + ((Distribution.getRandom().nextInt() % 4) + 1)*1000000));
	}
	
	@Override
	public void exit() {
		logger.close();	
		System.out.println("Simulation finished!");
	}

	/** 
	 * Logs instantaneous clock values of the nodes in the network 
	 */
	private void log() {
		for(int i=0;i<NodeFactory.nodes.length;i++){
			logger.log(NodeFactory.nodes[i].toString());
		}
	}

	/**
	 * Timer event used to log the clock of the nodes during simulation.
	 */
	@Override
	public void fireEvent(Timer timer) {
		log();
		timer.startOneshot((int) (PERIOD + ((Distribution.getRandom().nextInt() % 4) + 1)*1000000));
		
	}
}