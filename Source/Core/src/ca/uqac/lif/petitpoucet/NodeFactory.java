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
 * Provides lineage nodes in a local context.
 */
public class NodeFactory
{
	/*@ non_null @*/ protected Map<ObjectPart,PartNode> m_partNodes;
	
	public NodeFactory()
	{
		super();
		m_partNodes = new HashMap<ObjectPart,PartNode>();
	}
	
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
			if (o == null || !(o instanceof ObjectPart))
			{
				return false;
			}
			ObjectPart op = (ObjectPart) o;
			return m_part.equals(op.m_part) && m_subject.equals(op.m_subject);
		}
	}
}
