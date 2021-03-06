package simulations.synchronization.offline.ftsp;

import simit.hardware.Register32;
import simulations.shared.regression.LeastSquares;
import simulations.shared.regression.RegressionEntry;

public class FTSPContiniousNode {
	private static final int MAX_ENTRIES = 8;
	private static final int ENTRY_THROWOUT_LIMIT = 5000;
	private static final int ENTRY_VALID_LIMIT = 3; // number of entries to
													// become synchronized

	LeastSquares ls = new LeastSquares();
	LeastSquares ls2 = new LeastSquares();
	RegressionEntry table[] = new RegressionEntry[MAX_ENTRIES];
	int tableEntries = 0;
	int numEntries;
	int id;
	
	int sequence = 0;

	public FTSPContiniousNode(int id) {
		
		this.id = id;
		for (int i = 0; i < table.length; i++) {
			table[i] = new RegressionEntry();
		}
		
		clearTable();
	}

	private int numErrors = 0;

	void addNewEntry(Register32 globalTime, Register32 localTime) {
		int i, freeItem = -1, oldestItem = 0;
		Register32 age, oldestTime = new Register32();
		int timeError;

		// clear table if the received entry's been inconsistent for some time
		timeError = local2Global(localTime).toInteger() - globalTime.toInteger();

		if (is_synced()
				&& (timeError > ENTRY_THROWOUT_LIMIT || timeError < -ENTRY_THROWOUT_LIMIT)) {
			if (++numErrors > 3)
				clearTable();
			return; // don't incorporate a bad reading
		}

		tableEntries = 0; // don't reset table size unless you're recounting
		numErrors = 0;

		for (i = 0; i < MAX_ENTRIES; ++i) {
			age = new Register32(localTime);
			age = age.subtract(table[i].x);

			// logical time error compensation
			if (age.toLong() >= 0x7FFFFFFFL)
				table[i].free = true;

			if (table[i].free)
				freeItem = i;
			else
				++tableEntries;

			if (age.compareTo(oldestTime) >= 0) {
				oldestTime = age;
				oldestItem = i;
			}
		}

		if (freeItem < 0)
			freeItem = oldestItem;
		else
			++tableEntries;

		table[freeItem].free = false;
		table[freeItem].x = new Register32(localTime);
		table[freeItem].y = globalTime.toInteger() - localTime.toInteger();

		/* calculate new least-squares line */
		ls.calculate(table, tableEntries);
		numEntries = tableEntries;
		
		timeError = local2Global(localTime).toInteger() - local2GlobalContinious(localTime).toInteger();
		
		System.out.println(""+timeError);
		if(is_synced()== false){
			ls2.setMeanX(ls.getMeanX());
			ls2.setSlope(ls.getSlope());
			ls2.setMeanY(ls.getMeanY());
		}
		else if(timeError>=0 ){
			ls2.setMeanX(ls.getMeanX());
			ls2.setSlope(ls.getSlope());
			ls2.setMeanY(ls.getMeanY());		
			
		}
		else if (timeError < 0){
			ls2.setMeanX(ls.getMeanX());
			ls2.setSlope(ls.getSlope());
			ls2.setMeanY(ls.getMeanY()+timeError);
		}
	}

	private void clearTable() {
		int i;

		for (i = 0; i < MAX_ENTRIES; ++i)
			table[i].free = true;

		numEntries = 0;
	}

	public boolean is_synced() {
		if (numEntries >= ENTRY_VALID_LIMIT || id == FTSPProtocol.REFERENCE) // node 1 is always synced
			return true;
		else
			return false;
	}

	public Register32 local2Global(Register32 clock) {

		return ls.calculateY(clock);
	}
	
	public Register32 local2GlobalContinious(Register32 clock) {

		return ls2.calculateY(clock);
	}
	
	public float getSkew(){
		return ls.getSlope();
	}
	
	public void preBroadcast(){
		if(id==FTSPProtocol.REFERENCE) sequence++;
	}
}
