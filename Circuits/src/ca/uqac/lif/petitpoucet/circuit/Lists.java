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
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.LazyVertex;
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
		protected Vertex explain(int out_index, Part tail, VertexFactory f) throws ExplanationException
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
	public static class Apply extends Node
	{
		/**
		 * The function to apply.
		 */
		/*@ non_null @*/ protected final Node m_f;

		/**
		 * The explanations for each element of the list.
		 */
		/*@ non_null @*/ protected final List<AbstractVertex> m_explanations;

		/**
		 * Creates a new instance of the function.
		 * @param f The function to apply to each element of the list
		 */
		public Apply(/*@ non_null @*/ Node f)
		{
			super(1, 1);
			if (f.getInputArity() != 1)
			{
				throw new IllegalArgumentException("Function must have an arity of 1");
			}
			if (f.getOutputArity() != 1)
			{
				throw new IllegalArgumentException("Function must have an arity of 1");
			}
			m_f = f;
			m_explanations = new ArrayList<>();
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
		protected AbstractVertex explain(int out_index, Part tail, VertexFactory f) throws ExplanationException
		{
			return new ApplyLazyVertex(f, tail);
		}

		@Override
		public String toString()
		{
			return "\u03b1";
		}

		protected class ApplyLazyVertex extends LazyVertex
		{
			public ApplyLazyVertex(VertexFactory f, Part p)
			{
				super(f, p);
			}

			@Override
			public Vertex concretize(Part p) throws ExplanationException
			{
				Part t_head = head(p);
				if (t_head instanceof NthElement)
				{
					return explainElement(((NthElement) t_head).getIndex(), tail(p));
				}
				return AbstractVertex.get(Apply.super.explain(0, p, m_factory));
			}

			/**
			 * Computes the explanation for a specific element of the output list.
			 * @param index The position of the element in the list
			 * @param tail The tail part of the explanation
			 * @param f The factory used to create vertices for this explanation
			 * @return The root vertex of the explanation graph
			 * @throws ExplanationException Thrown if an error occurred during the
			 * calculation of the explanation
			 */
			/*@ non_null @*/ protected Vertex explainElement(int index, Part new_p) throws ExplanationException
			{
				Vertex inner;
				AbstractVertex in_e = m_explanations.get(index);
				if (in_e instanceof LazyVertex)
				{
					((LazyVertex) in_e).concretize(new_p);
					inner = ((LazyVertex) in_e).subgraph();
				}
				else
				{
					inner = (Vertex) in_e;
				}
				List<Vertex> children;
				Subgraph add_to = null;
				if (inner instanceof Subgraph)
				{
					children = ((Subgraph) inner).findLeaves();
					add_to = (Subgraph) inner;
				}
				else
				{
					children = inner.getChildren();
				}
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
					InputPart op = (InputPart) p_head;
					if (op.getIndex() != 0)
					{
						throw new ExplanationException("Expected input 0");
					}
					Part new_part = compose(tail(p), new NthElement(index), new InputPart(0));
					if (add_to == null)
					{
						child.addChild(m_factory.getPart(new_part, Apply.this));
					}
					else
					{
						add_to.addChild(m_factory.getPart(new_part, Apply.this), i);
					}
				}
				Vertex root = m_factory.getPart(compose(new_p, new OutputPart(0)), m_f);
				if (inner instanceof Subgraph)
				{
					((Subgraph) inner).pushRoot(root);
					return inner;
				}
				root.addChild(inner);
				return root;
			}
		}

		public Application getApplication(int index)
		{
			return new Application(index);
		}

		public class Application
		{
			protected final int m_index;

			protected Application(int index)
			{
				super();
				m_index = index;
			}

			public Node getNode()
			{
				return Apply.this;
			}

			public int getIndex()
			{
				return m_index;
			}

			@Override
			public int hashCode()
			{
				return m_index + Apply.this.hashCode();
			}

			@Override
			public boolean equals(Object o)
			{
				if (!(o instanceof Application))
				{
					return false;
				}
				Application a = (Application) o;
				return a.getIndex() == m_index && a.getNode() == getNode();
			}

			@Override
			public String toString()
			{
				return Application.this.toString() + "@" + m_index;
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
