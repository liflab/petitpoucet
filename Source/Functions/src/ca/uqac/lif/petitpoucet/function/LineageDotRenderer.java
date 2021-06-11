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
package ca.uqac.lif.petitpoucet.function;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.dag.Renderer;
import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.GraphUtilities;
import ca.uqac.lif.petitpoucet.OrNode;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;

/**
 * Renders a graph produced by a call to
 * {@link ExplanationQueryable#getExplanation(Part)} as an input file in the
 * DOT format used by <a href="https://graphviz.org">Graphviz</a>.
 * @author Sylvain Hallé
 */
public class LineageDotRenderer implements Renderer
{
	/**
	 * The nodes used as the starting point for the rendering.
	 */
	/*@ non_null @*/ protected List<Node> m_roots;

	/**
	 * A map associating node instances to their uniquely generated ID.
	 */
	/*@ non_null @*/ protected Map<Node,String> m_nodeIds;

	/**
	 * The set of nodes that have been rendered. Used internally by the renderer
	 * to avoid outputting code for the same node when encountered multiple times
	 * in the traversal of the graph.
	 */
	/*@ non_null @*/ protected Set<Node> m_rendered;

	/**
	 * The set of nodes that have been expanded (nodes that had their children
	 * rendered). Used internally by the renderer to avoid expanding the same
	 * node when encountered multiple times in the traversal of the graph.
	 */
	/*@ non_null @*/ protected Set<Node> m_expanded;

	/**
	 * The prefix to give to each node ID in the graph.
	 */
	/*@ non_null @*/ protected String m_prefix;

	/**
	 * The prefix to give to each node ID in the graph.
	 */
	/*@ non_null @*/ protected String m_indent;
	
	/**
	 * The nodes that corresponds to he leaves of the graph.
	 */
	/*@ non_null @*/ protected List<Node> m_leaves;

	/**
	 * A counter used to give unique IDs to each new node encountered in the
	 * graph.
	 */
	protected int m_idCounter;

	/**
	 * The nesting level of this graph.
	 */
	protected int m_nestingLevel;

	/**
	 * Flag that determines if the captions of non-leaf nodes should be printed.
	 */
	protected boolean m_noCaptions;

	/**
	 * Creates a new instance of renderer.
	 * @param roots The nodes used as the starting point for the rendering. These
	 * are typically the roots of a directed acyclic graph.
	 * @param prefix The prefix to give to each node ID in the graph
	 * @param nesting_level The nesting level of this graph
	 * @param no_captions Flag that determines if the captions of non-leaf nodes
	 * should be printed. If set to {@code true}, these nodes will simply be
	 * rendered as colored circles.
	 */
	public LineageDotRenderer(/*@ non_null @*/ List<Node> roots, /*@ non_null @*/ String prefix, int nesting_level, boolean no_captions)
	{
		super();
		m_idCounter = 0;
		m_roots = roots;
		m_nodeIds = new HashMap<Node,String>();
		m_rendered = new HashSet<Node>();
		m_expanded = new HashSet<Node>();
		m_prefix = prefix;
		m_nestingLevel = nesting_level;
		m_indent = getIndent(nesting_level);
		m_noCaptions = no_captions;
		m_leaves = new ArrayList<Node>();
	}
	
	/**
	 * Creates a new instance of renderer.
	 * @param root The node used as the starting point for the rendering. This
	 * is typically the root of a directed acyclic graph.
	 * @param prefix The prefix to give to each node ID in the graph
	 * @param nesting_level The nesting level of this graph
	 * @param no_captions Flag that determines if the captions of non-leaf nodes
	 * should be printed. If set to {@code true}, these nodes will simply be
	 * rendered as colored circles.
	 */
	public LineageDotRenderer(/*@ non_null @*/ Node root, /*@ non_null @*/ String prefix, int nesting_level, boolean no_captions)
	{
		this((List<Node>) Arrays.asList(root), prefix, nesting_level, no_captions);
	}

