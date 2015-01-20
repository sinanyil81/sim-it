package simulations.synchronization.offline.avts;

import simit.hardware.Register32;
import simulations.shared.avt.AdaptiveValueTracker;
import simulations.synchronization.avts.LogicalClock;

public class AVTSNode {
	private static final int TOLERANCE = 1;
	
	LogicalClock logicalClock = new LogicalClock();
	int sequence = 0;
	int id;

	public AVTSNode(int id) {
		
		this.id = id;
	}
	
	int calculateSkew(Register32 GlobalTime, Register32 LocalTime) {

		Register32 neighborClock = GlobalTime;
		Register32 myClock = logicalClock.getValue(LocalTime);

		return myClock.subtract(neighborClock).toInteger();
	}
	
	int numErrors = 0;

	public void adjustClock(Register32 GlobalTime, Register32 LocalTime) {
		logicalClock.update(LocalTime);

		int skew = calculateSkew(GlobalTime,LocalTime);
		
		if(Math.abs(skew)>1000){
			numErrors++;
			if(numErrors<2)
				return;
			else
				numErrors = 0;
		}
			
		logicalClock.setValue(GlobalTime, LocalTime);

		if (skew > TOLERANCE) {
			logicalClock.rate.adjustValue(AdaptiveValueTracker.FEEDBACK_LOWER);
		} else if (skew < -TOLERANCE) {
			logicalClock.rate
					.adjustValue(AdaptiveValueTracker.FEEDBACK_GREATER);
		} else {
			logicalClock.rate.adjustValue(AdaptiveValueTracker.FEEDBACK_GOOD);
		}
	}
	
	public Register32 local2Global(Register32 clock) {
		return logicalClock.getValue(clock);
	}
	
	
	public void preBroadcast(){
		if(id==20) sequence++;
	}
}
