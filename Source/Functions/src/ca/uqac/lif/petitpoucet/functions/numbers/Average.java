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
package ca.uqac.lif.petitpoucet.functions.numbers;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

/**
 * Calculates the average of all the numbers in a list
 * @author Sylvain Hallé
 *
 */
public class Average extends NaryFunction
{
	/**
	 * Creates a new instance of the function
	 */
	public Average()
	{
		super(1);
	}
	
	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		m_inputs = inputs;
		List<?> list = (List<?>) inputs[0];
		float sum = 0f, nb = 0f;
		for (Object o : list)
		{
			if (o instanceof Number)
			{
				sum += ((Number) o).floatValue();
				nb++;
			}
		}
		if (nb == 0)
		{
			outputs[0] = 0f;
		}
		else
		{
			outputs[0] = sum / nb;
		}
		m_returnedValue[0] = outputs[0];
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		if (!m_evaluated)
		{
			ComposedDesignator cd = new ComposedDesignator(d, new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			leaves.add(child);
			root.addChild(child, Quality.OVER);
			return;
		}
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		TraceabilityNode and = factory.getAndNode();
		List<?> list = (List<?>) m_inputs[0];
		for (int i = 0; i < list.size(); i++)
		{
			ComposedDesignator cd = new ComposedDesignator(d, new CollectionDesignator.NthElement(i), new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			leaves.add(child);
			and.addChild(child, Quality.EXACT);
		}
		root.addChild(and, Quality.EXACT);
	}
	
	@Override
	public String toString()
	{
		return "AVG";
	}

}
