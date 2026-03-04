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

public interface Connectable
{
	public int getInputArity();
	
	public int getOutputArity();
	
	public Connection getUpstream(int index);
	
	public Connection getDownstream(int index);
	
	public void assignInput(int i, Connectable c, int j);
	
	public void assignOutput(int i, Connectable c, int j);
	
	public static abstract class Connection
	{
		protected final Connectable m_connectable;
		
		protected final int m_index;
		
		public Connection(Connectable c, int i)
		{
			super();
			m_connectable = c;
			m_index = i;
		}
		
		public Connectable getObject()
		{
			return m_connectable;
		}
		
		public int getIndex()
		{
			return m_index;
		}
	}
	
	public static void connect(Connectable c1, int i1, Connectable c2, int i2)
	{
		c1.assignOutput(i1, c2, i2);
		c2.assignInput(i2, c1, i1);
	}
	
	public static class InputPart implements Part
	{
		protected final int m_index;
		
		public InputPart(int index)
		{
			super();
			m_index = index;
		}
		
		public int getIndex()
		{
			return m_index;
		}
		
		@Override
		public int hashCode()
		{
			return m_index;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof InputPart))
			{
				return false;
			}
			return m_index == ((InputPart) o).m_index;
		}
		
		@Override
		public String toString()
		{
			return "i" + m_index;
		}
		
		@Override
		public InputPart duplicate(boolean with_state)
		{
			// Object is immutable, so the same instance is OK
			return this;
		}
	}
	
	public static class OutputPart implements Part
	{
		protected final int m_index;
		
		public OutputPart(int index)
		{
			super();
			m_index = index;
		}
		
		public int getIndex()
		{
			return m_index;
		}
		
		@Override
		public int hashCode()
		{
			return m_index;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof OutputPart))
			{
				return false;
			}
			return m_index == ((OutputPart) o).m_index;
		}
		
		@Override
		public String toString()
		{
			return "o" + m_index;
		}
		
		@Override
		public OutputPart duplicate(boolean with_state)
		{
			// Object is immutable, so the same instance is OK
			return this;
		}
	}
}
