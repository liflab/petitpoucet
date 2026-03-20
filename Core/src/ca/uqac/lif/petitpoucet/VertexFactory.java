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

import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;

/**
 * Factory for creating vertices.
 * @author Sylvain Hallé 
 */
public interface VertexFactory
{
	/**
	 * Gets an OR vertex. This method always returns a new vertex, as OR
	 * vertices are not identified by their content, but by their position in
	 * the graph.
	 * @return An OR vertex
	 */
	/*@ non_null @*/ public OrVertex getOr();
	
	/**
	 * Gets an AND vertex. This method always returns a new vertex, as AND
	 * vertices are not identified by their content, but by their position in
	 * the graph.
	 * @return An AND vertex
	 */
	/*@ non_null @*/ public AndVertex getAnd();
	
	/**
	 * Gets a part vertex for the given part and source. If a vertex with the same part and source
	 * already exists, it is returned. Otherwise, a new vertex is created and returned.
	 * @param p The part for which to get the vertex. This parameter cannot be null.
	 * @param s The source of the part. This parameter can be null.
	 * @return A part vertex for the given part and source.
	 */
	/*@ non_null @*/ public PartVertex getPart(Part p, Object o);
	
	/**
	 * Gets a part vertex for the given part vertex. If a vertex with the same part and source
	 * already exists, it is returned. Otherwise, the given vertex is added to the list
	 * of vertices and returned.
	 * @param v The part vertex for which to get the vertex. This parameter cannot be null.
	 * @return A part vertex for the given part vertex.
	 */
	/*@ non_null @*/ public ConcreteVertex getPart(PartVertex v);
	
	/**
	 * Creates a new sub-factory associated to a given object.
	 * @param key The object to associate to the sub-factory. This parameter
	 * cannot be null.
	 * @return The corresponding sub-factory
	 */
	public VertexFactory subfactory(Object o);
	
	/**
	 * Determines if the factory already contains a given part vertex.
	 * @param p The part to look for
	 * @param o The object to look for
	 * @return {@code true} if the vertex is present, {@code false} otherwise
	 */
	public boolean contains(Part p, Object o);
	
	/**
	 * Determines if the factory already contains a given part vertex.
	 * @param v The vertex
	 * @return {@code true} if the vertex is present, {@code false} otherwise
	 */
	public boolean contains(Vertex v);
	
	/**
	 * Clears all nodes in the factory.
	 */
	public void clear();
	
	/**
	 * Exports connected vertices as a subgraph. The method assumes that the
	 * first vertex in the list of vertices is the root of the subgraph,
	 * and that all vertices in the list are connected to it.
	 * @return A subgraph containing all vertices in the factory
	 */
	public Subgraph subgraph();
	
	/**
	 * Gets an AND vertex with the given vertices as children.
	 * This method is a shortcut for creating an AND vertex and
	 * adding the given vertices as children.
	 * @param vertices The vertices to add as children of the
	 * AND vertex. This parameter can be null or empty.
	 * @return An AND vertex with the given vertices as children
	 */
	public AndVertex and(Vertex... vertices);
	
	/**
	 * Gets an OR vertex with the given vertices as children.
	 * This method is a shortcut for creating an OR vertex and
	 * adding the given vertices as children.
	 * @param vertices The vertices to add as children of the
	 * OR vertex. This parameter can be null or empty.
	 * @return An OR vertex with the given vertices as children
	 */
	public OrVertex or(Vertex... vertices);
	
	/**
	 * Gets a tree vertex with the given root and children. This method is a shortcut for
	 * creating a tree vertex and adding the given vertices as children.
	 * @param root The root of the tree. This parameter cannot be null.
	 * @param vertices The vertices to add as children of the
	 * root vertex. This parameter can be null or empty.
	 * @return A tree vertex with the given root and children
	 */
	public Vertex tree(Vertex root, Vertex... vertices);
}
