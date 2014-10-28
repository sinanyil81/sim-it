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
package simit.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import simit.core.SimulationEvent;
import simit.core.SimulationEventObserver;
import simit.deployment.NodeFactory;
import simit.nodes.Node;

public class NodePanel extends JPanel implements SimulationEventObserver {
	
	private static final long serialVersionUID = 1L;
	SimulationEvent event = new SimulationEvent(this);
	
	public static int SimulationSpeed = 0;

	public NodePanel(int w, int h) {
		this.setSize(w, h);
		this.setPreferredSize(new Dimension(w, h));
		event.register(1000000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.WHITE);	
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		draw(g);
	}

	/**
	 * Draws the graph to a given graphics object.
	 * 
	 * @param g
	 *            The graphics to paint to
	 */
	private void draw(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
		
		for (int i = 0; i < NodeFactory.numNodes; i++) {
			Node node = NodeFactory.nodes[i];
			simit.nodes.Position pos1 = node.getPosition();

			if(node.getID() == 1)
				g2.setColor(Color.RED);
			else
				g2.setColor(Color.LIGHT_GRAY);
			
			g2.fillOval((int)pos1.xCoord-6, (int)pos1.yCoord-6, 12, 12);
			
//			g2.setColor(Color.BLACK);
//			g2.drawString(""+node.getID(),(int) pos1.xCoord,(int) pos1.yCoord);

			Node[] neighbors = node.getChannel().getNeighbors();
			if(neighbors!=null){
				for (int j = 0; j < neighbors.length; j++) {
					simit.nodes.Position pos2 = neighbors[j].getPosition();
					g2.setColor(Color.BLUE);
					g2.setStroke(new BasicStroke(2));
					g2.drawLine((int)pos1.xCoord, (int)pos1.yCoord, (int)pos2.xCoord, (int)pos2.yCoord);
				}				
			}
		}
	}

	@Override
	public void signal(SimulationEvent event) {
		event.register(1000000);
		
		if(SimulationSpeed>0){
			try {
				Thread.sleep(SimulationSpeed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		this.repaint();
	}
}
