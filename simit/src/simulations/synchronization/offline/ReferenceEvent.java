package simulations.synchronization.offline;

import java.util.Iterator;
import java.util.Vector;

import simit.hardware.Register32;

public class ReferenceEvent {
	public long second = -1;
	public Vector<Long> receivers = new Vector<Long>();
	public Vector<Register32> receiverClocks = new Vector<Register32>();
	
	public ReferenceEvent(long second) {
		this.second = second;
	}

	public void addNodeData(long receiver, Register32 receiverClock) {
		receivers.add(new Long(receiver));
		receiverClocks.add(new Register32(receiverClock));
	}

	public boolean equals(ReferenceEvent event) {
		if(this.second == event.second) {
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
			str += "# " + second + "" + receiverId.longValue() + " " + receiverClock.toLong() + "\n";
		}
		
		return str;
	}
}
