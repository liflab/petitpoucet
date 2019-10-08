/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.ObjectNode;

public class ConcreteObjectNode extends ConcreteTraceabilityNode implements ObjectNode
{
	/**
	 * The designated object
	 */
	protected DesignatedObject m_object;

	/**
	 * Creates a new traceability node with no children
	 * 
	 * @param dob
	 *          The designated object represented by this node
	 */
	public ConcreteObjectNode(DesignatedObject dob)
	{
		super();
		m_object = dob;
	}

	/**
	 * Gets the designated object contained in this node
	 * 
	 * @return The designated object
	 */
	@Override
	public DesignatedObject getDesignatedObject()
	{
		return m_object;
	}

	@Override
	public String toString()
	{
		return toString("");
	}
	
	@Override
	protected String toString(String indent)
	{
		StringBuilder out = new StringBuilder();
		out.append(indent).append(m_object.toString()).append("\n");
		for (LabeledEdge le : m_children)
		{
			out.append(indent).append(le.getQuality());
			out.append(((ConcreteTraceabilityNode) le.getNode()).toString(indent + " "));
		}
		return out.toString();
	}
}
