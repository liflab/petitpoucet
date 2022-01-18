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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.dag.FlatteningCrawler;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.dag.Pin;

/**
 * Utility methods for transforming lineage graphs.
 * @author Sylvain Hallé
 */
public class GraphUtilities
{
	/**
	 * Simplifies a single-rooted lineage graph. This method is the combined
	 * application of {@link #squash(Node) squash()} and
	 * {@link #flatten(Node) flatten()}.
	 * @param root The root of the original graph
	 * @return The root of the squashed graph
	 */
	/*@ non_null @*/ public static Node simplify(/*@ non_null @*/ Node root)
	{
		return squash(flatten(root));
	}
	
	/**
	 * Simplifies a list of lineage graphs. This method is the combined
	 * application of {@link #squash(List)} and
	 * {@link #flatten(List) flatten()}.
	 * @param roots The roots of the original graphs
	 * @return The roots of the squashed graphs
	 */
	/*@ non_null @*/ public static List<Node> simplify(/*@ non_null @*/ List<Node> roots)
	{
		return squash(flatten(roots));
	}
	
	/**
	 * Out of a single-rooted lineage graph, creates another graph where nested
	 * nodes are exploded.
	 * @param root The root of the original graph
	 * @return The root of the simplified graph
	 */
	/*@ non_null @*/ public static Node flatten(/*@ non_null @*/ Node root)
	{
		FlatteningCrawler fc = new FlatteningCrawler(root, NodeConnector.instance);
		fc.crawl();
		return fc.getRootCopy();
	}
	
	/**
	 * Out of a list of lineage graph roots, creates another set of graphs where
	 * nested nodes are exploded.
	 * <p>
	 * The operation is less trivial than it seems, as one cannot simply flatten
	 * the graph starting from each root separately. Any node shared between two
	 * sub-graphs will result in two copies. One must therefore pass the set of
	 * copies already produced in a previous flattening operation to the next
	 * one.
	 * @param roots The root of the original graph
	 * @return The list of roots of the simplified graphs
	 */
	/*@ non_null @*/ public static List<Node> flatten(/*@ non_null @*/ List<Node> roots)
	{
		List<Node> flattened = new ArrayList<>(roots.size());
		FlatteningCrawler previous_fc = null;
		for (Node root : roots)
		{
			FlatteningCrawler fc = new FlatteningCrawler(root, NodeConnector.instance, previous_fc);
			fc.crawl();
			flattened.add(fc.getRootCopy());
			previous_fc = fc;
		}
		return flattened;
	}
	
	/**
	 * Out of a list of lineage graphs, creates another graph where only
	 * Boolean nodes and leaves are kept.
	 * @param roots The roots of the original graph
	 * @return The roots of the squashed graph
	 */
	/*@ non_null @*/ public static List<Node> squash(/*@ non_null @*/ List<Node> roots)
	{
		List<Node> squashed = new ArrayList<>(roots.size());
		Set<Node> visited = new HashSet<>();
		Map<Node,Node> duplicates = new HashMap<>();
		for (Node root : roots)
		{
			squashed.add(squash(root, visited, duplicates));
		}
		return squashed;
	}
	
	/**
	 * Out of a single-rooted lineage graph, creates another graph where only
	 * Boolean nodes and leaves are kept.
	 * @param root The root of the original graph
	 * @param visited A set of already visited nodes
	 * @param duplicates A map keeping track of original nodes and their copies
	 * in the resulting graph
	 * @return The root of the squashed graph
	 */
	/*@ non_null @*/ protected static Node squash(/*@ non_null @*/ Node root, Set<Node> visited, Map<Node,Node> duplicates)
	{
		Node new_root = root.duplicate();
		for (int i = 0; i < root.getOutputArity(); i++)
		{
			for (Pin<? extends Node> pin : root.getOutputLinks(i))
			{
				squash(new_root, i, pin, visited, duplicates);
			}
		}
		return new_root;
	}
	
	/**
	 * Out of a single-rooted lineage graph, creates another graph where only
	 * Boolean nodes and leaves are kept.
	 * @param root The root of the original graph
	 * @return The root of the squashed graph
	 */
	/*@ non_null @*/ public static Node squash(/*@ non_null @*/ Node root)
	{
		return squash(root, new HashSet<>(), new HashMap<>());
	}
	
	protected static void squash(Node parent, int pin_index, Pin<? extends Node> pin, Set<Node> visited, Map<Node,Node> duplicates)
	{
		Node target = pin.getNode();
		Node target_dup = null;
		if (duplicates.containsKey(target))
		{
			target_dup = duplicates.get(target);
		}
		else
		{
			target_dup = target.duplicate();
			duplicates.put(target, target_dup);
		}
		visited.add(parent);
		Node out_parent = parent;
		if (!(target instanceof AndNode) && !(target instanceof OrNode))
		{
			if (isLeaf(target))
			{
				NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
				return;
			}
		}
		if (target instanceof AndNode && !(parent instanceof AndNode))
		{
			NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
			out_parent = target_dup;
		}
		if (target instanceof OrNode && !(parent instanceof OrNode))
		{
			NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
			out_parent = target_dup;
		}
		if (!visited.contains(target))
		{
			for (int i = 0; i < target.getOutputArity(); i++)
			{
				for (Pin<? extends Node> t_pin : target.getOutputLinks(i))
				{
					squash(out_parent, i, t_pin, visited, duplicates);
				}
			}
		}
	}
	
	/**
	 * Determines if a node is a leaf.
	 * @param n The node
	 * @return {@code true} if the node is a leaf, {@code false} otherwise.
	 */
	public static boolean isLeaf(/*@ non_null @*/ Node n)
	{
		for (int i = 0; i < n.getOutputArity(); i++)
		{
			if (!n.getOutputLinks(i).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
}
