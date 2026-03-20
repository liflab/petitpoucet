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

import java.util.List;

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;

/**
 * An abstract vertex. This interface is implemented by both {@link ConcreteVertex}
 * and {@link LazyVertex}, and allows to treat them in a uniform way.
 * @author Sylvain Hallé
 */
public interface Vertex
{
	/**
	 * Gets the vertex corresponding to this abstract vertex. If this
	 * abstract vertex is a {@link LazyVertex}, it will be concretized
	 * and the resulting vertex will be returned. Otherwise, this method
	 * simply casts this abstract vertex to a {@link ConcreteVertex} and returns
	 * it.
	 * @param v The abstract vertex to convert
	 * @return The corresponding vertex
	 * @throws ExplanationException If an error occurs during concretization
	 */
	public static ConcreteVertex get(Vertex v) throws ExplanationException
	{
		if (v instanceof LazyVertex)
		{
			return ((LazyVertex) v).concretize();
		}
		return (ConcreteVertex) v;
	}
	
	/**
	 * Adds a child to this vertex. This method also adds this vertex as a parent
	 * of the child. It is not recommended to modify the list of children or parents
	 * directly, as it may cause inconsistencies in the graph.
	 * @param v The AbstractVertex to add as a child of this vertex
	 */
	public void addChild(Vertex v);
	
	/**
	 * Gets the number of children of this vertex.
	 * @return The number of children of this vertex
	 */
	public int childCount();
	
	/**
	 * Gets the number of parents of this vertex.
	 * @return The number of parents of this vertex
	 */
	public int parentCount();
	
	/**
	 * Gets the list of children of this vertex. The list is modifiable, but it is not
	 * recommended to modify it directly, as it may cause inconsistencies in the graph.
	 * @return The list of children of this vertex
	 */
	public List<Vertex> getChildren();
	
	/**
	 * Gets the list of parents of this vertex. The list is modifiable, but it is not
	 * recommended to modify it directly, as it may cause inconsistencies in the graph.
	 * @return The list of parents of this vertex
	 */
	public List<Vertex> getParents();
	
	/**
	 * Finds the leaves of the subgraph rooted in this vertex. The leaves are the
	 * vertices that have no children.
	 * @return The set of leaves of the subgraph rooted in this vertex
	 */
	public List<Vertex> findLeaves();
	
	/**
	 * Finds the root of the graph to which this vertex belongs. This method
	 * assumes that the graph has a single root, and that there are no
	 * cycles in the graph.
	 * @return The root of the graph to which this vertex belongs
	 */
	public Vertex findRoot();
	
	/**
	 * An AND vertex. This vertex represents a conjunction of its children.
	 */
	public static interface AndVertex extends Vertex
	{
		// Nothing to add
	}
	
	/**
	 * An OR vertex. This vertex represents a disjunction of its children.
	 */
	public static interface OrVertex extends Vertex
	{
		// Nothing to add
	}
}
