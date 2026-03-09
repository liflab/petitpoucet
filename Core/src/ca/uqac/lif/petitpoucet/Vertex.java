/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
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
import java.util.ArrayList;
import java.util.List;

/**
 * A vertex in a directed acyclic graph. This class is used to represent the
 * structure of the graph, and to perform operations on it. The graph is assumed
 * to have a single root. The vertices are of three concrete types:
 * {@link AndVertex}, {@link OrVertex}, {@link PartVertex}, {@link Subgraph}
 * and {@link ProxyVertex}. The first two types are used to represent the structure
 * of the graph, while the third type is used to represent the parts of the graph.
 * @author Sylvain Hallé
 */
public abstract class Vertex implements AbstractVertex, Renderer
{
	/**
	 * The list of children of this vertex, if any.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_children;

	/**
	 * The list of parents of this vertex, if any.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_parents;

	/**
	 * Utility method to create trees of vertices.
	 * @param root The root of the tree
	 * @param children The children to attach to the root
	 * @return The root of the tree
	 */
	public static Vertex tree(Vertex root, Vertex ... children)
	{
		for (Vertex c : children)
		{
			root.addChild(c);
		}
		return root;
	}

	/**
	 * Utility method to create trees of vertices with an AND vertex as root.
	 * @param children The children to attach to the root
	 * @return The root of the tree
	 */
	public static Vertex and(Vertex ... children)
	{
		return tree(new AndVertex(), children);
	}

	/**
	 * Utility method to create trees of vertices with an OR vertex as root.
	 * @param children The children to attach to the root
	 * @return The root of the tree
	 */
	public static Vertex or(Vertex ... children)
	{
		return tree(new OrVertex(), children);
	}

	/**
	 * Creates a new vertex with no children and no parents.
	 */
	public Vertex()
	{
		super();
		m_children = new ArrayList<>();
		m_parents = new ArrayList<>();
	}

	/**
	 * Gets the list of children of this vertex. The list is modifiable, but it is not
	 * recommended to modify it directly, as it may cause inconsistencies in the graph.
	 * @return The list of children of this vertex
	 */
	public List<Vertex> getChildren()
	{
		return m_children;
	}

	/**
	 * Gets the list of parents of this vertex. The list is modifiable, but it is not
	 * recommended to modify it directly, as it may cause inconsistencies in the graph.
	 * @return The list of parents of this vertex
	 */
	public List<Vertex> getParents()
	{
		return m_parents;
	}

	/**
	 * Finds the root of the graph to which this vertex belongs. This method
	 * assumes that the graph has a single root, and that there are no
	 * cycles in the graph.
	 * @return The root of the graph to which this vertex belongs
	 */
	public Vertex findRoot()
	{
		if (m_parents.isEmpty())
		{
			return this;
		}
		/* Since it is assumed that the DAG has a single root,
		 * we can reach it regardless of which parent we expand. */
		return m_parents.get(0).findRoot();
	}

	/**
	 * Prints the subgraph rooted in this vertex to the given print stream. The
	 * output is indented to show the structure of the graph. This method is
	 * useful for debugging purposes.
	 * @param ps The print stream to which to print the graph
	 */
	public void render(PrintStream ps)
	{
		render(ps, "", 0);
	}

	/**
	 * Prints the subgraph rooted in this vertex to the given print stream. The
	 * output is indented to show the structure of the graph. This method is
	 * useful for debugging purposes. This method is called by
	 * {@link #render(PrintStream)}, and should not be called directly.
	 * @param sb The print stream to which to print the graph
	 * @param indent The indentation to use for the current vertex; this
	 * parameter is used to indent the output to show the structure of the graph
	 */
	protected void render(PrintStream sb, String indent, int nesting)
	{
		sb.print(indent);
		for (int i = 0; i <= nesting; i++)
		{
			sb.print("*");
		}
		sb.print(toString());
		sb.println();
		indent += "  ";
		for (Vertex v : m_children)
		{
			v.render(sb, indent, nesting);
		}
	}
	
	/**
	 * Finds the leaves of the subgraph rooted in this vertex. The leaves are the
	 * vertices that have no children.
	 * @return The set of leaves of the subgraph rooted in this vertex
	 */
	public List<Vertex> findLeaves()
	{
		List<Vertex> leaves = new ArrayList<>();
		findLeaves(leaves);
		return leaves;
	}