	/**
	 * Creates a new instance of renderer. 
	 * @param roots The nodes used as the starting point for the rendering. These
	 * are typically the roots of a directed acyclic graph.
	 */
	public LineageDotRenderer(/*@ non_null @*/ Node ... roots)
	{
		this((List<Node>) Arrays.asList(roots), "", 0, false);
	}
	
	/**
	 * Creates a new instance of renderer. 
	 * @param roots The nodes used as the starting point for the rendering. These
	 * are typically the roots of a directed acyclic graph.
	 */
	public LineageDotRenderer(/*@ non_null @*/ List<Node> roots)
	{
		this(roots, "", 0, false);
	}
	
	/**
	 * Sets whether to hide captions of non-leaf nodes.
	 * @param b Set to {@code true} to hide captions, {@code false} otherwise
	 */
	public void setNoCaptions(boolean b)
	{
		m_noCaptions = b;
	}

	@Override
	public void render(PrintStream ps)
	{
		if (m_prefix.isEmpty())
		{
			ps.println("digraph G {");
			ps.println("compound=true;");
			ps.println("node [style=\"filled\",shape=\"rectangle\",fontsize=10,fontname=\"Arial\"]");
			for (Node root : m_roots)
			{
				render(ps, root);
			}
			if (m_roots.size() > 1)
			{
				printRanks(ps);
			}
			ps.println("}");
		}
		else
		{
			// This case only makes sense for a sub-graph with a single root
			ps.println(m_indent + "subgraph " + m_prefix + " {");
			ps.println(m_indent + "compound=true;");
			ps.println(m_indent + "color=black;");
			ps.println(m_indent + "style=filled;");
			ps.println(m_indent + "fillcolor=\"" + getBackgroundColor(m_nestingLevel) + "\";");
			render(ps, m_roots.get(0));
			ps.println(m_indent + "}");
		}
	}
	
	protected void printRanks(PrintStream ps)
	{
		ps.println("edge [style=invis];");
		ps.print("{rank=same; ");
		for (int i = 0; i < m_roots.size(); i++)
		{
			if (i > 0)
			{
				ps.print(" -> ");
			}
			ps.print(m_nodeIds.get(m_roots.get(i)));
		}
		ps.println(";};");
		ps.print("{rank=same; ");
		for (int i = 0; i < m_leaves.size(); i++)
		{
			if (i > 0)
			{
				ps.print(" -> ");
			}
			ps.print(m_nodeIds.get(m_leaves.get(i)));
		}
		ps.println(";};");
	}

	/**
	 * Recursive function that renders a node and then calls itself to render its
	 * descendants.
	 * @param ps The print stream where the node should be printed
	 * @param current The node to render
	 */
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

	/**
	 * Renders a transition between two nodes of the graph.
	 * @param ps The print stream where the transition should be printed
	 * @param from The source node
	 * @param out_index The index of the output pin on the source node
	 * corresponding
	 * @param pin The pin on the destination node of this transition
	 */
	protected void renderTransition(PrintStream ps, Node from, int out_index, Pin<? extends Node> pin)
	{
		String source_id = "", dest_id = "";
		Node to = pin.getNode();
		/*if (from instanceof DummyNode || to instanceof DummyNode)
		{
			return;
		}*/
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
		ps.println(m_indent + source_id + " -> " + dest_id + ";");
	}

	/**
	 * Renders a node. This method only adds a line in the output file for the
	 * node itself and its properties.
	 * @param ps The print stream where the node should be printed
	 * @param current The node to render
	 */
	protected void renderNode(PrintStream ps, Node current)
	{
		if (m_rendered.contains(current))
		{
			return;
		}
		m_rendered.add(current);
		if (GraphUtilities.isLeaf(current))
		{
			m_leaves.add(current);
		}
		String n_id = m_prefix + m_idCounter++;
		m_nodeIds.put(current, n_id);
		if (current instanceof OrNode)
		{
			ps.println(m_indent + n_id + " [shape=\"circle\",label=<<font color='white'><b>∨</b></font>>,width=.25,fixedsize=\"true\",fillcolor=\"red\",textcolor=\"white\"];");
		}
		else if (current instanceof AndNode)
		{
			ps.println(m_indent + n_id + " [shape=\"circle\",label=<<font color='white'><b>∧</b></font>>,width=.25,fixedsize=\"true\",fillcolor=\"blue\",textcolor=\"white\"];");
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
			ps.println(m_indent + n_id + " [label=\"?\"];");
		}
	}

