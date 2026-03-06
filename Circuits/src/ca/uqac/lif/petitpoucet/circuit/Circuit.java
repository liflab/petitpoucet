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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

/**
 * A circuit is a node that contains other nodes. It has a fixed number of
 * inputs and outputs, which are connected to the nodes it contains. The circuit
 * evaluates by connecting its inputs to the nodes it contains, triggering their
 * evaluation, and fetching the outputs from the nodes it contains. The circuit
 * can be duplicated, which creates a new circuit with the same structure but
 * different nodes. The circuit can also be reset, which resets all the nodes it
 * contains.
 * <p>
 * The circuit also takes care of most of the heavy lifting of the explanation
 * process. When an explanation is requested for an output of the circuit, it
 * propagates the request to the node that produces the output, and then extends
 * the explanation by connecting the inputs of the node to the inputs of the
 * circuit, and the outputs of the node to the outputs of the circuit.
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

	/**
	 * An optional name given to the circuit.
	 */
	/*@ non_null @*/ protected final String m_name;

	/**
	 * Creates a new circuit with the given input and output arities, and a
	 * default name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 */
	public Circuit(int in_arity, int out_arity)
	{
		this(in_arity, out_arity, "Circuit");
	}

	/**
	 * Creates a new circuit with the given input and output arities, and an
	 * optional name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 * @param name An optional name for the circuit
	 */
	public Circuit(int in_arity, int out_arity, String name)
	{
		super(in_arity, out_arity);
		m_nodes = new HashSet<>();
		m_inputAssociations = new UpstreamConnection[in_arity];
		m_outputAssociations = new DownstreamConnection[out_arity];
		m_name = name;
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
	protected AbstractVertex explain(int index, Part tail, VertexFactory f, int options) throws ExplanationException
	{
		return new CircuitLazyVertex(f, tail, index, options);
	}

	protected class CircuitLazyVertex extends LazyVertex
	{
		protected final int m_index;
		
		protected final int m_options;

		public CircuitLazyVertex(VertexFactory f, Part p, int index, int options)
		{
			super(f, p, options);
			m_index = index;
			m_options = options;
		}

		@Override
		public Vertex concretize(Part part)
		{
			VertexFactory subf = m_factory.subfactory(this);
			DownstreamConnection c = m_outputAssociations[m_index];
			Node n = c.getObject();
			int n_index = c.getIndex();
			Part start = CompositePart.compose(part, new OutputPart(n_index));
			try
			{
				propagateExplanation(n, start, subf);
			}
			catch (ExplanationException e)
			{
				return null;
			}
			Subgraph sg = subf.subgraph();
			extendLeaves(sg, m_factory);
			Vertex root = m_factory.getPart(CompositePart.compose(part, new OutputPart(m_index)), Circuit.this);
			root.addChild(sg);
			return root;
		}
	}

	protected void extendLeaves(Subgraph v, VertexFactory f)
	{
		List<Vertex> leaves = v.findLeaves();
		for (int j = 0; j < leaves.size(); j++)
		{
			Vertex leaf = leaves.get(j);
			if (!(leaf instanceof PartVertex))
			{
				continue;
			}
			PartVertex pv = (PartVertex) leaf;
			Part head = head(pv.getPart());
			if (!(head instanceof InputPart))
			{
				continue;
			}
			InputPart ip = (InputPart) head;
			Object o = pv.getSubject();
			if (!m_nodes.contains(o))
			{
				continue;
			}
			Node subject = (Node) o;
			for (int i = 0; i < m_inputAssociations.length; i++)
			{
				UpstreamConnection conn = m_inputAssociations[i];
				if (conn.getObject() == subject && conn.getIndex() == ip.getIndex())
				{
					Part out_part = CompositePart.compose(tail(pv.getPart()), new InputPart(i));
					v.addChild(f.getPart(out_part, this), j);
				}
			}
		}
	}

	protected Vertex propagateExplanation(Node n, Part p, VertexFactory f) throws ExplanationException
	{
		if (!(head(p) instanceof OutputPart))
		{
			throw new ExplanationException("Expected an output part");
		}
		Vertex root = f.getPart(p, n);
		Vertex explanation = AbstractVertex.get(n.explain(p, f));
		root.addChild(explanation);
		List<Vertex> leaves = explanation.findLeaves();
		for (Vertex leaf : leaves)
		{
			if (!(leaf instanceof PartVertex))
			{
				continue;
			}
			PartVertex pv = (PartVertex) leaf;
			Part n_p = head(pv.getPart());
			if (!(n_p instanceof InputPart))
			{
				continue;
			}
			InputPart ip = (InputPart) n_p;
			UpstreamConnection c = n.getUpstream(ip.getIndex());
			Part out_part = CompositePart.compose(tail(pv.getPart()), new OutputPart(c.getIndex()));
			if (f.contains(out_part, c.getObject()))
			{
				Vertex to_attach = f.getPart(out_part, c.getObject());
				leaf.addChild(to_attach);
				continue;
			}
			else if (m_nodes.contains(c.getObject()))
			{
				Vertex to_attach = propagateExplanation(c.getObject(), out_part, f);
				leaf.addChild(to_attach);
			}
		}
		return root;

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

	@Override
	/*@ pure non_null @*/ public String toString()
	{
		return m_name;
	}
}
