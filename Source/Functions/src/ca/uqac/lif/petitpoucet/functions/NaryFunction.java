/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;

/**
 * Generic function of <i>n</i> inputs and 1 output.
 * @author Sylvain Hallé
 */
public abstract class NaryFunction extends SingleFunction
{
	/**
	 * Creates a new instance of the function
	 * @param in_arity The input arity of the function
	 */
	public NaryFunction(int in_arity)
	{
		super(in_arity, 1);
	}

	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.EXACT);
	}

	/**
	 * Provides a "default" answer to a traceability query. Typically, thse
	 * answer links the function's single output to all its inputs.
	 * @param q The type of traceability query
	 * @param output_nb
	 * @param d
	 * @param root
	 * @param factory
	 * @param leaves
	 * @param quality
	 */
	protected void answerQueryDefault(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves, Quality quality)
	{
		// Default behaviour: a function's (single) output is linked to all its inputs
		TraceabilityNode and = factory.getAndNode();
		for (int i = 0; i < m_inArity; i++)
		{
			TraceabilityNode child = factory.getObjectNode(new CircuitDesignator.NthInput(i), this);
			and.addChild(child, quality);
			leaves.add(child);
		}
		root.addChild(and, quality);
	}
}
