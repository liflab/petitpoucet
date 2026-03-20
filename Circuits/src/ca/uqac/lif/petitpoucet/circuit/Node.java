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

import static ca.uqac.lif.petitpoucet.CompositePart.compose;
import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Duplicable;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Vertex;

/**
 * A node in a circuit. A node has a fixed number of inputs and outputs, and
 * computes its output values from its input values. The output values are
 * cached until the node is reset.
 * @see Connectable
 * @see Computable
 * @author Sylvain Hallé
 */
public abstract class Node implements Connectable, Computable, Duplicable, Explainable
{
	/**
	 * The connections to the upstream nodes. The length of this array is the input
	 * arity of the node.
	 */
	/*@ non_null @*/ protected final UpstreamConnection[] m_ins;

	/**
	 * The connections to the downstream nodes. The length of this array is the output
	 * arity of the node.
	 */
	/*@ non_null @*/ protected final DownstreamConnection[] m_outs;

	/**
	 * The cached output values. This is set to null when the node is reset, and
	 * recomputed when the node is computed again.
	 */
	/*@ null @*/ protected Object[] m_outputArguments;

	/**
	 * Creates a new node with the given input and output arities.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 */
	public Node(int in_arity, int out_arity)
	{
		super();
		m_ins = new UpstreamConnection[in_arity];
		m_outs = new DownstreamConnection[out_arity];
		m_outputArguments = null;
	}

	@Override
	public Object compute(int index)
	{
		Object[] arguments = new Object[m_ins.length];
		if (m_outputArguments == null)
		{
			m_outputArguments = new Object[m_outs.length];
			for (int i = 0; i < m_ins.length; i++)
			{
				UpstreamConnection c = m_ins[i];
				arguments[i] = c.getObject().compute(c.getIndex());
			}
			evaluate(arguments, m_outputArguments);
		}
		return m_outputArguments[index];
	}

	public Object evaluate(Object ... inputs)
	{
		for (int i = 0; i < inputs.length; i++)
		{
			Connectable.connect(new Argument(inputs[i], i), 0, this, i);
		}
		return compute();
	}

	@Override
	public Node duplicate()
	{
		return duplicate(false);
	}

	@Override
	public abstract Node duplicate(boolean with_state);

	/**
	 * Evaluates the node's output values from its input values. This method is called
	 * when the node is computed for the first time after a reset. The input values are
	 * passed in the {@code input} array, and the output values must be stored in
	 * the {@code output} array.
	 * @param input The input values, in the order of the node's inputs
	 * @param output The output values, in the order of the node's outputs.
	 * This array is pre-allocated and must be filled by this method.
	 */
	protected abstract void evaluate(/*@ non_null @*/ Object[] input, /*@ non_null @*/ Object[] output);

	@Override
	public void reset()
	{
		m_outputArguments = null;
	}

	@Override
	public int getInputArity()
	{
		return m_ins.length;
	}

	@Override
	public int getOutputArity()
	{
		return m_outs.length;
	}

	@Override
	public UpstreamConnection getUpstream(int index)
	{
		return m_ins[index];
	}

	@Override
	public DownstreamConnection getDownstream(int index)
	{
		return m_outs[index];
	}

	@Override
	public void assignInput(int i, Connectable c, int j)
	{
		if (!(c instanceof Node))
		{
			throw new IllegalArgumentException("Connectable must be a node");
		}
		m_ins[i] = new UpstreamConnection((Node) c, j);
	}

	@Override
	public void assignOutput(int i, Connectable c, int j)
	{
		if (!(c instanceof Node))
		{
			throw new IllegalArgumentException("Connectable must be a node");
		}
		m_outs[i] = new DownstreamConnection((Node) c, j);
	}

	@Override
	public AbstractVertex explain(Part p, VertexFactory f) throws ExplanationException
	{
		Part p_tail = tail(p);
		int index = checkHead(p);
		return explain(index, p_tail, f);
	}

