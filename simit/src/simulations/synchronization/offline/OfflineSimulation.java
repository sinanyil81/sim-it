package simulations.synchronization.offline;

import java.util.StringTokenizer;

public class OfflineSimulation {
	public static void main(String[] args) {
		Reader.openFile("src/simulations/synchronization/offline/experiment.txt");
		String line = null;
		
		while((line = Reader.nextLine())!=null){
			if(line.equals(new String(""))) continue;
			StringTokenizer tokenizer = new StringTokenizer(line," ");
			
			String s = tokenizer.nextToken();
			
			if( s.equals(new String("#"))){
				getInstantaneousLogicalClockValue(
						Long.valueOf(tokenizer.nextToken(), 10),  // Experiment second
						Long.valueOf(tokenizer.nextToken(), 10),  // Receiver Id 
						Long.valueOf(tokenizer.nextToken(), 10)); // Receiver Local Clock
			} 
			else{
				processMessage(
						Long.valueOf(s, 10),  					  // Sender Id 
						Long.valueOf(tokenizer.nextToken(), 10),  // Receiver Id
						Long.valueOf(tokenizer.nextToken(), 10),  // Sender Local Clock 
						Long.valueOf(tokenizer.nextToken(), 10)); // Receiver Local Clock
			}
			
			
		}
	}

	private static void processMessage(Long valueOf, Long valueOf2,
			Long valueOf3, Long valueOf4) {
		// TODO Auto-generated method stub
		
	}

	private static void getInstantaneousLogicalClockValue(Long valueOf,
			Long valueOf2, Long valueOf3) {
		// TODO Auto-generated method stub
		
	}
}
