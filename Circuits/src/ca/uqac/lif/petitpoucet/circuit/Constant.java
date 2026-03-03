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

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

public class Constant extends Node
{
	protected final Object m_value;

	public Constant(Object o)
	{
		super(1, 1);
		m_value = o;
	}

	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		output[0] = m_value;
	}

	@Override
	public Vertex explain(Part p, VertexFactory f) throws ExplanationException
	{
		Part head = head(p);
		if (head instanceof OutputPart && ((OutputPart) head).getIndex() == 0)
		{
			return f.getPart(new ConstantValue(), m_value);
		}
		throw new ExplanationException("Expected output part 0");
	}
	
	public class ConstantValue implements Part
	{
		public Object getValue()
		{
			return m_value;
		}
		
		@Override
		public Part duplicate(boolean with_state)
		{
			return this;
		}
		
		@Override
		public String toString()
		{
			return m_value.toString();
		}
		
		@Override
		public int hashCode()
		{
			return m_value.hashCode();
		}
		
		public boolean equals(Object o)
		{
			return o instanceof ConstantValue && ((ConstantValue) o).getValue() == m_value;
		}
	}
}
