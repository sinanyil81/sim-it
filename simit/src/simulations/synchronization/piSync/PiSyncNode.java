/*
 * Copyright (c) 2014, Ege University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holder nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Kasım Sinan YILDIRIM (sinanyil81@gmail.com)
 *
 */
package simulations.synchronization.piSync;

import simit.core.Simulator;
import simit.hardware.Register32;
import simit.hardware.clock.Timer;
import simit.hardware.clock.TimerHandler;
import simit.hardware.transceiver.Packet;
import simit.nodes.Node;
import simit.nodes.Position;
import simit.statistics.Distribution;

public class PiSyncNode extends Node implements TimerHandler {

        private static final int BEACON_RATE = 30000000;
        private static final float MAX_PPM = 0.0001f;

        PiSyncClock logicalClock = new PiSyncClock();
        Timer timer0;

        Packet processedMsg = null;
        PiSyncMessage outgoingMsg = new PiSyncMessage();

        public PiSyncNode(int id, Position position) {
        	super(id, position);

    		CLOCK.setRandomDrift();
    		CLOCK.setDynamicDrift();

    		timer0 = new Timer(CLOCK, this);

    		/* to start clock with a random value */
    		CLOCK.setValue(new Register32(Math.abs(Distribution.getRandom()
    				.nextInt())));

    		outgoingMsg.sequence = 0;
    		outgoingMsg.rootid = NODE_ID;
    		outgoingMsg.nodeid = NODE_ID;
        }

        int calculateSkew(Packet packet) {
                PiSyncMessage msg = (PiSyncMessage) packet.getPayload();

                Register32 neighborClock = msg.clock;
                Register32 myClock = logicalClock.getValue(packet.getEventTime());

                return neighborClock.subtract(myClock).toInteger();
        }

        private static final float BOUNDARY = 2.0f * MAX_PPM * (float) BEACON_RATE;

        int lastSkew;
        float alpha = 1.0f;
        
        private void algorithm(Packet packet) {
                Register32 updateTime = packet.getEventTime();

                PiSyncMessage msg = (PiSyncMessage) packet.getPayload();

                if (msg.rootid < outgoingMsg.rootid) {
                        outgoingMsg.rootid = msg.rootid;
                        outgoingMsg.sequence = msg.sequence;
                } else if (outgoingMsg.rootid == msg.rootid
                                && (msg.sequence - outgoingMsg.sequence) > 0) {
                        outgoingMsg.sequence = msg.sequence;
                } else {
                        return;
                }

                int skew = calculateSkew(packet);

                if (Math.abs(skew) > BOUNDARY) {
                        logicalClock.setValue(logicalClock.getValue(updateTime).add(skew),
                                        updateTime);
                        logicalClock.rate = 0.0f;
                        lastSkew = 0;
                        alpha = 1.0f;

                        return;
                }
                                      
                if(Math.signum(skew) == Math.signum(lastSkew)){
                        alpha *= 2.0f;                  
                }
                else{
                        alpha /=3.0f;
                }
                
                if (alpha > 1.0f) alpha = 1.0f;         
                if(alpha < 0.0000000001f) alpha = 0.0000000001f;
                
                lastSkew = skew;
                                
                logicalClock.rate += alpha*skew;
                logicalClock.setValue(((PiSyncMessage) packet.getPayload()).clock, updateTime);
        }

        void processMsg() {
                algorithm(processedMsg);
        }

        @Override
        public void receivePacket(Packet packet) {
                processedMsg = packet;
                processMsg();
        }

        @Override
        public void fireEvent(Timer timer) {
                sendMsg();
        }

        private void sendMsg() {
                Register32 localTime, globalTime;

                localTime = CLOCK.getValue();
                globalTime = logicalClock.getValue(localTime);

                if (outgoingMsg.rootid == NODE_ID) {
                        outgoingMsg.clock = new Register32(localTime);
                } else {
                        outgoingMsg.clock = new Register32(globalTime);
                }

                Packet packet = new Packet(new PiSyncMessage(outgoingMsg));
                packet.setEventTime(new Register32(localTime));
        		sendPacket(packet);

                if (outgoingMsg.rootid == NODE_ID)
                        ++outgoingMsg.sequence;
        }

        @Override
        public void on() throws Exception {
                super.on();

                timer0.startPeriodic(BEACON_RATE
                                + ((Distribution.getRandom().nextInt() % 100) + 1) * 10000);
        }

        public Register32 local2Global() {
                return logicalClock.getValue(CLOCK.getValue());
        }

        boolean changed = false;

        public String toString() {
                String s = "" + Simulator.getInstance().getSecond();

                s += " " + NODE_ID;
                s += " " + local2Global().toString();
                s += " "
//                              + Float.floatToIntBits((float) ((1.0 + logicalClock.rate) * (1.0 + CLOCK
//                                              .getDrift())));
                                + Float.floatToIntBits((float) logicalClock.rate);
                // + Float.floatToIntBits(alpha);
                // + Float.floatToIntBits((float) (increment));//
//              if (Simulator.getInstance().getSecond() >= 5000) {
//                      // /* to start clock with a random value */
//                      if (this.NODE_ID == 18) {
//                              if (changed == false) {
//                                      CLOCK.setDrift(-0.00005f);
//                                      changed = true;
//                              }
//                      }
//              }
                // }
                // }
                // + Float.floatToIntBits(K_i);
                // System.out.println("" + NODE_ID + " "
                // + (1.0 + (double) logicalClock.rate)
                // * (1.0 + CLOCK.getDrift()));

                return s;
        }
}

