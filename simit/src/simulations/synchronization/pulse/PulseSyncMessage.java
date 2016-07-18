package simulations.synchronization.pulse;

import simit.hardware.Register32;

public class PulseSyncMessage {
	public int nodeid = -1;
	public int rootid = -1;
	public Register32 clock = new Register32();
	public int sequence = -1;
	
	public PulseSyncMessage(int nodeid,int rootid,Register32 clock,int sequence){
		this.nodeid = nodeid;
		this.rootid = rootid;
		this.clock = new Register32(clock);
		this.sequence = sequence;
	}
	
	public PulseSyncMessage(PulseSyncMessage msg){
		this.nodeid = msg.nodeid;
		this.rootid = msg.rootid;
		this.clock = new Register32(msg.clock);
		this.sequence = msg.sequence;
	}

	public PulseSyncMessage() {
	
	}
}
