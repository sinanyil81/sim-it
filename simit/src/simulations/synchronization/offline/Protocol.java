package simulations.synchronization.offline;

public interface Protocol {
	
	/* Node with senderID sent a synchronization message carrying GlobalTime value
	 * senderGlobalTime. This message is received by node receiverID and the message
	 * receipt time the local clock of the receiver was receiverLocal.
	 */
	void processMessage(long senderId, long receiverId, long senderGlobalTime, long receiverLocal);
	
	/*
	 * Returns the Global Time of the node with given ID when its local time was localTime.
	 */
	long getLogicalClock(long id, long localTime);
}
