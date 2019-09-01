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

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;

/**
 * Duplicates a function's input to multiple outputs
 * @author Sylvain Hallé
 */
public class Fork extends SingleFunction
{
	public Fork(int out_arity)
	{
		super(1, out_arity);
	}

	public Fork()
	{
		this(2);
	}

	@Override
	public String toString()
	{
		return "Fork";
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		for (int i = 0; i < outputs.length; i++)
		{
			outputs[i] = inputs[0];
		}
	}

	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		ComposedDesignator cd = new ComposedDesignator(d, new CircuitDesignator.NthInput(0));
		TraceabilityNode child = factory.getObjectNode(cd, this);
		leaves.add(child);
		root.addChild(child, Quality.EXACT);
	}
}
