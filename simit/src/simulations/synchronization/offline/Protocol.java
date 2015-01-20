package simulations.synchronization.offline;

public interface Protocol {
	
	/* Called to simulate the update of variables before the node with
	 * senderId broadcasted a message. 
	 */
	void preBroadcast(int senderId,long senderLocalTime);
	
	/* Node with senderID sent a synchronization message carrying GlobalTime value
	 * senderGlobalTime. This message is received by node receiverID and the message
	 * receipt time the local clock of the receiver was receiverLocal.
	 */
	void processMessage(int senderId, int receiverId, long senderGlobalTime, long receiverLocalTime);
	
	/* Returns the Global Time of the node with given ID when its local time was localTime.
	 */
	long getLogicalClock(int id, long localTime);
	
	/* Returns the Global Time of the node with given ID when its local time was localTime.
	 */
	float getRateMultiplier(int id);
}
