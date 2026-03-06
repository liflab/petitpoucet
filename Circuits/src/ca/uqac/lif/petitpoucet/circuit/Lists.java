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

import static ca.uqac.lif.petitpoucet.CompositePart.compose;
import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Lists.NthElement;
import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;

/**
 * Utility class providing basic operations on lists.
 * @author Sylvain Hallé
 */
public abstract class Lists
{
	/**
	 * Extracts an element at a given index from a list.
	 */
	public static class ElementAt extends Node
	{
		/**
		 * The index to extract.
		 */
		protected final int m_index;

		/**
		 * Creates a new instance of the function.
		 * @param index The index to extract
		 */
		public ElementAt(int index)
		{
			super(1, 1);
			m_index = index;
		}

		@Override
		public ElementAt duplicate(boolean with_state)
		{
			return new ElementAt(m_index);
		}

		@Override
		protected void evaluate(Object[] input, Object[] output)
		{
			List<?> in = (List<?>) input[0];
			output[0] = in.get(m_index);
		}

		@Override
		protected Vertex explain(int out_index, Part tail, VertexFactory f, int options) throws ExplanationException
		{
			Part p = compose(tail, new CompositePart(new NthElement(m_index), new Connectable.InputPart(0)));
			return f.getPart(p, this);
		}

		@Override
		public String toString()
		{
			return "\u2208@(" + m_index + ")";
		}
	}

	/**
	 * Applies a function to every element of a list.
	 */
	public static class Apply extends ParameterizedNode
	{
		/**
		 * Creates a new instance of the function.
		 * @param f The function to apply to each element of the list
		 */
		public Apply(/*@ non_null @*/ Node f)
		{
			super(1, 1, f);
			if (f.getInputArity() != 1)
			{
				throw new IllegalArgumentException("Function must have an arity of 1");
			}
			if (f.getOutputArity() != 1)
			{
				throw new IllegalArgumentException("Function must have an arity of 1");
			}
		}

		@Override
		public Apply duplicate(boolean with_state)
		{
			return new Apply(m_f.duplicate(with_state));
		}

		@Override
		protected void evaluate(Object[] input, Object[] output)
		{
			List<?> in_list = (List<?>) input[0];
			List<Object> out_list = new ArrayList<>(in_list.size());
			for (Object o : in_list)
			{
				Constant c = new Constant(o);
				Connectable.connect(c, 0, m_f, 0);
				m_f.reset();
				out_list.add(m_f.compute());
				try
				{
					VertexFactory f = new VertexFactory();
					AbstractVertex e = m_f.explain(new OutputPart(0), f);
					m_explanations.add(e);
				}
				catch (ExplanationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			output[0] = out_list;
		}

		@Override
		protected AbstractVertex explain(int out_index, Part tail, VertexFactory f, int options) throws ExplanationException
		{
			return new ApplyLazyVertex(f, tail, options);
		}

		@Override
		public String toString()
		{
			return "\u03b1";
		}

		protected class ApplyLazyVertex extends ParameterLazyVertex
		{
			public ApplyLazyVertex(VertexFactory f, Part p, int options)
			{
				super(f, p, options);
			}
			
			@Override
			public Node getInstance()
			{
				return Apply.this;
			}
			
			@Override
			public Vertex concretize(Part p) throws ExplanationException
			{
				Part t_head = head(p);
				if (t_head instanceof NthElement)
				{
					return explainElement(((NthElement) t_head).getIndex(), tail(p));
				}
				return AbstractVertex.get(getInstance().explain(0, p, m_factory, m_options));
			}
		}
	}

	/**
	 * A {@link Part} designating a specific element inside a list.
	 */
	public static class NthElement implements Part
	{
		/**
		 * The index in the list.
		 */
		protected final int m_index;

		/**
		 * Creates a new instance of the part.
		 * @param index The index in the list
		 */
		public NthElement(int index)
		{
			super();
			m_index = index;
		}

		/**
		 * Gets the index in the list.
		 * @return The index
		 */
		/*@ pure @*/ public int getIndex()
		{
			return m_index;
		}

		@Override
		public Part duplicate(boolean with_state)
		{
			return this;
		}

		@Override
		public int hashCode()
		{
			return m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof NthElement && ((NthElement) o).m_index == m_index;
		}

		@Override
		public String toString()
		{
			return "#" + m_index;
		}
	}
}
