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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Vertex
{
	protected final List<Vertex> m_children;

	protected final List<Vertex> m_parents;

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
		Vertex parent = v.m_parents.get(0);
		int parent_index = parent.m_children.indexOf(v);
		parent.m_children.remove(v);
		for (int i = 0; i < v.m_children.size(); i++)
		{
			Vertex vc = v.m_children.get(i);
			vc.m_parents.remove(v);
			vc.m_parents.add(parent);
			parent.m_children.add(parent_index, vc);
			parent_index++;
		}
	}

	public static void simplify(Vertex v)
	{
		simplifyRecursive(v, null);
	}

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
		List<Vertex> children = new ArrayList<>();
		children.addAll(current.m_children);
		List<Vertex> to_squish = new ArrayList<>();
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
	 * Determines if the subgraph rooted in vertex <i>v</i><sub>1</sub> is the
	 * same as the subgraph rooted in vertex <i>v</i><sub>2</sub>.
	 * @param v1 The first root
	 * @param v2 The second root
	 * @return {@code true} if the subgraphs are the same, {@code false}
	 * otherwise
	 */
	public static boolean same(Vertex v1, Vertex v2)
	{
		if (v1 instanceof PartVertex && v2 instanceof PartVertex && !v1.equals(v2))
		{
			return false;
		}
		if (v1.childCount() != v2.childCount())
		{
			return false;
		}
		for (int i = 0; i < v1.childCount(); i++)
		{
			if (!same(v1.m_children.get(i), v2.m_children.get(i)))
			{
				return false;
			}
		}
		return true;
	}

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

	public Vertex()
	{
		super();
		m_children = new ArrayList<>();
		m_parents = new ArrayList<>();
	}

	public List<Vertex> getChildren()
	{
		return m_children;
	}

	public List<Vertex> getParents()
	{
		return m_parents;
	}

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
	
	public void print(PrintStream ps)
	{
		print(ps, "");
	}
	
	protected void print(PrintStream sb, String indent)
	{
		sb.append(indent).append("*").append(toString()).append("\n");
		indent += "  ";
		for (Vertex v : m_children)
		{
			v.print(sb, indent);
		}
	}

	public Set<Vertex> findLeaves()
	{
		Set<Vertex> leaves = new HashSet<>();
		findLeaves(leaves, this);
		return leaves;
	}

	protected void findLeaves(Set<Vertex> leaves, Vertex current)
	{
		if (m_children.isEmpty())
		{
			leaves.add(this);
			return;
		}
		for (Vertex v : m_children)
		{
			findLeaves(leaves, v);
		}
	}

	public int childCount()
	{
		return m_children.size();
	}

	public int parentCount()
	{
		return m_parents.size();
	}

	public void addChild(Vertex v)
	{
		v.m_parents.add(this);
		m_children.add(v);
	}

	public static class AndVertex extends Vertex
	{
		@Override
		public String toString()
		{
			return "\u2227";
		}
	}

	public static class OrVertex extends Vertex
	{
		@Override
		public String toString()
		{
			return "\u2228";
		}
	}

	public static class PartVertex extends Vertex
	{
		protected final Part m_part;

		protected final Object m_subject;

		public PartVertex(Part p, Object s)
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
			return ((PartVertex) o).m_part.equals(m_part) && ((PartVertex) o).m_subject.equals(m_subject);
		}

		@Override
		public String toString()
		{
			return m_part + "(" + m_subject + ")";
		}
	}
}
