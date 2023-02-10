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
package ca.uqac.lif.petitpoucet;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides lineage nodes in a local context. Objects that implement a
 * descendant of the {@link Queryable} interface should use a node factory to
 * create nodes, and not call their constructors directly.
 * @author Sylvain Hallé
 */
public class NodeFactory
{
	/**
	 * A map associating object/part pairs to instances of {@link PartNode}. This
	 * map is used to return the same node if the same object/part is requested
	 * multiple times.
	 */
	/*@ non_null @*/ protected Map<ObjectPart,PartNode> m_partNodes;
	
	/**
	 * A map associating part nodes to child factory instances.
	 */
	/*@ null @*/ protected Map<ObjectPart,NodeFactory> m_factories;
	
	/**
	 * Gets a new empty instance of a node factory.
	 * @return The factory instance
	 */
	public static NodeFactory getFactory()
	{
		return new NodeFactory();
	}
	
	/**
	 * Creates a new node factory.
	 */
	protected NodeFactory()
	{
		super();
		m_partNodes = new HashMap<>();
		m_factories = new HashMap<>();
	}
	
	/**
	 * Gets a derived factory instance to generate nodes pertaining to a
	 * specific pair of part and object.
	 * @param p The part
	 * @param subject The object
	 * @return The factory instance
	 */
	/*@ non_null @*/ public NodeFactory getFactory(Part p, Object subject)
	{
		ObjectPart op = new ObjectPart(p, subject);
		if (m_factories.containsKey(op))
		{
			return m_factories.get(op);
		}
		NodeFactory new_factory = new NodeFactory();
		m_factories.put(op, new_factory);
		return new_factory;
	}
	
	/**
	 * Gets an instance of {@link PartNode} corresponding to a particular part
	 * and subject. If the same part and subject are requested multiple times, the
	 * factory reuses the same node instance on each successive call.
	 * @param p The part
	 * @param subject The subject
	 * @return The node instance
	 */
	public PartNode getPartNode(Part p, Object subject)
	{
		ObjectPart op = new ObjectPart(p, subject);
		if (m_partNodes.containsKey(op))
		{
			return m_partNodes.get(op);
		}
		PartNode pn = new PartNode(p, subject);
		m_partNodes.put(op, pn);
		return pn;
	}
	
	/**
	 * Gets an instance of {@link AndNode}. A new instance is created on every
	 * call to this method. The factory keeps no track of this object after it
	 * is returned.
	 * @return The and node
	 */
	public AndNode getAndNode()
	{
		return new AndNode();
	}
	
	/**
	 * Gets an instance of {@link OrNode}. A new instance is created on every
	 * call to this method. The factory keeps no track of this object after it
	 * is returned.
	 * @return The or node
	 */
	public OrNode getOrNode()
	{
		return new OrNode();
	}
	
	/**
	 * Gets an instance of {@link UnknownNode}. A new instance is created on every
	 * call to this method. The factory keeps no track of this object after it
	 * is returned.
	 * @return The unknown node
	 */
	public UnknownNode getUnknownNode()
	{
		return new UnknownNode();
	}
	
	/**
	 * Asks the factory whether a node for a given part of an object already
	 * exists.
	 * @param p The part
	 * @param o The object
	 * @return <tt>true</tt> if a node already exists, <tt>false</tt> otherwise
	 */
	public boolean hasNodeFor(Part p, Object o)
	{
		return m_partNodes.containsKey(new ObjectPart(p, o));
	}
	
	/**
	 * Class that uniquely defines an object part and a subject. Instances of
	 * this class are used as keys by the node factory to uniquely identify
	 * {@link PartNode}s.
	 */
	protected static class ObjectPart
	{
		/**
		 * The object part.
		 */
		/*@ non_null @*/ protected Part m_part;
		
		/**
		 * The object.
		 */
		/*@ non_null @*/ protected Object m_subject;
		
		/**
		 * Creates a new object part.
		 * @param p The object part
		 * @param subject The object
		 */
		public ObjectPart(/*@ non_null @*/ Part p, /*@ non_null @*/ Object subject)
		{
			super();
			m_part = p;
			m_subject = subject;
		}
		
		@Override
		public int hashCode()
		{
			return m_part.hashCode() + m_subject.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof ObjectPart))
			{
				return false;
			}
			ObjectPart op = (ObjectPart) o;
			return m_part.equals(op.m_part) && m_subject.equals(op.m_subject);
		}
		
		@Override
		public String toString()
		{
			return m_part + " of " + m_subject;
		}
	}
}
