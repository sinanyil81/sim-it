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

import simit.core.SimulationTime;
import simit.core.Simulator;
import simit.hardware.Register32;
import simit.statistics.GaussianDistribution;

public class Clock32 {

	private static final int MEAN_DRIFT = 50;
	private static final int DRIFT_VARIANCE = 300;

	private static final int NOISE_MEAN = 0;
	private static final int NOISE_VARIANCE = 2;

	Counter32 counter = new Counter32();

	/** Drift of the clock */
	private double drift = 0.0;

	/** is started? */
	protected boolean started = false;
	protected boolean dynamicDrift = false;
	
	/* last read time */
	private SimulationTime lastRead = new SimulationTime();

	public void start() {
		started = true;
		lastRead = Simulator.getInstance().getTime();
	}
	
	public void stop(){
		started = false;
	}

	public void progress(double amount) {

		if (!started)
			return;

		/* Add dynamic noise */
		double noise = 0.0;
		if (dynamicDrift) {
			noise = GaussianDistribution.nextGaussian(NOISE_MEAN,
					NOISE_VARIANCE);
			noise /= 100000000.0;
		}

		/* Progress clock by considering the constant drift. */
		amount += amount * (drift + noise);
		counter.increment(amount);
	}

	public void setRandomDrift() {
		drift = GaussianDistribution.nextGaussian(MEAN_DRIFT, DRIFT_VARIANCE);
		drift /= 1000000.0;
	}

	public void setDrift(double drift) {
		this.drift = drift;
	}

	public double getDrift() {
		return drift;
	}

	public void setDynamicDrift() {
		dynamicDrift = true;
	}
	
	public Register32 getValue(){
		SimulationTime currentTime = Simulator.getInstance().getTime();
		progress(currentTime.sub(lastRead).toDouble());
		lastRead = currentTime;
		
		return counter.getValue();
	}
	
	public void setValue(Register32 value) {
		counter.setValue(value);
	}
}
