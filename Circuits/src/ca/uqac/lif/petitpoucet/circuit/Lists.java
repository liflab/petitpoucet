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

import java.util.List;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.CompositePart;

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
			Part p = CompositePart.compose(tail, new CompositePart(new NthElement(m_index), new Connectable.InputPart(0)));
			return f.getPart(p, this);
		}
		
		@Override
		public String toString()
		{
			return "\u2208@(" + m_index + ")";
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
