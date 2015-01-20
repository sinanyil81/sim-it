package simulations.synchronization.offline;

import java.util.Iterator;
import java.util.Vector;

import simit.hardware.Register32;

public class BroadcastEvent {

	public long sender = -1;
	public Register32 senderClock = new Register32();

	public Vector<Long> receivers = new Vector<Long>();
	public Vector<Register32> receiverClocks = new Vector<Register32>();

	public BroadcastEvent(long sender, Register32 senderClock) {
		this.sender = sender;
		this.senderClock = new Register32(senderClock);
	}

	public void addNodeData(long receiver, Register32 receiverClock) {
		receivers.add(new Long(receiver));
		receiverClocks.add(new Register32(receiverClock));
	}

	public boolean equals(BroadcastEvent event) {
		if(this.sender == event.sender && this.senderClock.compareTo(event.senderClock) == 0) {
			return true;
		}
		
		return false;
	}
	
	public String toString(){
		String str = "";
		
		int i = 0;
		for (Iterator<Register32> iterator = receiverClocks.iterator(); iterator.hasNext();) {
			Register32 receiverClock = (Register32) iterator.next();
			Long receiverId = receivers.elementAt(i++);
			str += sender + " " + receiverId.longValue() + " " + senderClock.toLong() + " " + receiverClock.toLong() + "\n";
		}
		
		return str;
	}

}