	/**
	 * Finds the leaves of the subgraph rooted in the given vertex, and adds them to
	 * the given set. This method is called by {@link #findLeaves()}, and should
	 * not be called directly.
	 * @param leaves The set to which to add the leaves; this parameter is used to
	 * accumulate the leaves as they are found
	 */
	protected void findLeaves(List<Vertex> leaves)
	{
		if (leaves.contains(this))
		{
			return;
		}
		if (m_children.isEmpty())
		{
			leaves.add(this);
			return;
		}
		for (Vertex v : m_children)
		{
			v.findLeaves(leaves);
		}
	}

	/**
	 * Gets the number of children of this vertex.
	 * @return The number of children of this vertex
	 */
	public int childCount()
	{
		return m_children.size();
	}

	/**
	 * Gets the number of parents of this vertex.
	 * @return The number of parents of this vertex
	 */
	public int parentCount()
	{
		return m_parents.size();
	}

	/**
	 * Adds a child to this vertex. This method also adds this vertex as a parent
	 * of the child. It is not recommended to modify the list of children or parents
	 * directly, as it may cause inconsistencies in the graph.
	 * @param v The AbstractVertex to add as a child of this vertex
	 */
	public void addChild(Vertex v)
	{
		if (v == this)
		{
			throw new IllegalArgumentException("Attempting to connect a vertex to itself");
		}
		v.getParents().add(this);
		m_children.add(v);
	}

	/**
	 * An AND vertex. This vertex represents a conjunction of its children.
	 */
	public static class AndVertex extends Vertex
	{
		/**
		 * Creates a new AND vertex with no children and no parents.
		 * The constructor is package-private to prevent external code from
		 * creating AND vertices directly, as they should be created through
		 * the {@link VertexFactory} class.
		 */
		/* package */ AndVertex()
		{
			super();
		}

		@Override
		public String toString()
		{
			return "\u2227";
		}
	}

	/**
	 * An OR vertex. This vertex represents a disjunction of its children.
	 */
	public static class OrVertex extends Vertex
	{
		/**
		 * Creates a new OR vertex with no children and no parents.
		 * The constructor is package-private to prevent external code from
		 * creating OR vertices directly, as they should be created through
		 * the {@link VertexFactory} class.
		 */
		/* package */ OrVertex()
		{
			super();
		}

		@Override
		public String toString()
		{
			return "\u2228";
		}
	}

	/**
	 * A part vertex. This vertex represents a part of an object, and is identified
	 * by the part and the source of the part.
	 */
	public static class PartVertex extends Vertex
	{
		/**
		 * The part represented by this vertex.
		 */
		/*@ non_null @*/ protected final Part m_part;

		/**
		 * The source of the part represented by this vertex.
		 */
		/*@ null @*/ protected final Object m_subject;

		/**
		 * Creates a new part vertex with the given part and source.
		 * The constructor is package-private to prevent external code from
		 * creating part vertices directly, as they should be created through
		 * the {@link VertexFactory} class.
		 * @param p The part represented by this vertex. This parameter cannot be null.
		 * @param s The source of the part represented by this vertex. This parameter can be null.
		 */
		/* package */ PartVertex(/*@ non_null @*/ Part p, /*@ null @*/ Object s)
		{
			super();
			m_part = p;
			m_subject = s;
		}

		@Override
		public int hashCode()
		{
			return m_part.hashCode() + m_subject.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof PartVertex))
			{
				return false;
			}
			if (!((PartVertex) o).m_part.equals(m_part))
				return false;
			return ((PartVertex) o).m_subject.equals(m_subject);
		}

		@Override
		public String toString()
		{
			return m_part + "(" + m_subject + ")";
		}

		/**
		 * Gets the part represented by this vertex.
		 * @return The part
		 */
		/*@ pure non_null @*/ public Part getPart()
		{
			return m_part;
		}

		/**
		 * Gets the subject represented by this vertex.
		 * @return The subject
		 */
		/*@ pure null @*/ public Object getSubject()
		{
			return m_subject;
		}
	}
}
