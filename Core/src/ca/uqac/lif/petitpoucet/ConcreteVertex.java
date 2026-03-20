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
public abstract class ConcreteVertex implements Vertex, Renderer
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
	 * Creates a new vertex with no children and no parents.
	 */
	public ConcreteVertex()
	{
		super();
		m_children = new ArrayList<>();
		m_parents = new ArrayList<>();
	}

	@Override
	public List<Vertex> getChildren()
	{
		return m_children;
	}

	@Override
	public List<Vertex> getParents()
	{
		return m_parents;
	}

	@Override
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
			if (v instanceof ConcreteVertex)
			{
				((ConcreteVertex) v).render(sb, indent, nesting + 1);
			}
			else
			{
				sb.println(indent + v.toString());
			}
		}
	}

	@Override
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
			leaves.addAll(v.findLeaves());
		}
	}

	@Override
	public int childCount()
	{
		return m_children.size();
	}

	@Override
	public int parentCount()
	{
		return m_parents.size();
	}

	@Override
	public void addChild(Vertex v)
	{
		if (v == this)
		{
			throw new IllegalArgumentException("Attempting to connect a vertex to itself");
		}
		if (v instanceof ConcreteVertex)
		{
			((ConcreteVertex) v).getParents().add(this);
			m_children.add((ConcreteVertex) v);
		}
	}

	/**
	 * A part vertex. This vertex represents a part of an object, and is identified
	 * by the part and the source of the part.
	 */
	public static class PartVertex extends ConcreteVertex
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
		 * the {@link IdentityVertexFactory} class.
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
