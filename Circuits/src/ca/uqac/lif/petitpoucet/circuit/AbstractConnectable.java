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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.Duplicable;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

public abstract class AbstractConnectable implements Connectable, Duplicable, Explainable
{
	/**
	 * The connections to the upstream nodes. The length of this list is the input
	 * arity of the node.
	 */
	/*@ non_null @*/ protected final List<UpstreamConnection> m_ins;

	/**
	 * The connections to the downstream nodes. The length of this list is the output
	 * arity of the node.
	 */
	/*@ non_null @*/ protected List<DownstreamConnection> m_outs;

	/**
	 * Creates a new node with the given input and output arities.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 */
	public AbstractConnectable(int in_arity, int out_arity)
	{
		super();
		m_ins = new ArrayList<UpstreamConnection>(in_arity);
		for (int i = 0; i < in_arity; i++)
		{
			m_ins.add(null);
		}
		m_outs = new ArrayList<DownstreamConnection>(out_arity);
		for (int i = 0; i < out_arity; i++)
		{
			m_outs.add(null);
		}
	}

	@Override
	public int getInputArity()
	{
		return m_ins.size();
	}

	@Override
	public int getOutputArity()
	{
		return m_outs.size();
	}

	@Override
	public UpstreamConnection getAssignedInput(int index)
	{
		return m_ins.get(index);
	}

	@Override
	public DownstreamConnection getAssignedOutput(int index)
	{
		return m_outs.get(index);
	}

	@Override
	public void assignInput(int i, UpstreamConnection o)
	{
		UpstreamConnection uc = (UpstreamConnection) o;
		m_ins.set(i, uc);
	}

	@Override
	public void assignOutput(int i, DownstreamConnection o)
	{
		DownstreamConnection uc = (DownstreamConnection) o;
		m_outs.set(i, uc);
	}

	@Override
	public Vertex explain(Part p, VertexFactory f) throws ExplanationException
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
	protected Vertex explain(int out_index, Part tail, VertexFactory f) throws ExplanationException
	{
		return new ConnectableVertex(f, tail);
	}
	
	@Override
	public void hint(Part p)
	{
		// By default, do nothing
	}
	
	public class ConnectableVertex extends LazyVertex
	{
		public ConnectableVertex(VertexFactory f, Part p)
		{
			super(f, p);
		}

		@Override
		public ConcreteVertex concretize(Part part, VertexFactory m_factory) throws ExplanationException
		{
			if (getInputArity() == 0)
			{
				ConcreteVertex root = m_factory.getPart(compose(tail(part), OutputPart.FIRST), AbstractConnectable.this);
				return root;
			}
			Vertex inside;
			if (getInputArity() == 1)
			{
				ConcreteVertex root = m_factory.getPart(compose(part, OutputPart.FIRST), AbstractConnectable.this);
				Part in_p = compose(part, InputPart.FIRST);
				ConcreteVertex child = m_factory.getPart(in_p, AbstractConnectable.this);
				root.addChild(child);
				return root;
			}
			else
			{
				inside = m_factory.getAnd();
				for (int i = 0; i < getInputArity(); i++)
				{
					Part in_p = compose(part, new InputPart(i));
					inside.addChild(m_factory.getPart(in_p, AbstractConnectable.this));
				}
			}
			ConcreteVertex root = m_factory.getPart(CompositePart.compose(part, OutputPart.FIRST), AbstractConnectable.this);
			root.addChild(inside);
			return root;
		}
	}
}
