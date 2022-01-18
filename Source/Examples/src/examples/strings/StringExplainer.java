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
package examples.strings;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.strings.Range;

/**
 * Utility methods to display an explanation graph for string manipulation
 * functions. In these graphs, both the input and the output strings are
 * appended to the root and leaves, and the part of these strings is
 * highlighted to help identifying them in the complete string.
 */
public class StringExplainer
{
	/**
	 * Displays an explanation graph.
	 * @param root The root of the graph
	 * @param in The input string
	 * @param out The output string
	 */
	public static void display(PartNode root, String in, String out)
	{
		display(root, in, out, true);
	}

	/**
	 * Displays an explanation graph.
	 * @param root The root of the graph
	 * @param in The input string
	 * @param out The output string
	 * @param all Set to <tt>true</tt> to display complete strings with
	 * highlighted parts, <tt>false</tt> to display only the part
	 */
	public static void display(PartNode root, String in, String out, boolean all)
	{
		NodeFactory f = NodeFactory.getFactory();
		Range r = Range.mentionedRange(root.getPart());
		PartNode new_root = f.getPartNode(Part.all, highlight(r, out, all));
		new_root.addChild(root);
		appendToLeaves(f, root, in, all);
		examples.util.GraphViewer.display(new_root);
	}

	/**
	 * Appends a node for the input string to all leaves of the graph.
	 * @param f A node factory to generate nodes
	 * @param n The current node to examine
	 * @param in The input string
	 * @param all Set to <tt>true</tt> to display complete strings with
	 * highlighted parts, <tt>false</tt> to display only the part
	 */
	protected static void appendToLeaves(NodeFactory f, Node n, String in, boolean all)
	{
		if (n instanceof PartNode && n.getOutputLinks(0).isEmpty())
		{
			// Leaf

			PartNode pn = (PartNode) n;
			Part p = pn.getPart();
			if (NthInput.mentionedInput(p) >= 0)
			{
				Range r = Range.mentionedRange(p);
				if (r != null)
				{
					pn.addChild(f.getPartNode(Part.all, highlight(r, in, all)));
				}
			}
		}
		else
		{
			for (Pin <? extends Node> p : n.getOutputLinks(0))
			{
				appendToLeaves(f, p.getNode(), in, all);
			}
		}
	}

	/**
	 * Highlights a part of a string based on a range
	 * @param r The range of characters in the string
	 * @param s The string to highlight
	 * @param all Set to <tt>true</tt> to display complete strings with
	 * highlighted parts, <tt>false</tt> to display only the part
	 * @return The highlighted string
	 */
	/*@ non_null @*/ protected static String highlight(Range r, String s, boolean all)
	{
		if (all)
		{
			StringBuilder out = new StringBuilder();
			int start = r.getStart();
			int end = r.getEnd();
			if (start > 0)
			{
				out.append("<font color=\"grey\">").append(s.substring(0, start)).append("</font>");
			}
			out.append(s.substring(start, end + 1));
			if (end < s.length())
			{
				out.append("<font color=\"grey\">").append(s.substring(end + 1)).append("</font>");
			}
			return out.toString();
		}
		else
		{
			return s.substring(r.getStart(), r.getEnd() + 1);
		}
	}
}
