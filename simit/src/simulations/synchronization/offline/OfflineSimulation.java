package simulations.synchronization.offline;

import java.util.StringTokenizer;

import simulations.shared.Logger;

public class OfflineSimulation {
	public static void main(String[] args) {
		runProtocol(new FTSPProtocol(), 
				"src/simulations/synchronization/offline/experiment.txt",
				"src/simulations/synchronization/offline/ftsp.txt");
	}
	
	private static void runProtocol(Protocol protocol, String experimentFile,String logFile){
		Reader.openFile(experimentFile);
		Logger logger = new Logger(logFile);
		String line = null;
		
		while((line = Reader.nextLine())!=null){
			if(line.equals(new String(""))) continue;
			StringTokenizer tokenizer = new StringTokenizer(line," ");
			
			String s = tokenizer.nextToken();
			
			if( s.equals(new String("#"))){
				long experimentSecond =  Long.valueOf(tokenizer.nextToken(), 10);
				long receiverId = Long.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				
				long receiverGlobalTime = protocol.getLogicalClock(receiverId,receiverLocalClock);
				
				logger.log(""+ experimentSecond +" "+receiverId + " " +  receiverGlobalTime);
				
			} 
			else{
				long senderId = Long.valueOf(s, 10);
				long receiverId = Long.valueOf(tokenizer.nextToken(), 10);
				long senderLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				
				long senderGlobalTime = protocol.getLogicalClock(senderId, senderLocalClock);
				protocol.processMessage(senderId, receiverId, senderGlobalTime, receiverLocalClock);
				
			}
			
		}
		
		logger.close();
	}
}
