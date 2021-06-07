/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.dag;

/**
 * An object associated to a node, and that can be the incoming or the
 * outgoing extremity of a connection between nodes.
 *
 * @param <T> The type of node the pin is attached to
 * 
 * @author Sylvain Hallé
 */
public class Pin<T extends Connectable>
{
	/**
	 * The node this pin is attached to.
	 */
	/*@ non_null @*/ protected final T m_node;
	
	/**
	 * The index of the pin on that node.
	 */
	protected final int m_index;
	
	/**
	 * Creates a new pin.
	 * @param node The node this pin is attached to
	 * @param index The index of the pin on that node
	 */
	public Pin(/*@ non_null @*/ T node, int index)
	{
		super();
		m_node = node;
		m_index = index;
	}
	
	/**
	 * Gets the index of the pin on the node this pin is attached to.
	 * @return The index
	 */
	/*@ pure @*/ public int getIndex()
	{
		return m_index;
	}
	
	/**
	 * Gets the node this pin is attached to.
	 * @return The node
	 */
	/*@ pure non_null @*/ public T getNode()
	{
		return m_node;
	}
	
	@Override
	public int hashCode()
	{
		return m_node.hashCode() * m_index;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Pin))
		{
			return false;
		}
		Pin<?> p = (Pin<?>) o;
		return m_index == p.m_index && m_node.equals(p.m_node);
	}
	
	@Override
	public String toString()
	{
		return m_node + "#" + m_index;
	}
}
