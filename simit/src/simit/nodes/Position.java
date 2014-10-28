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
package simit.nodes;

public class Position {

	public double xCoord;
	public double yCoord;
	public double zCoord;

	public Position() {
		xCoord = 0;
		yCoord = 0;
		zCoord = 0;
	}
	
	public Position(double x, double y, double z) {
		xCoord = (int)x;
		yCoord = (int)y;
		zCoord = (int)z;
	}

	public void set(Position p) {
		xCoord = p.xCoord;
		yCoord = p.yCoord;
		zCoord = p.zCoord;
	}

	public void set(int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public double distanceTo(Position pos) {
		return Math.sqrt(squareDistanceTo(pos));
	}

	public double squareDistanceTo(Position pos) {
		return ((xCoord - pos.xCoord) * (xCoord - pos.xCoord))
				+ ((yCoord - pos.yCoord) * (yCoord - pos.yCoord))
				+ ((zCoord - pos.zCoord) * (zCoord - pos.zCoord));
	}

	public boolean equals(Position p) {
		return (p.xCoord == xCoord && p.yCoord == yCoord && p.zCoord == zCoord);
	}
	
	public String toString(){
		return ""+(int)xCoord+" "+(int)yCoord+" "+(int)zCoord;
	}
}
