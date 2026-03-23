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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.circuit.Connectable.Connection;
import ca.uqac.lif.petitpoucet.circuit.Connectable.DownstreamConnection;
import ca.uqac.lif.petitpoucet.circuit.Connectable.UpstreamConnection;

/**
 * Represents a specific port of some component. 
 */
public abstract class ComputableConnection implements Connection
{
	/**
	 * The component designated by this connection.
	 */
	protected final Computable m_connectable;
	
	/**
	 * The index of the port on that component.
	 */
	protected final int m_index;
	
	/**
	 * Creates a new connection object.
	 * @param c The component designated by this connection
	 * @param i The index of the port on that component
	 */
	public ComputableConnection(Computable c, int i)
	{
		super();
		m_connectable = c;
		m_index = i;
	}
	
	/**
	 * Gets the component designated by this connection.
	 * @return The component
	 */
	public Computable getObject()
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
	
	/**
	 * A connection to an downstream node.
	 */
	public static class ComputableDownstreamConnection extends ComputableConnection implements DownstreamConnection
	{
		/**
		 * Creates a new downstream connection to the given node and index.
		 * @param c The node to connect to
		 * @param i The index of the input of the node to connect to
		 */
		public ComputableDownstreamConnection(Computable c, int i)
		{
			super(c, i);
		}

		@Override
		public Computable getObject()
		{
			return m_connectable;
		}

		@Override
		public int hashCode()
		{
			return m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof ComputableDownstreamConnection &&
					((ComputableDownstreamConnection) o).getObject().equals(m_connectable) &&
					((ComputableDownstreamConnection) o).getIndex() == m_index;
		}

		@Override
		public String toString()
		{
			return m_connectable.toString() + m_index + "\u2192";
		}
	}
	
	/**
	 * A connection to an upstream node.
	 */
	public static class ComputableUpstreamConnection extends ComputableConnection implements UpstreamConnection
	{
		/**
		 * Creates a new upstream connection to the given node and index.
		 * @param c The node to connect to
		 * @param i The index of the output of the node to connect to
		 */
		public ComputableUpstreamConnection(Computable c, int i)
		{
			super(c, i);
		}

		@Override
		public Computable getObject()
		{
			return m_connectable;
		}

		@Override
		public int hashCode()
		{
			return m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof ComputableUpstreamConnection &&
					((ComputableUpstreamConnection) o).getObject().equals(m_connectable) &&
					((ComputableUpstreamConnection) o).getIndex() == m_index;
		}

		@Override
		public String toString()
		{
			return "\u2192" + m_index + m_connectable.toString();
		}
	}


}

