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

	/*@ non_null @*/ protected Map<Node,String> m_nodeIds;

	/*@ non_null @*/ protected Set<Node> m_rendered;
	
	/*@ non_null @*/ protected Set<Node> m_expanded;

	/*@ non_null @*/ protected String m_prefix;

	protected int m_idCounter;

	public LineageDotRenderer(/*@ non_null @*/ Node root, /*@ non_null @*/ String prefix)
	{
		super();
		m_root = root;
		m_nodeIds = new HashMap<Node,String>();
		m_rendered = new HashSet<Node>();
		m_expanded = new HashSet<Node>();
		m_idCounter = 0;
		m_prefix = prefix;
	}

	public LineageDotRenderer(/*@ non_null @*/ Node root)
	{
		this(root, "");
	}

	@Override
	public void render(PrintStream ps)
	{
		if (m_prefix.isEmpty())
		{
			ps.println("digraph G {");
			ps.println("compound=true;");
			ps.println("node [style=\"filled\",shape=\"rectangle\"]");
			render(ps, m_root);
			ps.println("}");
		}
		else
		{
			ps.println("subgraph " + m_prefix + " {");
			ps.println("compound=true;");
			ps.println("color=black;");
			render(ps, m_root);
			ps.println("}");
		}
	}

	protected void render(PrintStream ps, Node current)
	{
		if (m_expanded.contains(current))
		{
			return;
		}
		renderNode(ps, current);
		m_expanded.add(current);
		for (int i = 0; i < current.getOutputArity(); i++)
		{
			Collection<Pin<? extends Node>> pins = current.getOutputLinks(i);
			for (Pin<? extends Node> pin : pins)
			{
				Node target = (Node) pin.getNode();
				render(ps, target);
				renderTransition(ps, current, i, pin);
			}
		}
	}
	
	protected void renderTransition(PrintStream ps, Node from, int out_index, Pin<? extends Node> pin)
	{
		String source_id = "", dest_id = "";
		Node to = pin.getNode();
		if (from instanceof NestedNode)
		{
			NestedNode nn_from = (NestedNode) from;
			Pin<? extends Node> inner_pin = nn_from.getAssociatedOutput(out_index);
			Node inner_node = inner_pin.getNode();
			source_id = m_nodeIds.get(inner_node);
		}
		else
		{
			source_id = m_nodeIds.get(from);
		}
		if (to instanceof NestedNode)
		{
			NestedNode nn_to = (NestedNode) to;
			Pin<? extends Node> inner_pin = nn_to.getAssociatedInput(pin.getIndex());
			Node inner_node = inner_pin.getNode();
			dest_id = m_nodeIds.get(inner_node);
		}
		else
		{
			dest_id = m_nodeIds.get(to);
		}
		ps.println(source_id + " -> " + dest_id + ";");
	}

	protected void renderNode(PrintStream ps, Node current)
	{
		if (m_rendered.contains(current))
		{
			return;
		}
		m_rendered.add(current);
		String n_id = m_prefix + m_idCounter++;
		m_nodeIds.put(current, n_id);
		if (current instanceof OrNode)
		{
			ps.println(n_id + " [shape=\"circle\",label=<<font color='white'><b>∨</b></font>>,width=.3,fixedsize=\"true\",fillcolor=\"red\",textcolor=\"white\"];");
		}
		else if (current instanceof AndNode)
		{
			ps.println(n_id + " [shape=\"circle\",label=<<font color='white'><b>∧</b></font>>,width=.3,fixedsize=\"true\",fillcolor=\"blue\",textcolor=\"white\"];");
		}
		else if (current instanceof PartNode)
		{
			renderPartNode(ps, (PartNode) current, n_id);
		}
		else if (current instanceof NestedNode)
		{
			renderNestedNode(ps, (NestedNode) current, n_id);
		}
		else
		{
			ps.println(n_id + " [label=\"?\"];");
		}
	}

	protected void renderPartNode(PrintStream ps, PartNode current, String n_id)
	{
		Part d = current.getPart();
		Object o = current.getSubject();
		ps.println(n_id + "[label=\"" + d.toString() + " of " + o.toString() + "\"];");
	}

	protected String renderNestedNode(PrintStream ps, NestedNode current, String n_id)
	{
		Node inner_start = current.getAssociatedInput(0).getNode();
		String new_prefix = "C" + n_id;
		if (m_prefix.isEmpty())
		{
			new_prefix = "cluster_" + new_prefix;
		}
		LineageDotRenderer sub_renderer = new LineageDotRenderer(inner_start, new_prefix);
		sub_renderer.render(ps);
		m_nodeIds.putAll(sub_renderer.m_nodeIds);
		return "C" + n_id + "0";
	}

	protected String getNodeId(Node current)
	{
		String n_id = "";
		if (!m_nodeIds.containsKey(current))
		{
			n_id = m_prefix + m_idCounter++;
			m_nodeIds.put(current, n_id);
		}
		else
		{
			n_id = m_nodeIds.get(current);
		}
		return n_id;
	}

}
