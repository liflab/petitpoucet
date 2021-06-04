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

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.dag.Renderer;

public class LineageDotRenderer implements Renderer
{
	/*@ non_null @*/ protected Node m_root;
	
	/*@ non_null @*/ protected Map<Node,Integer> m_nodeIds;
	
	/*@ non_null @*/ protected Set<Node> m_rendered;
	
	protected int m_idCounter;
	
	public LineageDotRenderer(/*@ non_null @*/ Node root)
	{
		super();
		m_root = root;
		m_nodeIds = new HashMap<Node,Integer>();
		m_rendered = new HashSet<Node>();
		m_idCounter = 0;
	}
	
	@Override
	public void render(PrintStream ps)
	{
		ps.println("digraph G {");
		render(ps, m_root);
		ps.println("}");
	}
	
	protected void render(PrintStream ps, Node current)
	{
		int n_id = -1;
		if (!m_nodeIds.containsKey(current))
		{
			n_id = m_idCounter++;
			m_nodeIds.put(current, n_id);
			renderNode(ps, current, n_id);
		}
		else
		{
			n_id = m_nodeIds.get(current);
		}
		if (m_rendered.contains(current))
		{
			return;
		}
		m_rendered.add(current);
		for (int i = 0; i < current.getOutputArity(); i++)
		{
			Collection<Pin<? extends Node>> pins = current.getOutputLinks(i);
			for (Pin<?> pin : pins)
			{
				Node o_n = (Node) pin.getNode();
				int o_id = getNodeId(o_n);
				render(ps, o_n);
				ps.println(n_id + " -> " + o_id + ";");
			}
		}
	}
	
	protected void renderNode(PrintStream ps, Node current, int n_id)
	{
		if (m_rendered.contains(current))
		{
			return;
		}
		if (current instanceof OrNode)
		{
			ps.println(n_id + " [shape=\"circle\",label=\"&lor;\"];");
		}
		else if (current instanceof AndNode)
		{
			ps.println(n_id + " [shape=\"circle\",label=\"&land;\"];");
		}
		else if (current instanceof PartNode)
		{
			renderPartNode(ps, (PartNode) current, n_id);
		}
		else if (current instanceof NestedNode)
		{
			// TODO
		}
		else
		{
			ps.println(n_id + " [label=\"?\"];");
		}
	}
	
	protected void renderPartNode(PrintStream ps, PartNode current, int n_id)
	{
		Part d = current.getPart();
		Object o = current.getSubject();
		ps.println(n_id + "[label=\"" + d.toString() + " of " + o.toString() + "\"];");
	}
	
	protected int getNodeId(Node current)
	{
		int n_id = -1;
		if (!m_nodeIds.containsKey(current))
		{
			n_id = m_idCounter++;
			m_nodeIds.put(current, n_id);
		}
		else
		{
			n_id = m_nodeIds.get(current);
		}
		return n_id;
	}

}
