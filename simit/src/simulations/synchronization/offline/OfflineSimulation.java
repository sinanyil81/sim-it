package simulations.synchronization.offline;

import java.util.Iterator;

import simulations.shared.Logger;

public class OfflineSimulation {
	public static void main(String[] args) {
		Reader  r = new Reader("src/simulations/synchronization/offline/timestamps.txt"," :[]=x","");
		System.out.println("Events loaded");
		
		Logger logger = new Logger("src/simulations/synchronization/offline/experiment.txt");
		
		for (Iterator<Object> iterator = r.getEvents().iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			
			if (object instanceof BroadcastEvent) {
				logger.log(((BroadcastEvent)object).toString());
			}
			else if (object instanceof ReferenceEvent) {
				logger.log(((ReferenceEvent)object).toString());
			}
			
		}
		
		logger.close();
	}
}
