package simulations.synchronization.offline.grades;

import simit.hardware.Register32;

public class GradesNode {
		
	LogicalClock logicalClock = new LogicalClock();
	int sequence = 0;
	int id;

	public GradesNode(int id) {
		
		this.id = id;
	}
	
	int calculateSkew(Register32 GlobalTime, Register32 LocalTime) {

		Register32 neighborClock = GlobalTime;
		Register32 myClock = logicalClock.getValue(LocalTime);

		return neighborClock.subtract(myClock).toInteger();
	}
	
	private static final int BEACON_RATE = 30000000;
	float K_max = 1.0f / (float) (BEACON_RATE*BEACON_RATE);

	
	int numErrors = 0;
	float alpha = K_max;
	int lastSkew=0;

	public void adjustClock(Register32 GlobalTime, Register32 LocalTime) {
		logicalClock.update(LocalTime);

		int skew = calculateSkew(GlobalTime,LocalTime);
		
		if(Math.abs(skew)>1000){
			if(++numErrors<2)
				return;
			
			logicalClock.rate = 0.0f;
            lastSkew = 0;
            alpha = K_max;
            logicalClock.setValue(GlobalTime, LocalTime);
            numErrors = 0;
            return;
		}
		
		numErrors = 0;
			
		logicalClock.setValue(GlobalTime, LocalTime);

		if(Math.signum(skew) == Math.signum(lastSkew)){
            alpha *= 2.0f;                  
		}
		else{
            alpha /=3.0f;
		}
		
		float derivative = 2*BEACON_RATE*skew;
		
		if (alpha > K_max) alpha = K_max;         
        if(alpha < 0.0000000001f) alpha = 0.0000000001f;
        
        lastSkew = skew;
                        
        logicalClock.rate += alpha*(float)(2*skew);
	}
	
	public Register32 local2Global(Register32 clock) {
		return logicalClock.getValue(clock);
	}	
	
	public void preBroadcast(){
		if(id==GradesProtocol.REFERENCE) sequence++;
	}

	public float getSkew() {
		return logicalClock.rate;
	}
}