package simulations.synchronization.offline;

import java.util.Arrays;
import java.util.StringTokenizer;

import simulations.shared.Logger;
import simulations.synchronization.offline.avts.AVTSProtocol;
import simulations.synchronization.offline.ftsp.FTSPProtocol;
import simulations.synchronization.offline.simulation.Reader;

public class OfflineSimulation {
	
	/*
	 * Consider the timestamp data in the experiment file and execute
	 * the time synchronization protocol.
	 * 
	 * The data was collected from a tesbed 5x4 grid topology.
	 * 
	 *  1 -  2 -  3 -  4
	 *  |    |    |    |
	 *  5 -  6 -  7 -  8
	 *  |    |    |    |
	 *  9  - 10 - 11 - 12
	 *  |    |    |    |
	 *  13 - 14 - 15 - 16
	 *  |    |    |    |
	 *  17 - 18 - 19 - 20
	 *  
	 */
	
	private final static int GRID = 0;
	private final static int LINE = 1;
		
	public static void main(String[] args) {

		runProtocol(new FTSPProtocol(),
				"src/simulations/synchronization/offline/experiment2.txt",
				"src/simulations/synchronization/offline/ftsp/ftsp.txt",LINE);

		runProtocol(new AVTSProtocol(),
				"src/simulations/synchronization/offline/experiment2.txt",
				"src/simulations/synchronization/offline/avts/avts.txt",LINE);

		evaluateResults(
				"src/simulations/synchronization/offline/ftsp/ftsp.txt",
				"src/simulations/synchronization/offline/ftsp/ftspResults.txt");

		evaluateResults(
				"src/simulations/synchronization/offline/avts/avts.txt",
				"src/simulations/synchronization/offline/avts/avtsResults.txt");
	}
	
	/*
	 *  The data was collected from a tesbed 5x4 grid topology.
	 * 
	 *  1 -  2 -  3 -  4
	 *  |    |    |    |
	 *  5 -  6 -  7 -  8
	 *  |    |    |    |
	 *  9  - 10 - 11 - 12
	 *  |    |    |    |
	 *  13 - 14 - 15 - 16
	 *  |    |    |    |
	 *  17 - 18 - 19 - 20
	 *  
	 *  To simulate line topology, we need to do this check.
	 *  
	 */
	private static boolean isAllowedToReceive(int topology, int receiver, int sender){
		
		if(topology == GRID) // the data was already collected from grid topology
			return true;
		else if(topology  == LINE){
			if(receiver == 8 || receiver == 9 || receiver == 16 || receiver == 17){
				if (receiver - sender == 4)
					return true;
			}
			else if(receiver <= 4 ){
				if (receiver - sender == 1)
					return true;
			}
			else if (receiver >= 5 && receiver <=8){
				if (receiver - sender == -1)
					return true;
			}
			else if (receiver >= 9 && receiver <=12){
				if (receiver - sender == 1)
					return true;
			}
			else if (receiver >= 13 && receiver <=16){
				if (receiver - sender == -1)
					return true;
			}
			else if (receiver >= 17 && receiver <=20){
				if (receiver - sender == 1)
					return true;
			}
		}
		
		return false;
	}

	private static void runProtocol(Protocol protocol, String experimentFile,
			String logFile, int topology) {
		Reader.openFile(experimentFile);
		Logger logger = new Logger(logFile);
		String line = null;

		boolean broadcast = false;

		while ((line = Reader.nextLine()) != null) {

			if (line.equals(new String(""))) {
				broadcast = false;
				continue;
			}

			StringTokenizer tokenizer = new StringTokenizer(line, " ");

			String s = tokenizer.nextToken();

			if (s.equals(new String("#"))) {
				long experimentSecond = Long.valueOf(tokenizer.nextToken(), 10);
				int receiverId = Integer.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(),
						10);

				long receiverGlobalTime = protocol.getLogicalClock(receiverId,
						receiverLocalClock);

				logger.log(""
						+ experimentSecond
						+ " "
						+ receiverId
						+ " "
						+ receiverGlobalTime
						+ " "
						+ Float.floatToIntBits(protocol
								.getRateMultiplier(receiverId - 1)));

			} else {

				int senderId = Integer.valueOf(s, 10);
				int receiverId = Integer.valueOf(tokenizer.nextToken(), 10);
				long senderLocalClock = Long.valueOf(tokenizer.nextToken(), 10);
				long receiverLocalClock = Long.valueOf(tokenizer.nextToken(),
						10);

				long senderGlobalTime = protocol.getLogicalClock(senderId,
						senderLocalClock);

				if (broadcast == false) {
					broadcast = true;
					protocol.preBroadcast(senderId, senderLocalClock);
				}

				if(isAllowedToReceive(topology, receiverId, senderId))
					protocol.processMessage(senderId, receiverId, senderGlobalTime,receiverLocalClock);

			}

		}

		logger.close();
	}

	private static void evaluateResults(String logFile, String resultFile) {
		Reader.openFile(logFile);
		Logger logger = new Logger(resultFile);
		String line = null;
		long clocks[] = new long[20];
		long experimentSecond = -1;
		String rates = "";

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
				int j = 0;
				while (clocks[j++] == 0 && j < 19)
					; // find the first non-zero clock

				long skew = clocks[19] - clocks[j];

				logger.log("" + experimentSecond + " " + skew + rates);
				
				for (int i = 0; i < clocks.length; i++) {
					clocks[i] = 0;
				}
				experimentSecond = second;
				// rates = "";
			}

			clocks[id - 1] = clock;

			// rates += " " + (int)(rate*100000000.0f);

		}

		logger.close();
	}
}
