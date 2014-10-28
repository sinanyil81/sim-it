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
package simit.deployment;

import java.lang.reflect.Constructor;

import simit.nodes.Node;
import simit.nodes.Position;

public class NodeFactory {

	static public int numNodes;
	static public Node[] nodes = null;
	static public DeploymentArea area = null;

	public static void createNodes(String classToLoad, int numNodes,
			DeploymentArea area) {
		NodeFactory.numNodes = numNodes;
		NodeFactory.area = area;

		nodes = new Node[numNodes];
		Deployment deployment = new RandomDeployment(area);

		for (int i = 0; i < numNodes; i++) {
			nodes[i] = createNode(classToLoad, i + 1,
					deployment.getNextPosition());
		}

		updateNeighborhood();
	}

	/**
	 * Should be called after each node position change or transmit power
	 * modification of the transceiver.
	 */
	public static void updateNeighborhood() {
		for (int i = 0; i < numNodes; i++) {
			nodes[i].getChannel().updateChannel(nodes);
		}
	}

	public static void turnOnNodes() {
		for (int i = 0; i < NodeFactory.numNodes; i++) {
			try {
				NodeFactory.nodes[i].on();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static Node createNode(String className, int id, Position position) {
		Class<?> c;
		Object object = null;
		try {
			c = Class.forName(className);
			Constructor<?> cons = c.getConstructor(int.class, Position.class);
			object = cons.newInstance(new Object[] { id, position });
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem loading/finding class " + className);
			System.exit(-1);
		}

		return (Node) object;
	}
}
