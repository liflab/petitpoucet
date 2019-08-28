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
	 * Creates a new traceability node with no children
	 * @param dob The designated object represented by this node
	 */
	public TraceabilityNode(DesignatedObject dob)
	{
		super();
		m_object = dob;
		m_links = new ArrayList<QualityLink>();
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
