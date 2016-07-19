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
package simulations.synchronization;

import java.util.Arrays;
import java.util.StringTokenizer;

import simit.core.Simulator;
import simit.statistics.Distribution;
import simulations.shared.Logger;
import simulations.synchronization.avts.AvtsSimulation;
import simulations.synchronization.ftsp.FtspSimulation;
import simulations.synchronization.grades.GradesSimulation;
import simulations.synchronization.offline.simulation.Reader;
import simulations.synchronization.piSync.PiSyncSimulation;
import simulations.synchronization.pulse.PulseSyncSimulation;



public class ScalabilitySimulations {
		
	public static void main(String[] args) {
//		
		Distribution.setSeed(0xabcdef);
		
		//Simulator.getInstance().startSimulation(new PulseSyncSimulation(20,10000));	
		Simulator.getInstance().startSimulation(new PiSyncSimulation(20,10000));	
		
//		Simulator.getInstance().startSimulation(new FtspSimulation(10000));
//		logFtsp.log(evaluateResults("logFile.txt", 20));
		
//		
//		for(int i=10;i<=100;i+=10){
//			Simulator.getInstance().startSimulation(new PulseSyncSimulation(i,20000));
//			logPulse.log(evaluateResults("logFile.txt", i));
//			Simulator.getInstance().startSimulation(new FtspSimulation(i,20000));
//			logFtsp.log(evaluateResults("logFile.txt", i));
//			
//		}
		
	}
	
	private static String evaluateResults(String logFile, int numNodes) {
		Reader.openFile(logFile);
		String line = null;
		long clocks[] = new long[numNodes];
		long experimentSecond = -1;
//		String rates = "";
		
		long maxError = 0;

		while ((line = Reader.nextLine()) != null) {

			StringTokenizer tokenizer = new StringTokenizer(line, " ");

			long second = Long.valueOf(tokenizer.nextToken(), 10);
			int id = Integer.valueOf(tokenizer.nextToken(), 10);
			long clock = Long.valueOf(tokenizer.nextToken(), 10);

			// float rate = Float.intBitsToFloat(Integer.valueOf(
			// tokenizer.nextToken(), 10));

			if (experimentSecond == -1) {
				experimentSecond = second;
			}

			if (second != experimentSecond) {
				Arrays.sort(clocks);

				long skew = clocks[clocks.length-1] - clocks[0];
				
				if(experimentSecond > 10000 && skew > maxError)
					maxError = skew;
				
				for (int i = 0; i < clocks.length; i++) {
					clocks[i] = 0;
				}
				experimentSecond = second;
				// rates = "";
			}

			clocks[id - 1] = clock;

			// rates += " " + (int)(rate*100000000.0f);

		}
		
		System.out.println(logFile + " " + numNodes + " " + maxError);
		
		return new String(""+numNodes+ " "+ maxError);
	}



}