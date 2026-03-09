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
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Connectable.InputPart;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.LazyVertex;

/**
 * Utility class providing basic operations on lists.
 * @author Sylvain Hallé
 */
public abstract class Lists
{
	protected static abstract class LazyAllElementsVertex extends LazyVertex
	{
		public LazyAllElementsVertex(VertexFactory f, Part p, int options)
		{
			super(f, p, options);
		}

		@Override
		public Vertex concretize(Part p, int options) throws ExplanationException
		{
			if (getArity() == 1)
			{
				return m_factory.getPart(compose(new NthElement(0), InputPart.FIRST), getInstance());
			}
			AndVertex a = m_factory.getAnd();
			for (int i = 0; i < getArity(); i++)
			{
				a.addChild(m_factory.getPart(compose(new NthElement(i), InputPart.FIRST), getInstance()));
			}
			return a;
		}

		protected abstract Node getInstance();

		protected abstract int getArity();

	}

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

	public static class SumAll extends Node
	{
		public SumAll()
		{
			super(1, 1);
		}

		@Override
		public SumAll duplicate(boolean with_state)
		{
			return new SumAll();
		}

		@Override
		public AbstractVertex explain(int index, Part p, VertexFactory f, int options)
		{
			return new LazySumAllVertex(f, p, options);
		}

		protected class LazySumAllVertex extends LazyAllElementsVertex
		{
			public LazySumAllVertex(VertexFactory f, Part p, int options)
			{
				super(f, p, options);
			}

			@Override
			protected Node getInstance()
			{
				return SumAll.this;
			}

			@Override
			protected int getArity()
			{
				return getInputArity();
			}
		}

		@Override
		protected void evaluate(Object[] input, Object[] output)
		{
			List<?> ins = (List<?>) input[0];
			float t = 0;
			for (Object o : ins)
			{
				if (!(o instanceof Number))
				{
					throw new IllegalArgumentException("Expected a number");
				}
				t += ((Number) o).floatValue();
			}
			output[0] = t;
		}
	}

	public static class Window extends ParameterizedNode
	{
		/**
		 * The width on which to apply the window
		 */
		protected final int m_width;

		/**
		 * Creates a new instance of the function.
		 * @param width The width of the window
		 * @param f The function to apply on each window
		 */
		public Window(int width, Circuit f)
		{
			super(1, 1, f);
			m_width = width;
		}

		@Override
		public Window duplicate(boolean with_state)
		{
			return new Window(m_width, m_f);
		}

		@Override
		protected void evaluate(Object[] input, Object[] output)
		{
			List<?> in_list = (List<?>) input[0];
			List<Object> out_list = new ArrayList<>(in_list.size() - m_width + 1);
			for (int i = 0; i <= in_list.size() - m_width; i++)
			{
				Object[] window = new Object[m_width];
				for (int j = 0; j < m_width; j++)
				{
					window[j] = in_list.get(i + j);
				}
				Object[] out = new Object[1];
				register(out, window);
				out_list.add(out[0]);
			}
			output[0] = out_list;
		}

		@Override
		protected AbstractVertex explain(int out_index, Part tail, VertexFactory f, int options) throws ExplanationException
		{
			return new WindowLazyVertex(f, tail, options);
		}

		@Override
		public String toString()
		{
			return "\u03c9";
		}

		protected class WindowLazyVertex extends ParameterLazyVertex
		{ 
			public WindowLazyVertex(VertexFactory f, Part p, int options)
			{
				super(f, p, options);
			}

			@Override
			public Node getInstance()
			{
				return Window.this;
			}

			@Override
			public Vertex concretize(Part p, int options) throws ExplanationException
			{
				Part t_head = head(p);
				if (t_head instanceof NthElement)
				{
					return explainElement(((NthElement) t_head).getIndex(), tail(p));
				}
				return AbstractVertex.get(Window.super.explain(0, p, m_factory, options));
			}

			@Override
			protected Vertex extendLeaves(Part new_p, int index, List<Vertex> children, Subgraph inner)
			{
				for (int i = 0; i < children.size(); i++)
				{
					Vertex child = children.get(i);
					if (!(child instanceof PartVertex))
					{
						continue;
					}
					PartVertex pv = (PartVertex) child;
					Part p = pv.getPart();
					Part p_head = head(p);
					if (!(p_head instanceof InputPart))
					{
						continue;
					}
					Part new_part = compose(tail(p), new NthElement(index + i), InputPart.FIRST);
					if (inner == null)
					{
						child.addChild(m_factory.getPart(new_part, getInstance()));
					}
					else
					{
						inner.addChild(m_factory.getPart(new_part, getInstance()), child);
					}
				}
				return inner;
			}
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
		public Apply(/*@ non_null @*/ Circuit f)
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
				Object[] out = new Object[1];
				register(out, new Object[] {o});
				out_list.add(out[0]);
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
			public Vertex concretize(Part p, int options) throws ExplanationException
			{
				Part t_head = head(p);
				if (t_head instanceof NthElement)
				{
					Vertex root = m_factory.getPart(CompositePart.compose(p, OutputPart.FIRST), Apply.this);
					Vertex child = explainElement(((NthElement) t_head).getIndex(), tail(p));
					root.addChild(child);
					return root;
				}
				return AbstractVertex.get(Apply.super.explain(0, p, m_factory, options));
			}

			@Override
			protected Vertex extendLeaves(Part new_p, int index, List<Vertex> children, Subgraph inner)
			{
				for (int i = 0; i < children.size(); i++)
				{
					Vertex child = children.get(i);
					if (!(child instanceof PartVertex))
					{
						continue;
					}
					PartVertex pv = (PartVertex) child;
					Part p = pv.getPart();
					Part p_head = head(p);
					if (!(p_head instanceof InputPart))
					{
						continue;
					}
					Part new_part = compose(tail(p), new NthElement(index), InputPart.FIRST);
					if (inner == null)
					{
						child.addChild(m_factory.getPart(new_part, Apply.this));
					}
					else
					{
						inner.addChild(m_factory.getPart(new_part, Apply.this), child);
					}
				}
				return inner;
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
