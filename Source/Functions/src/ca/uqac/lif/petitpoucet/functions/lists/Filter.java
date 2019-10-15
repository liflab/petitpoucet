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

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;

/**
 * Filters the elements of a list based on the Boolean values of
 * another.
 * @author Sylvain Hallé
 *
 */
@SuppressWarnings("rawtypes")
public class Filter extends BinaryFunction<List,List,List>
{
	/**
	 * Creates a new instance of the function
	 */
	public Filter()
	{
		super(List.class, List.class, List.class);
	}

	@Override
	public FilterQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		List<?> list1 = (List<?>) inputs[0];
		List<?> list2 = (List<?>) inputs[1];
		List<Object> list_out = new ArrayList<Object>();
		int len = Math.min(list1.size(), list2.size());
		List<Boolean> included = new ArrayList<Boolean>(len);
		for (int i = 0; i < len; i++)
		{
			Object o2 = list2.get(i);
			boolean put = false;
			if (o2 instanceof Boolean && ((Boolean) o2).booleanValue())
			{
				put = true;
				list_out.add(list1.get(i));
			}
			included.add(put);
		}
		outputs[0] = list_out;
		return new FilterQueryable(toString(), included);
	}
	
	public static class FilterQueryable extends FunctionQueryable
	{
		/**
		 * Records which events of the last list have been included
		 */
		protected List<Boolean> m_included;

		public FilterQueryable(String reference, List<Boolean> m_included)
		{
			super(reference, 2, 1);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			Designator top = d.peek();
			Designator tail = d.tail();
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			if (top instanceof NthElement)
			{
				// Find position of nth element in the input list
				int required_pos = ((NthElement) top).getIndex();
				if (required_pos >= m_included.size())
				{
					TraceabilityNode child = factory.getUnknownNode();
					root.addChild(child, Quality.EXACT);
					leaves.add(child);
					return leaves;
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
			return leaves;
		}
		
	}
	
	@Override
	public String toString()
	{
		return "Filter";
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Filter duplicate(boolean with_state) 
	{
		return new Filter();
	}
}
