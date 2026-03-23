/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (Connectable) 2016-2026 Laboratoire d'informatique formelle
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

import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.Duplicable;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

/**
 * A circuit is a node that contains other nodes. It has a fixed number of
 * inputs and outputs, which are connected to the nodes it contains. The circuit
 * evaluates by connecting its inputs to the nodes it contains, triggering their
 * evaluation, and fetching the outputs from the nodes it contains. The circuit
 * can be duplicated, which creates a new circuit with the same structure but
 * different nodes.
 * <p>
 * The circuit also takes care of most of the heavy lifting of the explanation
 * process. When an explanation is requested for an output of the circuit, it
 * propagates the request to the node that produces the output, and then extends
 * the explanation by connecting the inputs of the node to the inputs of the
 * circuit, and the outputs of the node to the outputs of the circuit.
 * @author Sylvain Hallé
 */
public abstract class CompositeConnectable<C extends Connectable> extends AbstractConnectable
{
	/**
	 * The nodes contained in this circuit.
	 */
	/*@ non_null @*/ protected final Set<C> m_nodes;
	
	/** 
	 * The associations between the circuit's inputs and the nodes it contains.
	 */
	/*@ non_null @*/ protected final List<UpstreamConnection> m_inputAssociations;

	/** 
	 * The associations between the circuit's outputs and the nodes it contains.
	 */
	/*@ non_null @*/ protected final List<DownstreamConnection> m_outputAssociations;

	/**
	 * An optional name given to the circuit.
	 */
	/*@ non_null @*/ protected final String m_name;

	/**
	 * Creates a new circuit with the given input and output arities, and an
	 * optional name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 * @param name An optional name for the circuit
	 */
	public CompositeConnectable(int in_arity, int out_arity, String name)
	{
		super(in_arity, out_arity);
		m_nodes = new HashSet<>();
		m_inputAssociations = new ArrayList<>(in_arity);
		m_outputAssociations = new ArrayList<>(out_arity);
		for (int i = 0; i < in_arity; i++)
		{
			m_inputAssociations.add(null);
		}
		for (int i = 0; i < out_arity; i++)
		{
			m_outputAssociations.add(null);
		}
		m_name = name;
	}

	/**
	 * Creates a new circuit with the given input and output arities, and a
	 * default name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 */
	public CompositeConnectable(int in_arity, int out_arity)
	{
		this(in_arity, out_arity, null);
	}

	/**
	 * Associates an input of the circuit with an input of a node contained in the
	 * circuit. This means that when the circuit is evaluated, the value of the
	 * input will be connected to the input of the node.
	 * @param i the index of the input of the circuit
	 * @param n the node contained in the circuit
	 */
	public void associateInput(int i, /*@ non_null @*/ C n, int j)
	{
		m_inputAssociations.set(i, newInputAssociation(n, j));
	}

	/**
	 * Associates an output of the circuit with an output of a node contained in the
	 * circuit. This means that when the circuit is evaluated, the value of the
	 * output will be fetched from the output of the node.
	 * @param i the index of the output of the circuit
	 * @param n the node contained in the circuit
	 * @param j the index of the output of the node
	 */
	public void associateOutput(int i, /*@ non_null @*/ C n, int j)
	{
		m_outputAssociations.set(i, newOutputAssociation(n, j));
	}

	/**
	 * Adds nodes to the circuit. The nodes must be connected to the circuit's
	 * inputs and outputs using the {@link #associateInput(int, Node, int)} and
	 * {@link #associateOutput(int, Node, int)} methods.
	 * @param nodes The nodes to add to the circuit
	 */
	@SuppressWarnings("unchecked")
	public void add(C ... nodes)
	{
		for (C n : nodes)
		{
			m_nodes.add(n);
		}
	}
	
