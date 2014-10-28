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
package simit.nodes.mobility;

import simit.deployment.NodeFactory;
import simit.hardware.clock.Clock32;
import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;

import java.lang.reflect.Constructor;

import simit.nodes.Position;

public class MobilityManager implements TimerHandler {
	protected Clock32 clock = new Clock32();
	protected Timer timer = new Timer(clock,this);
	protected MobilityModel[] models = null;
	
	public MobilityManager(String mobilityClassName) {
		clock.start();
		timer.startPeriodic(1000000);	
		
		models = new MobilityModel[NodeFactory.numNodes];
		for (int i = 0; i < NodeFactory.numNodes; i++) {
			models[i] = createModel(mobilityClassName);
		}		
	}
	
	@Override
	public void fireEvent(Timer timer) {
		for (int i = 0; i < NodeFactory.numNodes; i++) {
			Position pos = models[i].getNextPos(NodeFactory.nodes[i]);
			NodeFactory.nodes[i].setPosition(pos);
		}
		
		NodeFactory.updateNeighborhood();
	}
	
	static MobilityModel createModel(String className){
		Class<?> c;
		Object object = null;
		try {
			c = Class.forName(className);
			Constructor<?> cons = c.getConstructor();
			object = cons.newInstance(new Object[] {});
		} catch (Exception e) {			
			e.printStackTrace();
			System.out.println("Problem loading/finding class " + className);
			System.exit(-1);
		}
		
		return (MobilityModel)object;
	}

}
