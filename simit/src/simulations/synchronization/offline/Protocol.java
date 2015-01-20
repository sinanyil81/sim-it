package simulations.synchronization.offline;

public interface Protocol {
	void processMessage(long senderId, long receiverId, long senderGlobalTime, long receiverLocal);
	long getLogicalClock(long id, long localTime);
}
