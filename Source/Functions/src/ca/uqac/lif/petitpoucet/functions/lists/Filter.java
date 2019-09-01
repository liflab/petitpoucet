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
package ca.uqac.lif.petitpoucet.functions.lists;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;

/**
 * Filters the elements of a list based on the Boolean values of
 * another.
 * @author Sylvain Hallé
 *
 */
public class Filter extends NaryFunction
{
	/**
	 * Records which events of the last list have been included
	 */
	protected List<Boolean> m_included;
	
	/**
	 * Creates a new instance of the function
	 */
	public Filter()
	{
		super(2);
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		List<?> list1 = (List<?>) inputs[0];
		List<?> list2 = (List<?>) inputs[1];
		List<Object> list_out = new ArrayList<Object>();
		int len = Math.min(list1.size(), list2.size());
		m_included = new ArrayList<Boolean>(len);
		for (int i = 0; i < len; i++)
		{
			Object o2 = list2.get(i);
			boolean put = false;
			if (o2 instanceof Boolean && ((Boolean) o2).booleanValue())
			{
				put = true;
				list_out.add(list1.get(i));
			}
			m_included.add(put);
		}
		outputs[0] = list_out;
		m_returnedValue[0] = list_out;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator top = d.peek();
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		if (!m_evaluated || !(top instanceof NthElement))
		{
			// We did not evaluate the function; the best we can say is that the output depends on
			// the whole input list, but this is an over-approximation
			ComposedDesignator cd = new ComposedDesignator(tail, new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.OVER);
			leaves.add(child);
			return;
		}
		if (top instanceof NthElement)
		{
			// Find position of nth element in the input list
			int required_pos = ((NthElement) top).getIndex();
			if (required_pos >= m_included.size())
			{
				TraceabilityNode child = factory.getUnknownNode();
				root.addChild(child, Quality.EXACT);
				leaves.add(child);
				return;
			}
			int cur_pos = 0;
			for (int i = 0; i < m_included.size(); i++)
			{
				if (m_included.get(i) == true)
				{
					if (cur_pos == required_pos)
					{
						// That's the one
						if (q instanceof ProvenanceQuery)
						{
							ComposedDesignator cd = new ComposedDesignator(tail, new NthElement(i), new NthInput(0));
							TraceabilityNode child = factory.getObjectNode(cd, this);
							root.addChild(child, Quality.EXACT);
							leaves.add(child);
						}
						if (q instanceof CausalityQuery)
						{
							TraceabilityNode and = factory.getAndNode();
							ComposedDesignator cd1 = new ComposedDesignator(tail, new NthElement(i), new NthInput(0));
							TraceabilityNode child1 = factory.getObjectNode(cd1, this);
							and.addChild(child1, Quality.EXACT);
							ComposedDesignator cd2 = new ComposedDesignator(tail, new NthElement(i), new NthInput(1));
							TraceabilityNode child2 = factory.getObjectNode(cd2, this);
							and.addChild(child2, Quality.EXACT);
							root.addChild(and, Quality.EXACT);
							leaves.add(child1);
							leaves.add(child2);
						}
						break;
					}
					cur_pos++;
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "Filter";
	}
}
