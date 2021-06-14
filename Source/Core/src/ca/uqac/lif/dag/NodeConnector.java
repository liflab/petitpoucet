/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.dag;

/**
 * Provides methods to connect nodes together.
 * 
 * @author Sylvain Hallé
 */
public class NodeConnector
{
	/**
	 * A static instance of the node connector.
	 */
	public static final transient NodeConnector instance = new NodeConnector();

	/**
	 * Disconnects an output pin of a node to the input pin of another node.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public static void disconnect(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		instance.disconnectFrom(n1, i1, n2, i2);
	}

	/**
	 * Cuts the directed link between a node and another one. This does not
	 * remove the reciprocal link in the reverse direction. This method can be
	 * used when the downstream node is completely removed from a graph.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public static void cutFrom(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		n1.removeFromOutput(i1, n2.getInputPin(i2));
	}

	/**
	 * Connects an output pin of a node to the input pin of another node.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public static void connect(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		instance.connectTo(n1, i1, n2, i2);
	}

	/**
	 * Connects an output pin of a node to the input pin of another node.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public void connectTo(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		Pin<? extends Node> out_p1 = n1.getOutputPin(i1);
		Pin<? extends Node> in_p2 = n2.getInputPin(i2);
		if (!n1.getOutputLinks(i1).contains(in_p2))
		{
			n1.addToOutput(i1, in_p2);
		}
		if (!n2.getInputLinks(i2).contains(out_p1))
		{
			n2.addToInput(i2, out_p1);
		}
	}

	/**
	 * Disconnects an output pin of a node to the input pin of another node.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public void disconnectFrom(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		n1.removeFromOutput(i1, n2.getInputPin(i2));
		n2.removeFromInput(i2, n1.getOutputPin(i1));
	}
}
