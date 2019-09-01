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
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.ObjectNode;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityTree;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.SingleFunction;

public class SlidingWindow extends SingleFunction 
{
	/**
	 * The function to be applied on each window
	 */
	protected Function m_function;
	
	/**
	 * The width of the window
	 */
	protected int m_width;
	
	public SlidingWindow(int width, Function f)
	{
		super(1, 1);
		m_function = f;
		m_width = width;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator top = d.peek();
		if (!(top instanceof NthElement))
		{
			// Can't do anything with this query; at best, say that
			// output depends on all input
			TraceabilityNode and = factory.getAndNode();
			for (int i = 0; i < m_inArity; i++)
			{
				TraceabilityNode child = factory.getObjectNode(new CircuitDesignator.NthInput(i), this);
				leaves.add(child);
				and.addChild(child, Quality.EXACT);
			}
			root.addChild(and, Quality.OVER);
			return;
		}
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		int elem_index = ((NthElement) top).getIndex();
		List<TraceabilityNode> l_f_links = getFunctionLinks(q, tail, elem_index, root,
				factory);
		for (TraceabilityNode f_links : l_f_links)
		{
			if (!(f_links instanceof ObjectNode))
			{
				leaves.add(f_links);
				continue;
			}
			DesignatedObject f_dob = ((ObjectNode) f_links).getDesignatedObject();
			Designator f_dob_d = f_dob.getDesignator().peek();
			if (f_dob_d instanceof NthInput)
			{
				// Convert the inner function's input into ApplyToAll's input
				int index = ((NthInput) f_dob_d).getIndex();
				Designator d_tail = f_dob.getDesignator().tail();
				Designator d_tail_elem = d_tail.peek();
				ComposedDesignator cd = null;
				if (d_tail_elem instanceof NthElement)
				{
					// Offset elements by window's offset
					NthElement ne = new NthElement(((NthElement) d_tail_elem).getIndex() + elem_index);
					Designator new_tail = d_tail.tail();
					if (new_tail == null)
					{
						new_tail = Designator.identity;
					}
					cd = new ComposedDesignator(new_tail, ne, new NthInput(0));
				}
				else
				{
					cd = new ComposedDesignator(f_dob.getDesignator().tail(), new NthElement(elem_index),
							new NthInput(index));
				}
				TraceabilityNode tn = factory.getObjectNode(cd, this);
				leaves.add(tn);
				f_links.addChild(tn, Quality.EXACT);
			}
			else
			{
				leaves.add(f_links);
				continue;
			}
		}
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		List<?> list = (List<?>) inputs[0];
		List<Object> out_list = new ArrayList<Object>();
		int max_pos = Math.max(0, list.size() - m_width);
		for (int i = 0; i < max_pos; i++)
		{
			Object value = evaluateWindow(i, list);
			out_list.add(value);
		}
		outputs[0] = out_list;
	}
	
	protected Object evaluateWindow(int offset, List<?> list)
	{
		List<Object> lob = new ArrayList<Object>(m_width);
		for (int i = offset; i < offset + m_width; i++)
		{
			lob.add(list.get(i));
		}
		Object[] outputs = new Object[1];
		m_function.getValue(new Object[] {lob}, outputs);
		return outputs[0];
	}
	
	protected List<TraceabilityNode> getFunctionLinks(TraceabilityQuery q, Designator d,
			int offset, TraceabilityNode root, Tracer factory)
	{
		ComposedDesignator cd = new ComposedDesignator(new NthOutput(0));
		// Replace the function in the context when it evaluated this input
		List<?>[] lists = new List<?>[m_inputs.length];
		for (int i = 0; i < m_inputs.length; i++)
		{
			lists[i] = (List<?>) m_inputs[i];
		}
		evaluateWindow(offset, (List<?>) m_inputs[0]);
		Tracer sub_f = factory.getSubTracer();
		TraceabilityTree tt = sub_f.trace(q, cd, m_function);
		root.addChild(tt.getRoot(), Quality.EXACT);
		return tt.getLeaves();
	}
	
	@Override
	public String toString()
	{
		return m_function + " on a window of " + m_width;
	}
}
