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
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;

/**
 * JUnit assertions specific to the manipulation of lineage graphs.
 * @author Sylvain Hallé
 */
public abstract class Assertions
{
	/**
	 * Asserts that the subgraph rooted in vertex <i>v</i><sub>1</sub> is the
	 * same as the subgraph rooted in vertex <i>v</i><sub>2</sub>.
	 * @param v1 The first root
	 * @param v2 The second root
	 * otherwise
	 */
	public static void assertEqualGraphs(AbstractVertex a1, AbstractVertex a2)
	{
		Vertex v1, v2;
		try
		{
			v1 = AbstractVertex.get(a1);
			v2 = AbstractVertex.get(a2);
		}
		catch (ExplanationException e)
		{
			throw new AssertionError(e);
		}
		if (v1 instanceof PartVertex && v2 instanceof PartVertex && !v1.equals(v2))
		{
			throw new AssertionError(v1 + " != " + v2);
		}
		if (v1.childCount() != v2.childCount())
		{
			throw new AssertionError(v1 + " has " + v1.childCount() + " children while " + v2 + " has " + v2.childCount());
		}
		if ((v1 instanceof Subgraph) != (v2 instanceof Subgraph))
		{
			throw new AssertionError("Expected two subgraphs");
		}
		if (v1 instanceof Subgraph)
		{
			Subgraph s1 = (Subgraph) v1;
			Subgraph s2 = (Subgraph) v2;
			assertEqualGraphs(s1.findRoot(), s2.findRoot());
		}
		for (int i = 0; i < v1.childCount(); i++)
		{
			assertEqualGraphs(v1.getChildren().get(i), v2.getChildren().get(i));
		}
	}

	/**
	 * Asserts that the subgraph rooted in vertex <i>v</i><sub>1</sub> is
	 * <em>not</em> the
	 * same as the subgraph rooted in vertex <i>v</i><sub>2</sub>.
	 * @param v1 The first root
	 * @param v2 The second root
	 */
	public static void assertNotEqualGraphs(Vertex v1, Vertex v2)
	{
		try
		{
			assertEqualGraphs(v1, v2);
		}
		catch (AssertionError e)
		{
			// OK
			return;
		}
		throw new AssertionError("Graphs are equal");
	}
}
