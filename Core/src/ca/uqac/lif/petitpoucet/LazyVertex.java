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

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;

/**
 * A {@link Vertex} that delays the calculation of an explanation until
 * a call to {@link #concretize()}.
 * @author Sylvain Hallé
 */
public abstract class LazyVertex implements AbstractVertex
{
	/**
	 * The factory that will be used to create nodes.
	 */
	protected final VertexFactory m_factory;
	
	/**
	 * The part to be explained.
	 */
	private final Part m_part;
	
	/**
	 * Creates a new lazy vertex.
	 * @param f The factory that will be used to create nodes
	 * @param p The part to be explained
	 */
	public LazyVertex(VertexFactory f, Part p)
	{
		super();
		m_factory = f;
		m_part = p;
	}
	
	/**
	 * Gets the explanation of this vertex enclosed in a subgraph.
	 * @return The subgraph
	 */
	public Subgraph subgraph()
	{
		return m_factory.subgraph();
	}
	
	/**
	 * Calculates the explanation for a given part.
	 * @param p The part to explain
	 * @return The root of the lineage graph.
	 * @throws ExplanationException Thrown if an error occurs in the calculation
	 * of the explanation
	 */
	public abstract Vertex concretize(Part p) throws ExplanationException;
	
	/**
	 * Calculates the explanation.
	 * @return The root of the lineage graph.
	 * @throws ExplanationException Thrown if an error occurs in the calculation
	 * of the explanation
	 */
	public Vertex concretize() throws ExplanationException
	{
		return concretize(m_part);
	}
}
