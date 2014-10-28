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
 * Made use of the source code of JProwler simulator 
 * (http://w3.isis.vanderbilt.edu/projects/nest/jprowler/)
 */

/*
 * Copyright (c) 2003, Vanderbilt University
 * All rights reserved.
 *
 * 
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */
package simit.nodes;

import simit.hardware.transceiver.Packet;
import simit.hardware.transceiver.PacketListener;
import simit.hardware.transceiver.RadioSignal;
import simit.hardware.transceiver.Transceiver;

public class Channel implements PacketListener{
	protected Node source = null;

	protected Node[] neighbors = null;
	protected Transceiver edges[] = null;
	protected double[] staticFadings;
	protected double[] dynamicStrengths;

	public Channel(Node source) {
		this.source = source;
	}

	public void updateChannel(Node[] nodes) {

		Transceiver[] edges = new Transceiver[nodes.length];
		Node[] neighbors = new Node[nodes.length];
		double[] staticFadings = new double[nodes.length];

//		System.out.println("--------------------------");
//		System.out.println("Node "+source.getID()+" neighbors");
		int j = 0;
		for (int i = 0; i < nodes.length; i++) {
			if(nodes[i]!=source){
				double staticRadioStrength = RadioSignal.getStaticFading(
						source.getDistanceSquare(nodes[i]), source.getTransceiver().getTransmitPower());
//				System.out.println("**[Node"+nodes[i].getID()+"] "+source.getDistanceSquare(nodes[i]));
				if (staticRadioStrength >= RadioSignal.radioStrengthCutoff) {
					edges[j] = nodes[i].getTransceiver();
					neighbors[j] = nodes[i];
//					System.out.println("[Node"+nodes[i].getID()+"] "+source.getDistanceSquare(nodes[i]));
					staticFadings[j++] = staticRadioStrength;
				}
				
			}
		}
//		System.out.println("");
//		System.out.println("--------------------------");

		this.edges = new Transceiver[j];
		this.staticFadings = new double[j];
		this.dynamicStrengths = new double[j];
		this.neighbors = new Node[j];

		System.arraycopy(edges, 0, this.edges, 0, j);
		System.arraycopy(neighbors, 0, this.neighbors, 0, j);
		System.arraycopy(staticFadings, 0, this.staticFadings, 0, j);				
	}
	
	public void transmit(Packet packet) {
		for (int i = 0; i < dynamicStrengths.length; i++) {
			dynamicStrengths[i] = RadioSignal.getDynamicStrength(staticFadings[i]);	
		}
		
		source.getTransceiver().transmit(packet, edges,dynamicStrengths);
	}

	@Override
	public void receivePacket(Packet packet,boolean isCorrupted) {	
		if(!isCorrupted)
			source.receivePacket(packet);
//		else
//			System.out.println("Node "+source.getID()+" Corruption!");
			
	}
	
	public boolean ClearChannelAssessment(){
		return source.getTransceiver().CCA();
	}
	
	public Node[] getNeighbors(){
		return neighbors;
	}
}