	/**
	 * A specialization of {@link #renderNode(PrintStream, Node)} that renders
	 * a {@link PartNode}.
	 * @param ps The print stream where the node should be printed
	 * @param current The node to render
	 * @param n_id The unique ID given to that node
	 */
	protected void renderPartNode(PrintStream ps, PartNode current, String n_id)
	{
		Part d = current.getPart();
		Object o = current.getSubject();
		String color = getPartNodeColor(d);
		if (m_noCaptions && ((!GraphUtilities.isLeaf(current) && !m_roots.contains(current)) || m_nestingLevel > 0))
		{
			ps.println(m_indent + n_id + " [height=0.25,shape=\"circle\",label=\"\",fillcolor=\"" + color + "\"];");
		}
		else
		{
			ps.println(m_indent + n_id + " [height=0.25,label=\"" + d.toString() + " of " + o.toString() + "\",fillcolor=\"" + color + "\"];");
		}
	}

	/**
	 * A specialization of {@link #renderNode(PrintStream, Node)} that renders
	 * a {@link PartNode}.
	 * @param ps The print stream where the node should be printed
	 * @param current The node to render
	 * @param n_id The unique ID given to that node
	 * @return The ID of the nested node corresponding to the cluster
	 */
	protected String renderNestedNode(PrintStream ps, NestedNode current, String n_id)
	{
		Node inner_start = current.getAssociatedInput(0).getNode();
		String new_prefix = "";
		if (m_prefix.isEmpty())
		{
			new_prefix = "cluster_" + "C" + n_id;
		}
		else
		{
			new_prefix = m_prefix + "C" + n_id.replace("cluster_", "");
		}
		LineageDotRenderer sub_renderer = new LineageDotRenderer(inner_start, new_prefix, m_nestingLevel + 1, m_noCaptions);
		sub_renderer.render(ps);
		m_nodeIds.putAll(sub_renderer.m_nodeIds);
		return "C" + n_id + "0";
	}

	/**
	 * Gets the background color of the sub-graph associated to a given nesting
	 * level. This method has a purely cosmetic effect: it results in
	 * increasingly nested sub-graphs to be pained against increasingly darker
	 * backgrounds to better distinguish them.
	 * @param nesting_level The level of nesting
	 * @return The background color associated to this nesting level
	 */
	/*@ non_null @*/ protected static String getBackgroundColor(int nesting_level)
	{
		switch (nesting_level)
		{
		case 0:
			return "white";
		case 1:
			return "#f8f8f8";
		case 2:
			return "#f0f0f0";
		case 3:
			return "#e8e8e8";
		case 4:
			return "#e0e0e0";
		case 5:
			return "#d8d8d8";
		default:
			return "#d0d0d0";
		}
	}

	/**
	 * Gets the background color of a part node.
	 * @param p The part
	 * @return The color this node is assigned to
	 */
	protected static String getPartNodeColor(Part p)
	{
		if (NthInput.mentionedInput(p) >= 0)
		{
			return "Tomato";
		}
		if (NthOutput.mentionedOutput(p) >= 0)
		{
			return "GreenYellow";
		}
		return "AliceBlue";
	}

	/**
	 * Gets the indent to apply to each line of code depending on the renderrer's
	 * nesting level. The goal of this method is simply to make the output file
	 * minimally human-readable.
	 * @param nesting_level The level of nesting of this renderer
	 * @return A string with the appropriate number of indents
	 */
	protected static String getIndent(int nesting_level)
	{
		String out = "";
		for (int i = 0; i < nesting_level; i++)
		{
			out += "  ";
		}
		return out;
	}
}
