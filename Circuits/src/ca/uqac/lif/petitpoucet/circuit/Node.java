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

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

public abstract class Node implements Connectable, Computable, Explainable
{
	protected final UpstreamConnection[] m_ins;
	
	protected final DownstreamConnection[] m_outs;
	
	protected Object[] m_outputArguments;
	
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
	
	protected abstract void evaluate(Object[] input, Object[] output);
	
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
	public Vertex explain(Part p, VertexFactory f) throws ExplanationException
	{
		Part p_tail = tail(p);
		AndVertex a = f.getAnd();
		for (int i = 0; i < getInputArity(); i++)
		{
			Part in_p = compose(p_tail, new InputPart(i));
			a.addChild(f.getPart(in_p, this));
		}
		return a;
	}
	
	@Override
	public void hint(Part p)
	{
		// By default, do nothing
	}
	
	public static class UpstreamConnection extends Connection
	{
		public UpstreamConnection(Connectable c, int i)
		{
			super(c, i);
		}
		
		@Override
		public Node getObject()
		{
			return (Node) m_connectable;
		}
	}
	
	public static class DownstreamConnection extends Connection
	{
		public DownstreamConnection(Connectable c, int i)
		{
			super(c, i);
		}
		
		@Override
		public Node getObject()
		{
			return (Node) m_connectable;
		}
	}
	
	protected static Part head(Part p)
	{
		if (p instanceof CompositePart)
		{
			return ((CompositePart) p).head();
		}
		return p;
	}
	
	protected static Part tail(Part p)
	{
		if (p instanceof CompositePart)
		{
			return ((CompositePart) p).tail();
		}
		return null;
	}
}
