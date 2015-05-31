package simulations.synchronization.offline.piSync;

import simit.hardware.Register32;
import simulations.synchronization.offline.Protocol;

public class PiSyncProtocol implements Protocol{
	public static final int REFERENCE = 1;
	PiSyncNode nodes[] = new PiSyncNode[20];
	
	public PiSyncProtocol() {
		for (int i = 0; i < 20; i++) {
			nodes[i]=new PiSyncNode(i+1);
		}
	}

	@Override
	public void preBroadcast(int senderId, long senderLocalTime) {
		nodes[senderId-1].preBroadcast();
	}

	@Override
	public void processMessage(int senderId, int receiverId,
			long senderGlobalTime, long receiverLocal) {
		if(nodes[senderId-1].sequence > nodes[receiverId-1].sequence){
			nodes[receiverId-1].sequence = nodes[senderId-1].sequence;
			nodes[receiverId-1].adjustClock(new Register32(senderGlobalTime),
											new Register32(receiverLocal));
		}
		
	}

	@Override
	public long getLogicalClock(int id, long localTime) {
		return nodes[id-1].local2Global(new Register32(localTime)).toLong();
	}

	@Override
	public float getRateMultiplier(int id) {
		return nodes[id].getSkew();
	}


}