	public int getInputIndex(Object target, int index)
	{
		for (int i = 0; i < m_inputAssociations.size(); i++)
		{
			UpstreamConnection c = m_inputAssociations.get(i);
			Connectable n = c.getObject();
			if (n.equals(target) && c.getIndex() == index)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	protected Vertex explain(int index, Part tail, VertexFactory f) throws ExplanationException
	{
		return new CompositeConnectableVertex(f, tail, index);
	}

	protected class CompositeConnectableVertex extends LazyVertex
	{
		protected final int m_index;

		public CompositeConnectableVertex(VertexFactory f, Part p, int index)
		{
			super(f, p);
			m_index = index;
		}

		@Override
		public ConcreteVertex concretize(Part part, VertexFactory m_factory) throws ExplanationException
		{
			VertexFactory subf = m_factory.subfactory(this);
			DownstreamConnection c = m_outputAssociations.get(m_index);
			Connectable n = c.getObject();
			int n_index = c.getIndex();
			Part start = CompositePart.compose(part, new OutputPart(n_index));
			if (n instanceof Explainable)
			{
				propagateExplanation((Explainable) n, start, subf);
			}

			Subgraph sg = subf.subgraph();
			extendLeaves(sg, m_factory);
			ConcreteVertex root = m_factory.getPart(CompositePart.compose(part, new OutputPart(m_index)), CompositeConnectable.this);
			root.addChild(sg);
			return root;
		}

		protected void extendLeaves(Subgraph sg, VertexFactory m_factory) throws ExplanationException
		{
			for (Vertex leaf : sg.innerLeaves())
			{
				Part p = isLeafToExtend(leaf);
				if (p == null)
					continue;
				int i = getInputIndex(((PartVertex) leaf).getSubject(), ((InputPart) head(p)).getIndex());
				if (i < 0)
				{
					throw new ExplanationException("Leaf not found");
				}
				PartVertex pv = m_factory.getPart(CompositePart.compose(tail(p), new InputPart(i)), CompositeConnectable.this);
				sg.addChild(pv, leaf);
			}
		}

		protected Part isLeafToExtend(Vertex v)
		{
			if (!(v instanceof PartVertex))
			{
				return null;
			}
			PartVertex pv = (PartVertex) v;
			Part head = head(pv.getPart());
			if (!(head instanceof InputPart))
			{
				return null;
			}
			return pv.getPart();
		}

		protected ConcreteVertex propagateExplanation(Explainable n, Part p, VertexFactory f) throws ExplanationException
		{
			if (!(head(p) instanceof OutputPart))
			{
				throw new ExplanationException("Expected an output part");
			}
			ConcreteVertex explanation = Vertex.get(n.explain(p, f));
			ConcreteVertex root = explanation;
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
				if (!(n instanceof Connectable))
				{
					throw new ExplanationException("Expected a connectable node");
				}
				Connection c = ((Connectable) n).getAssignedInput(ip.getIndex());
				Part out_part = CompositePart.compose(tail(pv.getPart()), new OutputPart(c.getIndex()));
				Connectable c_o = c.getObject();
				if (f.contains(out_part, c_o))
				{
					ConcreteVertex to_attach = f.getPart(out_part, c.getObject());
					leaf.addChild(to_attach);
					continue;
				}
				else if (m_nodes.contains(c_o))
				{
					if (!(c_o instanceof Explainable))
					{
						throw new ExplanationException("Expected an explainable node");
					}
					ConcreteVertex to_attach = propagateExplanation((Explainable) c_o, out_part, f);
					leaf.addChild(to_attach);
				}
			}
			return root;
		}
	}
	
	public abstract CompositeConnectable<C> duplicate(boolean with_state);

	@SuppressWarnings("unchecked")
	protected void duplicate(CompositeConnectable<C> g, boolean with_state)
	{
		Map<C,C> fromto = new HashMap<>();
		Map<C,C> tofrom = new HashMap<>();
		for (C n : m_nodes)
		{
			if (!(n instanceof Duplicable))
			{
				throw new UnsupportedOperationException("Cannot duplicate " + n);
			}
			C n_dup = (C) ((Duplicable) n).duplicate(with_state);
			fromto.put(n, n_dup);
			tofrom.put(n_dup, n);
			g.add(n_dup);
		}
		for (C n : fromto.values())
		{
			C n_orig = tofrom.get(n);
			for (int i = 0; i < n_orig.getInputArity(); i++)
			{
				UpstreamConnection c = n_orig.getAssignedInput(i);
				if (c != null)
				{
					Connectable target = fromto.get(c.getObject());
					getConnector().connectElements(target, c.getIndex(), n, i);
				}
			}
			for (int i = 0; i < n_orig.getOutputArity(); i++)
			{
				DownstreamConnection c = n_orig.getAssignedOutput(i);
				if (c != null)
				{
					C target = fromto.get(c.getObject());
					getConnector().connectElements(n, i, target, c.getIndex());
				}
			}
		}
		for (int i = 0; i <  m_inputAssociations.size(); i++)
		{
			UpstreamConnection c = m_inputAssociations.get(i);
			C target = fromto.get(c.getObject());
			g.associateInput(i, target, c.getIndex());
		}
		for (int i = 0; i <  m_outputAssociations.size(); i++)
		{
			DownstreamConnection c = m_outputAssociations.get(i);
			C target = fromto.get(c.getObject());
			g.associateOutput(i, target, c.getIndex());
		}
	}

	@Override
	/*@ pure non_null @*/ public String toString()
	{
		return m_name == null ? "Circuit" : m_name;
	}	

	protected abstract UpstreamConnection newInputAssociation(C n, int i);

	protected abstract DownstreamConnection newOutputAssociation(C n, int i);

}