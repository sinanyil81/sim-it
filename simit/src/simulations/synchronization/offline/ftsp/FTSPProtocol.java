package simulations.synchronization.offline.ftsp;

import simulations.synchronization.offline.Protocol;

public class FTSPProtocol implements Protocol {

	FTSPNode nodes[] = new FTSPNode[20];

	public FTSPProtocol() {
		for (int i = 0; i < 20; i++) {
			nodes[i]=new FTSPNode(i+1);
		}
	}

	@Override
	public void processMessage(long senderId, long receiverId,
			long senderGlobalTime, long receiverLocalTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getLogicalClock(long id, long localTime) {
		// TODO Auto-generated method stub
		return 0;
	}
}
