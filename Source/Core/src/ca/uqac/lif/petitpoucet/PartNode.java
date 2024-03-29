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
package ca.uqac.lif.petitpoucet;

import ca.uqac.lif.dag.LabelledNode;

/**
 * Node whose label is a pair made of a part and an object. These nodes are
 * used in explanation graphs to designate the part of some object (input or
 * output of a function, etc.). 
 */
public class PartNode extends LabelledNode
{	
	/**
	 * Creates a new part node. The constructor has {@code package} visibility to
	 * force the use of a {@link NodeFactory} to obtain instances of this class.
	 * @param part The part
	 * @param subject The object this part refers to. This object can be null.
	 */
	protected PartNode(/*@ non_null @*/ Part part, /*@ null @*/ Object subject)
	{
		super(new Object[] {part, subject});
	}
	
	/**
	 * Gets the part contained in this node.
	 * @return The part
	 */
	/*@ non_null @*/ public Part getPart()
	{
		return (Part) ((Object[]) m_label)[0];
	}
	
	/**
	 * Gets the subject contained in this node.
	 * @return The subject
	 */
	/*@ null @*/ public Object getSubject()
	{
		return ((Object[]) m_label)[1];
	}
	
	/**
	 * Sets the part contained in this node.
	 * @param p The part
	 */
	public void setPart(Part p)
	{
		((Object[]) m_label)[0] = p;
	}
	
	/**
	 * Sets the subject contained in this node.
	 * @param p The part
	 */
	public void setSubject(Object p)
	{
		((Object[]) m_label)[1] = p;
	}
	
	@Override
	public String toString()
	{
		return ((Object[]) m_label)[0] + " of " + ((Object[]) m_label)[1];
	}
	
	@Override
	public PartNode duplicate(boolean with_state)
	{
		PartNode pn = new PartNode((Part) ((Object[]) m_label)[0], ((Object[]) m_label)[1]);
		copyInto(pn, with_state);
		return pn;
	}
	
	/* ---- This part is deliberately commented out. PartNodes must be considered
	 * distinct, even when they refer to the same part and the same subject.
	 * Otherwise, problems occur on operations such as flattening and copying,
	 * where a node and its copy will be mistakenly confused for one another.
	 * If you really wish to implement this behavior, the implementation of
	 * e.g. FlatteningCrawler.crawl must be adjusted accordingly.
	 
	@Override
	public int hashCode()
	{
		return getPart().hashCode() + getSubject().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof PartNode))
		{
			return false;
		}
		PartNode pn = (PartNode) o;
		return getPart().equals(pn.getPart()) && getSubject().equals(pn.getSubject());
	}
	*/
}
