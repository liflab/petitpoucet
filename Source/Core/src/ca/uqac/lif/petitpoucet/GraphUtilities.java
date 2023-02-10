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
	 * Private constructor.
	 */
	private GraphUtilities()
	{
		super();
	}

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
	 * nodes are exploded. For example, given the following graph:
	 * <p>
	 * <img src="{@docRoot}/doc-files/Flatten-before.png" alt="Lineage graph before" />
	 * <p>
	 * the application of the method would result in the following graph:
	 * <p>
	 * <img src="{@docRoot}/doc-files/Flatten-after.png" alt="Lineage graph after" />
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
	 * Boolean nodes and leaves are kept. For example, given the following graph:
	 * <p>
	 * <img src="{@docRoot}/doc-files/Squash-before.png" alt="Lineage graph before" />
	 * <p>
	 * the application of the method would result in the following graph:
	 * <p>
	 * <img src="{@docRoot}/doc-files/Squash-after.png" alt="Lineage graph after" />
	 * <p>
	 * The method does not simply remove intermediate non-Boolean nodes. In the
	 * graph above, this would result in a &and; node (top-left) being connected
	 * to another &and; node (the child of node j). When such a situation occurs,
	 * the leaves of the second are appended directly to the parent &and; node.
	 * An identical procedure is applied for the case of an &or; node having an
	 * &or; child.
	 * 
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
		if (!(target instanceof AndNode) && !(target instanceof OrNode) && isLeaf(target))
		{
			// Don't skip over a leaf node
			NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
			return;
		}
		if (target instanceof AndNode || target instanceof OrNode)
		{
			boolean is_nary = target.getOutputNodeCount() > 1;
			if (target instanceof AndNode && is_nary && !(parent instanceof AndNode))
			{
				// Don't skip over an AND node of arity > 1 if parent is something else
				NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
				out_parent = target_dup;
			}
			if (target instanceof OrNode && is_nary && !(parent instanceof OrNode))
			{
				// Don't skip over an OR node of arity > 1 if parent is something else
				NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
				out_parent = target_dup;
			}
		}
		// Otherwise, out_parent = parent, meaning that children of target will be
		// connected to parent of current node (i.e current is skipped over)
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
	
	/**
	 * Converts a lineage graph into a flattened list of clauses. This is best
	 * explained by seeing the graph as a Boolean formula, with the leaves of
	 * the graph corresponding to its ground terms. The method transforms such a
	 * graph into a formula in disjunctive normal form (DNF).
	 * <p>
	 * For example, given the following abstract lineage graph:
	 * <p>
	 * <img src="{@docRoot}/doc-files/AsDnf-example.png" alt="Lineage graph" />
	 * <p>
	 * the application of the method would result in the following list of
	 * clauses:
	 * <blockquote>
	 * [{a,b}, {c,d,f}, {c,e,f}]
	 * </blockquote>
	 * Intuitively, the graph describes two top-level alternatives (root "or"
	 * node): the first (left branch) is composed of the nodes a and b taken
	 * together, producing the clause {a,b}. The second (right branch) is
	 * composed of nodes c and f, along with either d or e (second "or" node),
	 * thus producing the two other clauses {c,d,f} and {c,e,f}.
	 * 
	 * @param root The root of the lineage graph
	 * @return The list of clauses
	 */
	/*@ non_null @*/ public static List<Clause> asDnf(/*@ non_null @*/ Node root)
	{
		List<Clause> clauses = new ArrayList<Clause>();
		if (root instanceof PartNode && isLeaf(root))
		{
			// Leaf: create a singleton clause with it and return
			Clause clause = new Clause();
			clause.add((PartNode) root);
			clauses.add(clause);
			return clauses;
		}
		// Non-leaf node: first recursively get list of clauses from each child
		List<List<Clause>> list_clauses = new ArrayList<List<Clause>>();
		for (int i = 0; i < root.getOutputArity(); i++)
		{
			List<Pin<? extends Node>> pins = root.getOutputLinks(i);
			for (Pin<? extends Node> pin : pins)
			{
				Node child = pin.getNode();
				list_clauses.add(asDnf(child));
			}
		}
		if (root instanceof OrNode)
		{
			// Or node: merge all clause lists into one and return
			for (List<Clause> l_clauses : list_clauses)
			{
				clauses.addAll(l_clauses);
			}
			return clauses;
		}
		if (root instanceof AndNode)
		{
			// And node: "distribute" clause lists
			return Clause.distribute(list_clauses);
		}
		return clauses;
	}
}
