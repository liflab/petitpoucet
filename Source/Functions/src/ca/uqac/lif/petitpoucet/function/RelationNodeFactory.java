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

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;

/**
 * A {@link NodeFactory} that produces instances of {@link RelationNode} when
 * asked for an {@link AndNode}.
 * 
 * @author Sylvain Hallé
 */
public class RelationNodeFactory extends NodeFactory
{
	/**
	 * A single instance of the node factory.
	 */
	protected static final RelationNodeFactory s_factory = new RelationNodeFactory();
	
	/**
	 * Gets a default instance of the node factory.
	 * @return The factory
	 */
	public static RelationNodeFactory getFactory()
	{
		return s_factory;
	}
	
	@Override
	public RelationNodeFactory getFactory(Part p, Object subject)
	{
		ObjectPart op = new ObjectPart(p, subject);
		if (m_factories.containsKey(op))
		{
			return (RelationNodeFactory) m_factories.get(op);
		}
		RelationNodeFactory new_factory = new RelationNodeFactory();
		m_factories.put(op, new_factory);
		return new_factory;
	}
	
	@Override
	public RelationNode getAndNode()
	{
		return getAndNode(null);
	}
	
	/**
	 * Gets an instance of "and" node specifying the relation between the
	 * children parts.
	 * @param relation The relation
	 * @return The node instance
	 */
	public RelationNode getAndNode(Function relation)
	{
		return new RelationNode(relation);
	}
}
