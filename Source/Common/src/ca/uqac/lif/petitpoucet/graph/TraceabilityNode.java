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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;

public class TraceabilityNode
{
	/**
	 * The designated object
	 */
	protected DesignatedObject m_object;
	
	/**
	 * The links between this object and others
	 */
	protected List<QualityLink> m_links;
	
	/**
	 * A unique ID for the node
	 */
	protected int m_id;
	
	/**
	 * A counter for unique IDs
	 */
	private static int s_idCounter = 0; 
	
	/**
	 * Creates a new traceability node with no children
	 * @param dob The designated object represented by this node
	 */
	public TraceabilityNode(DesignatedObject dob)
	{
		super();
		m_id = s_idCounter++;
		m_object = dob;
		m_links = new ArrayList<QualityLink>();
	}
	
	/**
	 * Gets the node's unique ID
	 * @return The ID
	 */
	public int getId()
	{
	  return m_id;
	}
	
	/**
	 * Gets the children of this node
	 * @return The list of children
	 */
	public List<QualityLink> getChildren()
	{
		return m_links;
	}
	
	/**
	 * Adds children to this node
	 * @param links The list of children
	 */
	public void addChildren(List<QualityLink> links)
	{
		m_links.addAll(links);
	}
	
	/**
	 * Adds children to this node
	 * @param links The list of children
	 */
	public void addChildren(QualityLink ... links)
	{
		for (QualityLink node : links)
		{
			m_links.add(node);
		}
	}
	
	public static class QualityLink
	{
		protected TraceabilityNode m_node;
		
		protected Quality m_quality;
		
		public QualityLink(TraceabilityNode node, Quality q)
		{
			super();
			m_node = node;
			m_quality = q;
		}
	}
}
