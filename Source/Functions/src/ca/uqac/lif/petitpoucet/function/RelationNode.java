/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.AndNode;

/**
 * An {@link AndNode} carrying extra information about the relation between
 * its children nodes. This relation takes the form of a function, and the
 * children of the node are the arguments of that function.
 *  
 * @author Sylvain Hallé
 */
public class RelationNode extends AndNode
{
	/**
	 * The relation uniting the parts under this and node.
	 */
	/*@ null @*/ protected Function m_relation;
	
	/**
	 * Creates a new relation node with no specified relation between the
	 * children.
	 */
	protected RelationNode()
	{
		this(null);
	}
	
	/**
	 * Creates a new relation node.
	 * @param relation The relation uniting the parts under this node
	 */
	protected RelationNode(/*@ null @*/ Function relation)
	{
		super();
		m_relation = relation;
	}
	
	/**
	 * Gets the relation uniting the parts under this node.
	 * @return The relation
	 */
	/*@ pure null @*/ public Function getRelation()
	{
		return m_relation;
	}
	
	@Override
	public RelationNode duplicate(boolean with_state)
	{
		RelationNode n = new RelationNode();
		copyInto(n, with_state);
		return n;
	}
	
	/**
	 * Copies the content of the current node into another node
	 * @param n The node to copy the content into
	 * @param with_state Set to <tt>true</tt> for a stateful copy, <tt>false</tt>
	 * otherwise
	 */
	protected void copyInto(RelationNode n, boolean with_state)
	{
		super.copyInto(n, with_state);
		m_relation = n.getRelation();
	}
	
	@Override
	public String toString()
	{
		if (m_relation == null)
		{
			return super.toString();
		}
		return m_relation.toString();
	}
}
