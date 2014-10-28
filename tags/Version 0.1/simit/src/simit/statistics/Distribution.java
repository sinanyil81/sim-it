/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
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

/**
 * 
 * Modified by * @author Kasım Sinan YILDIRIM (sinanyil81@gmail.com)
 * 
 */
package simit.statistics;

import java.util.Random;

public abstract class Distribution {
	
	protected static Random randomGenerator; // the singleton instance of the random object. Be sure to initialize before using the first time! 
	private static long randomSeed = -1; // the seed used for the random object

	public static void setSeed(long seed){
		randomSeed = seed;
	}
	
	public static long getSeed() {
		getRandom(); // initialize the random generator if it's not already done
		return randomSeed;
	}

	/**
	 * The super-class for all distributions, ensures that the random generator instance exists 
	 */
	protected Distribution(){
		getRandom(); // initialize the random generator if it's not already done
	}
	
	/**
	 * Returns the singleton random generator object of this simulation. You should only use this
	 * random number generator in this project to ensure that the simulatoin can be repeated by
	 * using a fixed seed. (The usage of a fixed seed can be enforced in the XML configuration file.)  
	 *
	 * @return the singleton random generator object of this simulation
	 */
	public static Random getRandom() {
		// construct the singleton random object if it does not yet exist
		if(randomGenerator == null) {
			if(randomSeed == -1)
				randomSeed = (new java.util.Random()).nextLong();
			randomGenerator = new Random(randomSeed); // use a random seed
		}
		return randomGenerator;
	}
	
	/**
	 * Returns the next random sample of this distribution. 
	 * 
	 * This method must be implemented in all proper subclasses.
	 * @return the next random sample of this distribution.
	 */
	public abstract double nextSample();
	public abstract double nextSample(Random r);

}
