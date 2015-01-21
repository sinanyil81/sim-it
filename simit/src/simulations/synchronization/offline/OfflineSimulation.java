package simulations.synchronization.offline;

import java.util.Arrays;
import java.util.StringTokenizer;

import simulations.shared.Logger;
import simulations.synchronization.offline.avts.AVTSProtocol;
import simulations.synchronization.offline.ftsp.FTSPProtocol;
import simulations.synchronization.offline.simulation.Reader;

public class OfflineSimulation {
	public static void main(String[] args) {
		runProtocol(new FTSPProtocol(),
				"src/simulations/synchronization/offline/experiment2.txt",
				"src/simulations/synchronization/offline/ftsp/ftsp.txt");

		runProtocol(new AVTSProtocol(),
				"src/simulations/synchronization/offline/experiment2.txt",
				"src/simulations/synchronization/offline/avts/avts.txt");

		evaluateResults(
				"src/simulations/synchronization/offline/ftsp/ftsp.txt",
				"src/simulations/synchronization/offline/ftsp/ftspResults.txt");

		evaluateResults(
				"src/simulations/synchronization/offline/avts/avts.txt",
				"src/simulations/synchronization/offline/avts/avtsResults.txt");
	}

	private static void runProtocol(Protocol protocol, String experimentFile,
			String logFile) {
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

				protocol.processMessage(senderId, receiverId, senderGlobalTime,
						receiverLocalClock);

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

				if (skew >= 200)
					skew = 200;

				logger.log("" + experimentSecond + " " + skew + rates);
				if (experimentSecond > 20000)
					break;

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
