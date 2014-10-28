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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simit.core.SimulationEvent;
import simit.core.SimulationEventObserver;
import simit.core.Simulator;
import simit.deployment.NodeFactory;

public class InfoPanel extends JPanel implements SimulationEventObserver{
	
	JLabel numNodesCaption = new JLabel("Number of Nodes");
	JLabel maxSecondCaption = new JLabel("Simulation End Time");
	JLabel simulationSecondCaption = new JLabel("Simulation Second");
	
	JLabel numNodes = new JLabel("Number of Nodes");
	JLabel maxSecond = new JLabel("0");
	
	
	JLabel simulationSecond = new JLabel("0");
	JButton stopButton = new JButton("Exit");
	
	JButton incrementSimulationSpeed = new JButton("Slow Down");
	JButton decrementSimulationSpeed = new JButton(" Speed Up ");
	
	SimulationEvent event = new SimulationEvent(this);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InfoPanel(int w, int h) {
				
		this.setSize(w,h);
		this.setPreferredSize(new Dimension(w, h));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		numNodesCaption.setForeground(Color.RED);
		maxSecondCaption.setForeground(Color.RED);
		simulationSecondCaption.setForeground(Color.RED);
		
		add(numNodesCaption);
		add(numNodes);
		add(simulationSecondCaption);
		add(simulationSecond);
		add(maxSecondCaption);
		add(maxSecond);
		add(incrementSimulationSpeed);
		add(decrementSimulationSpeed);
		add(stopButton);
		
		stopButton.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent ae){
		           Simulator.getInstance().stopSimulation();
		           System.exit(0);
		       } 
	    });
		
		incrementSimulationSpeed.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NodePanel.SimulationSpeed+=10;
			}
		});
		
		decrementSimulationSpeed.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NodePanel.SimulationSpeed -=10;
				if(NodePanel.SimulationSpeed<0)
					NodePanel.SimulationSpeed = 0;
			}
		});
		
		
		event.register(1000000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	/**
	 * Draws the graph to a given graphics object.
	 * 
	 * @param g
	 *            The graphics to paint to
	 */
	private void draw(Graphics g) {
		numNodes.setText(""+NodeFactory.numNodes);	
		if(Simulator.getInstance().getSimulation()!=null){
			maxSecond.setText(""+Simulator.getInstance().getSimulation().getEndTime());	
		}		
		simulationSecond.setText(""+Simulator.getInstance().getSecond());		
	}

	@Override
	public void signal(SimulationEvent event) {
		this.repaint();
		event.register(1000000);		
	}
}
