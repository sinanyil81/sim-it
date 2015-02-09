package simulations.synchronization.offline.ftsp;

import simit.hardware.Register32;
import simulations.synchronization.offline.Protocol;

public class FTSPContiniousProtocol implements Protocol {

	public static final int REFERENCE = 1;

	FTSPContiniousNode nodes[] = new FTSPContiniousNode[20];

	public FTSPContiniousProtocol() {
		for (int i = 0; i < 20; i++) {
			nodes[i]=new FTSPContiniousNode(i+1);
		}
	}

	@Override
	public void processMessage(int senderId, int receiverId,
			long senderGlobalTime, long receiverLocalTime) {
		// TODO Auto-generated method stub
		if(nodes[senderId-1].is_synced() && 
				nodes[senderId-1].sequence > nodes[receiverId-1].sequence){
			nodes[receiverId-1].sequence = nodes[senderId-1].sequence;
			nodes[receiverId-1].addNewEntry(new Register32(senderGlobalTime),
											new Register32(receiverLocalTime));
		}
	}

	@Override
	public long getLogicalClock(int id, long localTime) {
		return nodes[id-1].local2Global(new Register32(localTime)).toLong();
	}
	
	public long getLogicalClockContinious(int id, long localTime) {
		return nodes[id-1].local2GlobalContinious(new Register32(localTime)).toLong();
	}

	@Override
	public void preBroadcast(int senderId, long senderLocalTime) {
		nodes[senderId-1].preBroadcast();
		
	}

	@Override
	public float getRateMultiplier(int id) {
		return nodes[id].getSkew();
	}
}
