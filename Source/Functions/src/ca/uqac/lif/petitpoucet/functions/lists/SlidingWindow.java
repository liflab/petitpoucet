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
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.ObjectNode;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityTree;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;

public class SlidingWindow implements Function 
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
		super();
		m_function = f;
		m_width = width;
	}

	@Override
	public SlidingWindowQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		List<?> list = (List<?>) inputs[0];
		List<Object> out_list = new ArrayList<Object>();
		int max_pos = Math.max(0, list.size() - m_width);
		List<FunctionQueryable> queryables = new ArrayList<FunctionQueryable>();
		for (int i = 0; i < max_pos; i++)
		{
			Object[] in_out = new Object[1];
			FunctionQueryable fq = evaluateWindow(i, list, in_out, c);
			out_list.add(in_out[0]);
			queryables.add(fq.duplicate());
		}
		outputs[0] = out_list;
		return new SlidingWindowQueryable(toString(), m_width, queryables);
	}
	
	protected FunctionQueryable evaluateWindow(int offset, List<?> list, Object[] outputs, Context c)
	{
		List<Object> lob = new ArrayList<Object>(m_width);
		for (int i = offset; i < offset + m_width; i++)
		{
			lob.add(list.get(i));
		}
		return (FunctionQueryable) m_function.evaluate(new Object[] {lob}, outputs, c);
	}
	
	@Override
	public String toString()
	{
		return m_function + " on a window of " + m_width;
	}
	
	public static class SlidingWindowQueryable extends FunctionQueryable
	{
		protected List<FunctionQueryable> m_innerQueryables;
		
		protected int m_width;
		
		public SlidingWindowQueryable(String reference, int width, List<FunctionQueryable> queryables)
		{
			super(reference, 1, 1);
			m_innerQueryables = queryables;
			m_width = width;
		}
		
		@Override
		public SlidingWindowQueryable duplicate(boolean with_state)
		{
			List<FunctionQueryable> lfq = new ArrayList<FunctionQueryable>(m_innerQueryables.size());
			for (FunctionQueryable fq : m_innerQueryables)
			{
				lfq.add(fq.duplicate(with_state));
			}
			return new SlidingWindowQueryable(m_reference, m_width, lfq);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			Designator top = d.peek();
			if (!(top instanceof NthElement))
			{
				// Can't do anything with this query; at best, say that
				// output depends on all input
				TraceabilityNode and = factory.getAndNode();
				for (int i = 0; i < getInputArity(); i++)
				{
					TraceabilityNode child = factory.getObjectNode(CircuitDesignator.NthInput.get(i), this);
					leaves.add(child);
					and.addChild(child, Quality.EXACT);
				}
				root.addChild(and, Quality.OVER);
				return leaves;
			}
			Designator tail = d.tail();
			int elem_index = ((NthElement) top).getIndex();
			if (elem_index < 0 || elem_index >= m_innerQueryables.size())
			{
				return factory.unknownLink(root);
			}
			FunctionQueryable inner_q = m_innerQueryables.get(elem_index);
			Tracer sub_factory = factory.getSubTracer(toString());
			ComposedDesignator cod = new ComposedDesignator(tail, NthOutput.get(0));
			TraceabilityTree tree = sub_factory.trace(q, cod, inner_q);
			root.addChild(tree.getRoot(), Quality.EXACT);
			List<TraceabilityNode> l_f_links = tree.getLeaves();
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
						NthElement ne = NthElement.get(((NthElement) d_tail_elem).getIndex() + elem_index);
						Designator new_tail = d_tail.tail();
						if (new_tail == null)
						{
							new_tail = Designator.identity;
						}
						cd = new ComposedDesignator(new_tail, ne, NthInput.get(0));
					}
					else
					{
						cd = new ComposedDesignator(f_dob.getDesignator().tail(), NthElement.get(elem_index),
								NthInput.get(index));
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
			return leaves;
		}
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
	public SlidingWindow duplicate(boolean with_state) 
	{
		return new SlidingWindow(m_width, m_function.duplicate(with_state));
	}

	@Override
	public Function duplicate()
	{
		return duplicate(false);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs)
	{
		return evaluate(inputs, outputs, null);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		if (index == 0)
		{
			return List.class;
		}
		return null;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		if (index == 0)
		{
			return List.class;
		}
		return null;
	}

	@Override
	public int getInputArity() 
	{
		return 1;
	}

	@Override
	public int getOutputArity()
	{
		return 1;
	}

	@Override
	public void reset()
	{
		// Nothing to do
	}
}
