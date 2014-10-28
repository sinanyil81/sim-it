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
 * Made use of the source code of Sinalgo simulator 
 * 
 */
/* Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package simit.nodes.mobility;

import java.util.Random;

import simit.deployment.NodeFactory;
import simit.nodes.Node;
import simit.nodes.Position;
import simit.statistics.Distribution;
import simit.statistics.GaussianDistribution;
import simit.statistics.PoissonDistribution;

public class RandomWayPoint extends MobilityModel{
	// we assume that these distributions are the same for all nodes
	protected static Distribution speedDistribution;
	protected static Distribution waitingTimeDistribution;
	protected boolean initialized = false;

	public static Random random = Distribution.getRandom(); // a random generator of the framework 
	
	protected Position nextDestination = new Position(); // The point where this node is moving to
	protected Position moveVector = new Position(); // The vector that is added in each step to the current position of this node
	protected int remaining_hops = 0; // the remaining hops until a new path has to be determined
	protected int remaining_waitingTime = 0;
	
	public RandomWayPoint(){	
		if(!initialized){
			speedDistribution = new GaussianDistribution(MobilityConfiguration.speedMean,MobilityConfiguration.speedVariance); 
			waitingTimeDistribution = new PoissonDistribution(MobilityConfiguration.waitingLambda);
			initialized = true;
		}
	}
	
	public Position getNextPos(Node n) {
				
		// execute the waiting loop
		if(remaining_waitingTime > 0) {
			remaining_waitingTime --;
			return n.getPosition();
		}
		
		Position nextPosition = new Position();

		if(remaining_hops == 0) {
			// determine the speed at which this node moves
			double speed = Math.abs(speedDistribution.nextSample(random)); // units per round
			if(speed == 0) 
				speed = MobilityConfiguration.speedMean;

			// determine the next point where this node moves to
			nextDestination = getNextWayPoint();
			
			// determine the number of rounds needed to reach the target
			double dist = nextDestination.distanceTo(n.getPosition());
			double rounds = dist / speed;
			remaining_hops = (int) Math.ceil(rounds);
			
			// determine the moveVector which is added in each round to the position of this node
			double dx = nextDestination.xCoord - n.getPosition().xCoord;
			double dy = nextDestination.yCoord - n.getPosition().yCoord;
			double dz = nextDestination.zCoord - n.getPosition().zCoord;
			moveVector.xCoord = dx / rounds;
			moveVector.yCoord = dy / rounds;
			moveVector.zCoord = dz / rounds;			
		}
		if(remaining_hops <= 1) { // don't add the moveVector, as this may move over the destination.
			nextPosition.xCoord = nextDestination.xCoord;
			nextPosition.yCoord = nextDestination.yCoord;
			nextPosition.zCoord = nextDestination.zCoord;
			// set the next waiting time that executes after this mobility phase
			remaining_waitingTime = (int) Math.ceil(waitingTimeDistribution.nextSample(random));
			remaining_hops = 0;
		} else {
			double newx = n.getPosition().xCoord + moveVector.xCoord; 
			double newy = n.getPosition().yCoord + moveVector.yCoord; 
			double newz = n.getPosition().zCoord + moveVector.zCoord; 
			nextPosition.xCoord = newx;			
			nextPosition.yCoord = newy;
			nextPosition.zCoord = newz;
			remaining_hops --;
		}

		return nextPosition;
	}
	
	/**
	 * Determines the next waypoint where this node moves after having waited.
	 * The position is expected to be within the deployment area.
	 * @return the next waypoint where this node moves after having waited. 
	 */
	protected Position getNextWayPoint() {
		double randx = random.nextDouble() * NodeFactory.area.dimX;
		double randy = random.nextDouble() * NodeFactory.area.dimY;
		double randz = 0;
		if(NodeFactory.area.numDimentions == 3) {
			randz = random.nextDouble() * NodeFactory.area.dimZ;
		}
		return new Position(randx,randy,randz);
	}
}
