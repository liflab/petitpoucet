/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;

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
	 * Creates a copy of a graph, starting from a root vertex.
	 * @param root The root of the graph to copy
	 * @param f A factory to create new copies of vertices
	 * @return
	 */
	public static Vertex duplicate(Vertex root, VertexFactory f)
	{
		Vertex new_root = copyVertex(root, f);
		if (new_root == null)
		{
			// Node already processed
			return root;
		}
		for (Vertex child : root.getChildren())
		{
			Vertex new_child =  duplicate(child, f);
			new_root.addChild(new_child);
		}
		return new_root;
	}

	/**
	 * Creates a copy of a vertex.
	 * @param v The vertex to copy
	 * @param f A factory to create new copies of vertices
	 * @return A copy of the vertex, or {@code null} if the vertex already
	 * exists in the factory. This is used by {@link #duplicate(Vertex, VertexFactory)}
	 * to indicate that this vertex has already been processed.
	 */
	protected static Vertex copyVertex(Vertex v, VertexFactory f)
	{
		if (f.contains(v))
		{
			return null;
		}
		if (v instanceof PartVertex)
		{
			return f.getPart(((PartVertex) v).getPart(), ((PartVertex) v).getSubject()); 
		}
		if (v instanceof AndVertex)
		{
			return f.getAnd();
		}
		if (v instanceof OrVertex)
		{
			return f.getOr();
		}
		throw new IllegalArgumentException("Unknown type of vertex");
	}

	/**
	 * Removes a vertex <i>v</i> from a graph, by connecting all its children
	 * directly to <i>v</i>'s parent.
	 * @param v The vertex to remove
	 */
	public static void squish(Vertex v)
	{
		if (v.parentCount() != 1)
		{
			throw new IllegalArgumentException("A vertex must have exactly one parent to be squished");
		}
		if (v.childCount() == 0)
		{
			throw new IllegalArgumentException("A vertex must have at least one child to be squished");
		}
		Vertex parent = v.getParents().get(0);
		List<Vertex> children = parent.getChildren();
		int parent_index = children.indexOf(v);
		children.remove(v);
		for (int i = 0; i < v.getChildren().size(); i++)
		{
			Vertex vc = v.getChildren().get(i);
			vc.getParents().remove(v);
			vc.getParents().add(parent);
			parent.getChildren().add(parent_index, vc);
			parent_index++;
		}
	}

	/**
	 * Simplifies a graph by removing all Boolean vertices that have only one
	 * child, or that have the same type as their parent.
	 * @param v The vertex to simplify; the method will simplify the subgraph
	 * rooted in this vertex
	 */
	public static void simplify(Vertex v)
	{
		simplifyRecursive(v, null);
	}

	/**
	 * Recursively simplifies a graph by removing all Boolean vertices that have
	 * only one child, or that have the same type as their parent. This method is
	 * called by {@link #simplify(Vertex)}, and should not be called directly.
	 * @param current The vertex to simplify; the method will simplify the subgraph
	 * rooted in this vertex
	 * @param parent The parent of the current vertex; this parameter is used to
	 * determine if the current vertex has the same type as its parent
	 * @return {@code true} if the current vertex should be squished, {@code false}
	 * otherwise
	 */
	protected static boolean simplifyRecursive(Vertex current, Vertex parent)
	{
		boolean squish_me = false;
		if (current instanceof AndVertex)
		{
			if (parent instanceof AndVertex || current.childCount() == 1)
			{
				squish_me = true;
			}
		}
		if (current instanceof OrVertex)
		{
			if (parent instanceof OrVertex || current.childCount() == 1)
			{
				squish_me = true;
			}
		}
		if (current instanceof Subgraph)
		{
			simplifyRecursive(((Subgraph) current).findRoot(), null);
		}
		List<Vertex> children = new ArrayList<>();
		children.addAll(current.getChildren());
		Set<Vertex> to_squish = new HashSet<>();
		for (Vertex v : children)
		{
			if (simplifyRecursive(v, current))
			{
				to_squish.add(v);
			}
		}
		for (Vertex v : to_squish)
		{
			squish(v);
		}
		return squish_me;
	}

	/**
	 * Collapses a lineage graph by keeping only Boolean vertices and leaves.
	 * @param v The vertex to collapse; the method will simplify the subgraph
	 * rooted in this vertex
	 */
	public static void collapse(Vertex v)
	{
		collapseRecursive(v, null);
	}

	/**
	 * Recursively collapses a lineage graph by keeping only Boolean vertices and
	 * leaves.
	 * @param current The vertex to collapse; the method will simplify the subgraph
	 * rooted in this vertex
	 * @param parent The parent of the current vertex
	 */
	protected static boolean collapseRecursive(Vertex current, Vertex parent)
	{
		boolean collapse_me = !isLeaf(current) && current instanceof PartVertex;
		Set<Vertex> to_squish = new HashSet<>();
		for (Vertex v : current.getChildren())
		{
			if (collapseRecursive(v, current))
			{
				to_squish.add(v);
			}
		}
		for (Vertex v : to_squish)
		{
			squish(v);
		}
		return collapse_me;
	}

	/**
	 * Out of a single-rooted lineage graph, creates another graph where subgraphs
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
	/*@ non_null @*/ public static Vertex flatten(/*@ non_null @*/ Vertex root)
	{
		// TODO
		return null;
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
	/*@ non_null @*/ public static List<Vertex> flatten(/*@ non_null @*/ List<Vertex> roots)
	{
		// TODO
		return null;
	}

	/**
	 * Simplifies a set of lineage graphs.
	 * @param roots The roots of the original graph
	 * @see #simplify(Vertex)
	 */
	/*@ non_null @*/ public static void simplify(/*@ non_null @*/ List<Vertex> roots)
	{
		for (Vertex root : roots)
		{
			simplify(root);
		}
	}

	/**
	 * Determines if a vertex is a leaf.
	 * @param n The vertex
	 * @return {@code true} if the vertex is a leaf, {@code false} otherwise.
	 */
	public static boolean isLeaf(/*@ non_null @*/ Vertex n)
	{
		return n.childCount() == 0;
	}

	/**
	 * Converts a lineage graph into a flattened set of clauses. This is best
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
	 * {{a,b}, {c,d,f}, {c,e,f}}
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
	/*@ non_null @*/ public static MathSet<Clause> asDnf(/*@ non_null @*/ Vertex root)
	{
		MathSet<Clause> clauses = new MathSet<>();
		if (root instanceof PartVertex && isLeaf(root))
		{
			PartVertex pn = (PartVertex) root;
			// Leaf: create a singleton clause with it and return
			Clause clause = new Clause();
			clause.add(pn);
			clauses.add(clause);
			return clauses;
		}
		// Non-leaf node: first recursively get set of clauses from each child
		List<MathSet<Clause>> list_clauses = new ArrayList<MathSet<Clause>>();
		for (Vertex child : root.getChildren())
		{
			list_clauses.add(asDnf(child));
		}
		if (root instanceof AndVertex)
		{
			// And node: "distribute" clause lists
			return Clause.distribute(list_clauses);
		}
		// Or node: merge all clause lists into one and return
		for (MathSet<Clause> l_clauses : list_clauses)
		{
			clauses.addAll(l_clauses);
		}
		return clauses;
	}
}
