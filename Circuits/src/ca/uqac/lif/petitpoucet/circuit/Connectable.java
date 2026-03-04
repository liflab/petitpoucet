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

/**
 * Interface implemented by objects having input and output "ports" that
 * can be connected to each other.
 * @author Sylvain Hallé
 */
public interface Connectable
{
	/**
	 * Gets the input arity of the component, i.e.<!-- --> its number of input
	 * ports.
	 * @return The arity
	 */
	public int getInputArity();
	
	/**
	 * Gets the output arity of the component, i.e.<!-- --> its number of output
	 * ports.
	 * @return The arity
	 */
	public int getOutputArity();
	
	/**
	 * Retrieves the component connected to a given input port of the object.
	 * @param index The index of the input port
	 * @return A {@link Connection} object, or {@code null} if the port is
	 * not connected
	 */
	public Connection getUpstream(int index);
	
	/**
	 * Retrieves the component connected to a given output port of the object.
	 * @param index The index of the output port
	 * @return A {@link Connection} object, or {@code null} if the port is
	 * not connected
	 */
	public Connection getDownstream(int index);
	
	/**
	 * Connects an output port of a component to an input port of the current
	 * object. 
	 * @param i The index of the input port
	 * @param c The component to connect
	 * @param j The index of that component's output port
	 */
	public void assignInput(int i, Connectable c, int j);
	
	/**
	 * Connects an input port of a component to an output port of the current
	 * object. 
	 * @param i The index of the output port
	 * @param c The component to connect
	 * @param j The index of that component's input port
	 */
	public void assignOutput(int i, Connectable c, int j);
	
	/**
	 * Represents a specific port of some component. 
	 */
	public static abstract class Connection
	{
		/**
		 * The component designated by this connection.
		 */
		protected final Connectable m_connectable;
		
		/**
		 * The index of the port on that component.
		 */
		protected final int m_index;
		
		/**
		 * Creates a new connection object.
		 * @param c The component designated by this connection
		 * @param i The index of the port on that component
		 */
		public Connection(Connectable c, int i)
		{
			super();
			m_connectable = c;
			m_index = i;
		}
		
		/**
		 * Gets the component designated by this connection.
		 * @return The component
		 */
		public Connectable getObject()
		{
			return m_connectable;
		}
		
		/**
		 * Gets the index of the port on the component.
		 * @return The index
		 */
		public int getIndex()
		{
			return m_index;
		}
	}
	
	/**
	 * Connects an "upstream" component to a "downstream" component.
	 * @param c1 The upstream component
	 * @param i1 The index of that component's output port
	 * @param c2 The downstream component
	 * @param i2 The index of that component's input port
	 */
	public static void connect(Connectable c1, int i1, Connectable c2, int i2)
	{
		c1.assignOutput(i1, c2, i2);
		c2.assignInput(i2, c1, i1);
	}
	
	/**
	 * A {@link Part} designating a specific input port of the current object.
	 */
	public static class InputPart implements Part
	{
		/**
		 * The index of the input port.
		 */
		protected final int m_index;
		
		/**
		 * Creates a new instance of the part.
		 * @param index The index of the input port
		 */
		public InputPart(int index)
		{
			super();
			m_index = index;
		}
		
		/**
		 * Gets the index of the input port.
		 * @return The index
		 */
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
	
	/**
	 * A {@link Part} designating a specific output port of the current object.
	 */
	public static class OutputPart implements Part
	{
		/**
		 * The index of the output port.
		 */
		protected final int m_index;
		
		/**
		 * Creates a new instance of the part.
		 * @param index The index of the output port
		 */
		public OutputPart(int index)
		{
			super();
			m_index = index;
		}
		
		/**
		 * Gets the index of the output port.
		 * @return The index
		 */
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