	/**
	 * Checks that the head of the part is an output part with a valid index,
	 * and returns the index of the output part.
	 * @param p The part to check
	 * @return The index of the output part
	 * @throws ExplanationException If the head of the part is not an output part,
	 * or if the index is out of bounds
	 */
	protected int checkHead(Part p) throws ExplanationException
	{
		Part p_head = head(p);
		if (!(p_head instanceof OutputPart))
		{
			throw new ExplanationException("Expected an output part");
		}
		OutputPart op = (OutputPart) p_head;
		if (op.getIndex() < 0 || op.getIndex() >= getOutputArity())
		{
			throw new ExplanationException("Output index out of bounds");
		}
		return op.getIndex();
	}

	/**
	 * Explains the output of this node at the given index, given the tail of
	 * the part. The default implementation is to create an AND vertex with one
	 * child for each input, where the child is the explanation of
	 * the input part corresponding to the input index. Subclasses can override
	 * this method to provide a different explanation.
	 * @param out_index The index of the output to explain
	 * @param tail The tail of the part
	 * @param f The vertex factory to use to create the explanation vertices
	 * @param options The options to pass
	 * @return A vertex explaining the output of this node at the given index
	 * @throws ExplanationException
	 */
	protected AbstractVertex explain(int out_index, Part tail, VertexFactory f) throws ExplanationException
	{
		return new NodeLazyVertex(f, tail);
	}

	@Override
	public void hint(Part p)
	{
		// By default, do nothing
	}

	public class NodeLazyVertex extends LazyVertex
	{
		public NodeLazyVertex(VertexFactory f, Part p)
		{
			super(f, p);
		}

		@Override
		public Vertex concretize(Part part)
		{
			if (getInputArity() == 0)
			{
				Vertex root = m_factory.getPart(compose(tail(part), OutputPart.FIRST), Node.this);
				return root;
			}
			Vertex inside;
			if (getInputArity() == 1)
			{
				Vertex root = m_factory.getPart(compose(part, OutputPart.FIRST), Node.this);
				Part in_p = compose(part, InputPart.FIRST);
				Vertex child = m_factory.getPart(in_p, Node.this);
				root.addChild(child);
				return root;
			}
			else
			{
				inside = m_factory.getAnd();
				for (int i = 0; i < getInputArity(); i++)
				{
					Part in_p = compose(part, new InputPart(i));
					inside.addChild(m_factory.getPart(in_p, Node.this));
				}
			}
			Vertex root = m_factory.getPart(CompositePart.compose(part, OutputPart.FIRST), Node.this);
			root.addChild(inside);
			return root;
		}
	}

	public static class Argument extends Constant
	{
		protected final int m_index;

		public Argument(Object o, int index)
		{
			super(o);
			m_index = index;
		}

		/*@ pure @*/ public int getIndex()
		{
			return m_index;
		}

		@Override
		public int hashCode()
		{
			return m_value.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof Argument && ((Argument) o).m_value.equals(m_value);
		}

		@Override
		public String toString()
		{
			return "'" + m_value.toString();
		}

	}

	/**
	 * A connection to an upstream node.
	 */
	public static class UpstreamConnection extends Connection
	{
		/**
		 * Creates a new upstream connection to the given node and index.
		 * @param c The node to connect to
		 * @param i The index of the output of the node to connect to
		 */
		public UpstreamConnection(Connectable c, int i)
		{
			super(c, i);
		}

		@Override
		public Node getObject()
		{
			return (Node) m_connectable;
		}

		@Override
		public int hashCode()
		{
			return m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof UpstreamConnection &&
					((UpstreamConnection) o).m_connectable.equals(m_connectable) &&
					((UpstreamConnection) o).m_index == m_index;
		}

		@Override
		public String toString()
		{
			return "\u2192" + m_index + m_connectable.toString();
		}
	}

	/**
	 * A connection to an downstream node.
	 */
	public static class DownstreamConnection extends Connection
	{
		/**
		 * Creates a new downstream connection to the given node and index.
		 * @param c The node to connect to
		 * @param i The index of the input of the node to connect to
		 */
		public DownstreamConnection(Connectable c, int i)
		{
			super(c, i);
		}

		@Override
		public Node getObject()
		{
			return (Node) m_connectable;
		}

		@Override
		public int hashCode()
		{
			return m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof DownstreamConnection &&
					((DownstreamConnection) o).m_connectable.equals(m_connectable) &&
					((DownstreamConnection) o).m_index == m_index;
		}

		@Override
		public String toString()
		{
			return m_connectable.toString() + m_index + "\u2192";
		}
	}
}
