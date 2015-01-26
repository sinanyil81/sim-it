package simulations.synchronization.offline.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import simit.hardware.Register32;
import simulations.shared.Logger;

public class Reader {

	static BufferedReader in = null;
	Vector<Object> events = new Vector<Object>();
	
	public static void main(String[] args) {
		Reader  r = new Reader("src/simulations/synchronization/offline/simulation/timestamps4.txt"," :[]=x");
		System.out.println("Events loaded");
		
		Logger logger = new Logger("src/simulations/synchronization/offline/experiment4.txt");
		
		for (Iterator<Object> iterator = r.getEvents().iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			
			if (object instanceof BroadcastEvent) {
				logger.log(((BroadcastEvent)object).toString());
			}
			else if (object instanceof ReferenceEvent) {
				logger.log(((ReferenceEvent)object).toString());
			}
			
		}
		
		logger.close();
	}

	public Reader(String infile, String delimiters) {
		try {
			readFile(infile, delimiters);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void openFile(String infile) {
		try {
			in = new BufferedReader(new FileReader(new File(infile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String nextLine(){
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void readFile(String file, String delimiters)
			throws FileNotFoundException {
		in = new BufferedReader(new FileReader(new File(file)));

		try {
			readMatrix(delimiters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readMatrix(String delimiters) throws Exception {
		String line = null;

		try {
			while ((line = in.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line,delimiters);

				while (tokenizer.hasMoreElements()) {
					String s = tokenizer.nextToken();

					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					long senderId = Long.valueOf(s, 16);
					
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					long receiverId = Long.valueOf(s, 16);
					
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					long senderClock = Long.valueOf(s, 16);
									
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					s = tokenizer.nextToken();
					long receiverClock = Long.valueOf(s, 16);

					if (senderId == 0) {
					  // do nothing	
					}
					else if (senderId != 65535) {

						BroadcastEvent currentEvent = searchEvent(new BroadcastEvent(senderId, new Register32(senderClock)));
						currentEvent.receivers.addElement(new Long(receiverId));
						currentEvent.receiverClocks.addElement(new Register32(receiverClock));
					}
					else{
						ReferenceEvent currentEvent = searchEvent(new ReferenceEvent(senderClock));									
						currentEvent.receivers.addElement(new Long(receiverId));
						currentEvent.receiverClocks.addElement(new Register32(receiverClock));
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	ReferenceEvent searchEvent(ReferenceEvent event) {
		
		for (Iterator<Object> iterator = events.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof ReferenceEvent) {
				if (((ReferenceEvent) object).equals(event))
					return (ReferenceEvent) object;
			}
		}
		
		events.addElement(event);
		return event;
	}
	
	BroadcastEvent searchEvent(BroadcastEvent event) {
		for (Iterator<Object> iterator = events.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof BroadcastEvent) {
				if (((BroadcastEvent) object).equals(event))
					return (BroadcastEvent) object;
			}
		}

		events.addElement(event);
		return event;
	}
	
	public Vector<Object> getEvents(){
		return this.events;
	}

}
