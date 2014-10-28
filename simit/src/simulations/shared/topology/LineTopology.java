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
package simulations.shared.topology;

/**
 * 
 * Class that rearranges the positions of the nodes in a given 
 * deployment area into the line topology. It should be noted 
 * that the distance between each node is 15 and in order to 
 * connect these nodes, the transmission power of the transceiver
 * is set to 100 according to the {@link RadioSignal#getStaticFading}
 * and {@link Channel#updateChannel}.
 * 
 */
import simit.deployment.DeploymentArea;
import simit.deployment.NodeFactory;
import simit.hardware.transceiver.RadioSignal;
import simit.nodes.Position;

public class LineTopology {

	private static int SPACING = 15;

	public LineTopology(DeploymentArea area) {
		double positionX =10;
		double positionY =10;
		boolean nextLine = false;
		int direction = 1;

		RadioSignal.setStaticRandomFactor(0.0);
		
		for (int i = 0; i < NodeFactory.numNodes; i++) {
			NodeFactory.nodes[i].setPosition(new Position(positionX, positionY,0));
			NodeFactory.nodes[i].getTransceiver().setTransmitPower(100);
			positionX += direction*SPACING;
			
			if(nextLine){
				positionY += SPACING;
				nextLine = false;
			}
			
			if ((positionX+direction*SPACING) > area.dimX || (positionX+direction*SPACING) < 0) {
				direction = -direction;
				positionY += SPACING;
				nextLine = true;
			}
		}
		
		/* update the broadcast ranges of the nodes */
		NodeFactory.updateNeighborhood();
	}
}
