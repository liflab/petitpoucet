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
 * A node that contains an object, called its "label".
 * @author Sylvain Hallé
 */
public class LabelledNode extends Node
{
	/**
	 * An object associated to a node.
	 */
	/*@ null @*/ protected Object m_label;
	
	/**
	 * Creates a new labelled node.
	 * @param label The node's label
	 */
	public LabelledNode(/*@ null @*/ Object label)
	{
		super(1, 1);
		m_label = label;
	}
	
	/**
	 * Gets the label associated to this node.
	 * @return The label
	 */
	/*@ null @*/ public Object getLabel()
	{
		return m_label;
	}
	
	@Override
	public String toString()
	{
		if (m_label != null)
		{
			return m_label.toString();
		}
		return "null";
	}
	
	@Override
	public LabelledNode duplicate(boolean with_state)
	{
		LabelledNode ln = new LabelledNode(m_label);
		copyInto(ln, with_state);
		return ln;
	}
	
	/**
	 * Connects the first output pin of the current node to the first input pin
	 * of another node.
	 * @param n The other node
	 */
	public void addChild(Node n)
	{
		NodeConnector.connect(this, 0, n, 0);
	}
}
