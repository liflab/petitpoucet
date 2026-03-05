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
import java.util.HashSet;
import java.util.Set;

public class Subgraph extends Vertex
{
	/**
	 * The root of the subgraph.
	 */
	/*@ non_null @*/ protected final Vertex m_root;
	
	/**
	 * The set of all vertices contained in this subgraph.
	 */
	/*@ non_null @*/ protected final Set<Vertex> m_vertices;
	
	/**
	 * The set of all leaves contained in this subgraph.
	 */
	/*@ non_null @*/ protected final Set<Vertex> m_leaves; 
	
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
	}
	
	@Override
	public void render(PrintStream ps)
	{
		m_root.render(ps);
	}
	
	@Override
	/*@ pure non_null @*/ public Set<Vertex> findLeaves()
	{
		return m_leaves;
	}
	
	@Override
	public void addChild(Vertex v)
	{
		throw new UnsupportedOperationException("A leaf must be specified");
	}
	
	/**
	 * Adds a child vertex to the current subgraph, by attaching it to one of
	 * its leaves.
	 * @param v The vertex to add as a child
	 * @param leaf The leaf vertex to which it should be attached
	 */
	public void addChild(/*@ non_null @*/ Vertex v, /*@ non_null @*/ Vertex leaf)
	{
		if (!m_leaves.contains(leaf))
		{
			throw new IllegalArgumentException("Attempting to add a child to a non-leaf vertex");
		}
		super.addChild(v);
		leaf.addChild(v);
	}

	@Override
	public Vertex findRoot()
	{
		return m_root;
	}
}
