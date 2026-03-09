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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.petitpoucet.Connectable.Connection;

public class Subgraph extends Vertex
{
	/**
	 * The root of the subgraph.
	 */
	/*@ non_null @*/ protected Vertex m_root;

	/**
	 * The set of all vertices contained in this subgraph.
	 */
	/*@ non_null @*/ protected final Set<Vertex> m_vertices;

	/**
	 * The set of all leaves contained in this subgraph.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_leaves;
	
	protected Connection m_inputConnection;

	/*@ non_null @*/ protected final Map<Vertex,Vertex> m_outputConnections;

	/**
	 * Creates a new subgraph.
	 * @param root The root of the subgraph
	 * @param vertices The set of all vertices contained in this subgraph
	 */
	public Subgraph(/*@ non_null @*/ Vertex root, /*@ non_null @*/ Collection<Vertex> vertices)
	{
		super();
		m_root = root;
		m_vertices = new HashSet<>(vertices.size());
		m_vertices.addAll(vertices);
		m_leaves = m_root.findLeaves();
		m_inputConnection = null;
		m_outputConnections = new HashMap<>();
	}

	public void pushRoot(Vertex v)
	{
		v.addChild(m_root);
		m_root = v;
		m_vertices.add(v);
	}

	@Override
	public void render(PrintStream ps, String indent, int nesting)
	{
		ps.print(indent);
		for (int i = 0; i <= nesting + 1; i++)
		{
			ps.print("*");
		}
		ps.println();
		m_root.render(ps, indent + "  ", nesting + 1);
		for (Map.Entry<Vertex,Vertex> e : m_outputConnections.entrySet())
		{
			Vertex v = e.getValue();
			if (v != null)
			{
				v.render(ps, indent + "  ", nesting);
			}
		}
	}
		
	@Override
	public void addChild(Vertex v)
	{
		throw new UnsupportedOperationException("Must specify how to attach");
	}
	
	public void addChild(Vertex v, Vertex inner_leaf)
	{
		super.addChild(v);
		if (!m_vertices.contains(inner_leaf))
		{
			throw new IllegalArgumentException("Second argument must be an inner vertex");
		}
		m_outputConnections.put(inner_leaf, v);
	}
	
	public Vertex innerRoot()
	{
		return m_root;
	}
	
	public List<Vertex> innerLeaves()
	{
		return m_leaves;
	}

	@Override
	protected void findLeaves(List<Vertex> leaves)
	{
		for (Vertex v : m_children)
		{
			v.findLeaves(leaves);
		}
	}
}
