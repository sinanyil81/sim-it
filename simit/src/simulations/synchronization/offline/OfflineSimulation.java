package simulations.synchronization.offline;

import java.util.StringTokenizer;

import simulations.shared.Logger;
import simulations.synchronization.offline.ftsp.FTSPProtocol;

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
		
		boolean broadcast = false;
		
		while((line = Reader.nextLine())!=null){
			
			if(line.equals(new String(""))){
				broadcast = false;
				continue;
			}
			
			StringTokenizer tokenizer = new StringTokenizer(line," ");
			
			String s = tokenizer.nextToken();
			
			if( s.equals(new String("#"))){
				long experimentSecond =  Long.valueOf(tokenizer.nextToken(), 10);
				int receiverId = Integer.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				
				long receiverGlobalTime = protocol.getLogicalClock(receiverId,receiverLocalClock);
				
				logger.log(""+ experimentSecond +" "+receiverId + " " +  receiverGlobalTime);
				
			} 
			else{
				
				int senderId = Integer.valueOf(s, 10);
				int receiverId = Integer.valueOf(tokenizer.nextToken(), 10);
				long senderLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				
				long senderGlobalTime = protocol.getLogicalClock(senderId, senderLocalClock);
				
				if(broadcast == false){
					broadcast = true;
					protocol.preBroadcast(senderId, senderLocalClock);
				}
				
				protocol.processMessage(senderId, receiverId, senderGlobalTime, receiverLocalClock);
				
			}
			
		}
		
		logger.close();
	}
}
