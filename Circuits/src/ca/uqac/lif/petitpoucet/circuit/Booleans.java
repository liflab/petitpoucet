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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

/**
 * Utility class providing basic logical operations.
 * @author Sylvain Hallé
 */
public abstract class Booleans extends Node
{
	/**
	 * Creates a new instance of the class.
	 * @param in_arity The input arity; the output arity is assumed to be 1
	 */
	public Booleans(int in_arity)
	{
		super(in_arity, 1);
	}
	
	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		boolean[] arguments = new boolean[input.length];
		for (int i = 0; i < arguments.length; i++)
		{
			Object o = input[i];
			if (!(o instanceof Boolean))
			{
				throw new IllegalArgumentException("Expected a boolean");
			}
			arguments[i] = (Boolean) o;
		}
		output[0] = getValue(arguments);
	}
	
	protected abstract boolean getValue(boolean[] arguments);
	
	protected abstract static class IndexLazyVertex extends LazyVertex
	{
		/*@ non_null @*/ protected final int[] m_indices;
		
		public IndexLazyVertex(VertexFactory f, Part p, List<Integer> indices, int options)
		{
			super(f, p, options);
			m_indices = new int[indices.size()];
			for (int i = 0; i < indices.size(); i++)
			{
				m_indices[i] = indices.get(i);
			}
		}
	}
	
	/**
	 * A Boolean AND operator.
	 */
	public static class And extends Booleans
	{
		/**
		 * The list of inputs that are false, if any. If null, it means all inputs
		 * are true.
		 */
		/*@ null @*/ protected List<Integer> m_falseInputs;
		
		/**
		 * Creates a new instance of the class.
		 * @param in_arity The input arity; the output arity is assumed to be 1
		 */
		public And(int in_arity)
		{
			super(in_arity);
			m_falseInputs = null;
		}

		@Override
		public And duplicate(boolean with_state)
		{
			return new And(getInputArity());
		}

		@Override
		protected boolean getValue(boolean[] arguments)
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (!arguments[i])
				{
					if (m_falseInputs == null)
					{
						m_falseInputs = new ArrayList<>();
					}
					m_falseInputs.add(i);
				}
			}
			return m_falseInputs == null;
		}
		
		@Override
		public AbstractVertex explain(Part p, VertexFactory f, int options) throws ExplanationException
		{
			checkHead(p);
			if (m_falseInputs != null)
			{
				return new AndFalseLazyVertex(f, p, m_falseInputs, options);
			}
			return super.explain(p, f, options);
		}
		
		@Override
		public void reset()
		{
			super.reset();
			m_falseInputs = null;
		}
		
		@Override
		public String toString()
		{
			return "\u2227";
		}
		
		protected class AndFalseLazyVertex extends IndexLazyVertex
		{
			public AndFalseLazyVertex(VertexFactory f, Part p, List<Integer> indices, int options)
			{
				super(f, p, indices, options);
			}

			@Override
			public Vertex concretize(Part p) throws ExplanationException
			{
				if (m_indices.length == 1 || Explainable.shouldCut(m_options))
				{
					return m_factory.getPart(new InputPart(m_indices[0]), And.this);
				}
				Vertex o = m_factory.getOr();
				for (int z : m_indices)
				{
					o.addChild(m_factory.getPart(new InputPart(z), And.this));
				}
				return o;
			}
		}
	}
	
	/**
	 * A Boolean OR operator.
	 */
	public static class Or extends Booleans
	{
		/**
		 * The list of inputs that are true, if any. If null, it means all inputs
		 * are false.
		 */
		/*@ null @*/ protected List<Integer> m_trueInputs;
		
		/**
		 * Creates a new instance of the class.
		 * @param in_arity The input arity; the output arity is assumed to be 1
		 */
		public Or(int in_arity)
		{
			super(in_arity);
			m_trueInputs = null;
		}

		@Override
		public Or duplicate(boolean with_state)
		{
			return new Or(getInputArity());
		}

		@Override
		protected boolean getValue(boolean[] arguments)
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (arguments[i])
				{
					if (m_trueInputs == null)
					{
						m_trueInputs = new ArrayList<>();
					}
					m_trueInputs.add(i);
				}
			}
			return m_trueInputs != null;
		}
		
		@Override
		public AbstractVertex explain(Part p, VertexFactory f, int options) throws ExplanationException
		{
			checkHead(p);
			if (m_trueInputs != null)
			{
				return new OrTrueLazyVertex(f, p, m_trueInputs, options);
			}
			return super.explain(p, f, options);
		}
		
		@Override
		public void reset()
		{
			super.reset();
			m_trueInputs = null;
		}
		
		@Override
		public String toString()
		{
			return "\u2228";
		}
		
		protected class OrTrueLazyVertex extends IndexLazyVertex
		{
			public OrTrueLazyVertex(VertexFactory f, Part p, List<Integer> indices, int options)
			{
				super(f, p, indices, options);
			}

			@Override
			public Vertex concretize(Part p) throws ExplanationException
			{
				if (m_indices.length == 1 || Explainable.shouldCut(m_options))
				{
					return m_factory.getPart(new InputPart(m_indices[0]), Or.this);
				}
				Vertex o = m_factory.getOr();
				for (int z : m_indices)
				{
					o.addChild(m_factory.getPart(new InputPart(z), Or.this));
				}
				return o;
			}
		}
	}
}
