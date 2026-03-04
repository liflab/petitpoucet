/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A circuit is a node that contains other nodes. It has a fixed number of
 * inputs and outputs, which are connected to the nodes it contains. The circuit
 * evaluates by connecting its inputs to the nodes it contains, triggering their
 * evaluation, and fetching the outputs from the nodes it contains. The circuit
 * can be duplicated, which creates a new circuit with the same structure but
 * different nodes. The circuit can also be reset, which resets all the nodes it
 * contains.
 * @author Sylvain Hallé
 */
public class Circuit extends Node
{
	/**
	 * The nodes contained in this circuit.
	 */
	/*@ non_null @*/ protected final Set<Node> m_nodes;

	/** 
	 * The associations between the circuit's inputs and the nodes it contains.
	 */
	/*@ non_null @*/ protected final UpstreamConnection[] m_inputAssociations;

	/** 
	 * The associations between the circuit's outputs and the nodes it contains.
	 */
	/*@ non_null @*/ protected final DownstreamConnection[] m_outputAssociations;

	public Circuit(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
		m_nodes = new HashSet<>();
		m_inputAssociations = new UpstreamConnection[in_arity];
		m_outputAssociations = new DownstreamConnection[out_arity];
	}

	/**
	 * Associates an input of the circuit with an input of a node contained in the
	 * circuit. This means that when the circuit is evaluated, the value of the
	 * input will be connected to the input of the node.
	 * @param i the index of the input of the circuit
	 * @param n the node contained in the circuit
	 * @param j the index of the input of the node
	 */
	public void associateInput(int i, /*@ non_null @*/ Node n, int j)
	{
		m_inputAssociations[i] = new UpstreamConnection(n, j);
	}

	/**
	 * Associates an output of the circuit with an output of a node contained in the
	 * circuit. This means that when the circuit is evaluated, the value of the
	 * output will be fetched from the output of the node.
	 * @param i the index of the output of the circuit
	 * @param n the node contained in the circuit
	 * @param j the index of the output of the node
	 */
	public void associateOutput(int i, /*@ non_null @*/ Node n, int j)
	{
		m_outputAssociations[i] = new DownstreamConnection(n, j);
	}

	/**
	 * Adds nodes to the circuit. The nodes must be connected to the circuit's
	 * inputs and outputs using the {@link #associateInput(int, Node, int)} and
	 * {@link #associateOutput(int, Node, int)} methods.
	 * @param nodes The nodes to add to the circuit
	 */
	public void add(Node ... nodes)
	{
		for (Node n : nodes)
		{
			m_nodes.add(n);
		}
	}

	@Override
	/*@ non_null @*/ public Circuit duplicate(boolean with_state)
	{
		Circuit g = new Circuit(getInputArity(), getOutputArity());
		Map<Node,Node> fromto = new HashMap<>();
		Map<Node,Node> tofrom = new HashMap<>();
		for (Node n : m_nodes)
		{
			Node n_dup = n.duplicate(with_state);
			fromto.put(n, n_dup);
			tofrom.put(n_dup, n);
			g.add(n_dup);
		}
		for (Node n : fromto.values())
		{
			Node n_orig = tofrom.get(n);
			for (int i = 0; i < n_orig.getInputArity(); i++)
			{
				UpstreamConnection c = n_orig.getUpstream(i);
				if (c != null)
				{
					Node target = fromto.get(c.getObject());
					Connectable.connect(target, c.getIndex(), n, i);
				}
			}
			for (int i = 0; i < n_orig.getOutputArity(); i++)
			{
				DownstreamConnection c = n_orig.getDownstream(i);
				if (c != null)
				{
					Node target = fromto.get(c.getObject());
					Connectable.connect(n, i, target, c.getIndex());
				}
			}
		}
		for (int i = 0; i <  m_inputAssociations.length; i++)
		{
			UpstreamConnection c = m_inputAssociations[i];
			Node target = fromto.get(c.getObject());
			g.associateInput(i, target, c.getIndex());
		}
		for (int i = 0; i <  m_outputAssociations.length; i++)
		{
			DownstreamConnection c = m_outputAssociations[i];
			Node target = fromto.get(c.getObject());
			g.associateOutput(i, target, c.getIndex());
		}
		return g;
	}

	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		// Step 1: connect inputs with constant
		for (int i = 0; i < input.length; i++)
		{
			UpstreamConnection c = m_inputAssociations[i]; 
			Node n = c.getObject();
			Connectable.connect(new Constant(input[i]), 0, n, c.getIndex());
		}
		// Step 2: trigger evaluation and fetch outputs
		for (int i = 0; i < output.length; i++)
		{
			DownstreamConnection c = m_outputAssociations[i]; 
			Node n = c.getObject();
			output[i] = n.compute(c.getIndex());
		}
	}

	@Override
	public void reset()
	{
		super.reset();
		for (Node n : m_nodes)
		{
			n.reset();
		}
	}
}
