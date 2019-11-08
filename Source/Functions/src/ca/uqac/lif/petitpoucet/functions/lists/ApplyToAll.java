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
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.ObjectNode;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.StateDuplicable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityTree;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;

/**
 * Applies a function to all the elements of input lists, producing output
 * lists.
 */
public class ApplyToAll implements Function
{
	/**
	 * The function to apply
	 */
	/*@ non_null @*/ protected Function m_function;

	public ApplyToAll(Function f)
	{
		super();
		m_function = f;
	}

	@Override
	public String toString()
	{
		return "ApplyToAll";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ApplyToAllQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		List[] in_lists = new List[m_function.getInputArity()];
		int num_el = Integer.MAX_VALUE;
		for (int i = 0; i < in_lists.length; i++)
		{
			in_lists[i] = (List<?>) inputs[i];
			num_el = Math.min(num_el, in_lists[i].size());
		}
		for (int i = 0; i < m_function.getOutputArity(); i++)
		{
			outputs[i] = new ArrayList<Object>();
		}
		List<Queryable> inner_q = null;
		if (track)
		{
			inner_q = new ArrayList<Queryable>(num_el);
		}
		for (int i = 0; i < num_el; i++)
		{
			Object[] outs = new Object[m_function.getOutputArity()];
			Queryable q = evaluateInnerFunctionAt(i, c, outs, track, in_lists);
			if (track)
			{
				inner_q.add(((StateDuplicable<Queryable>) q).duplicate());
			}
			for (int j = 0; j < outputs.length; j++)
			{
				((List<Object>) outputs[j]).add(outs[j]);
			}
		}
		if (track)
		{
			return new ApplyToAllQueryable(toString(), inner_q);
		}
		return null;
	}
	
	@Override
	public ApplyToAllQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		return evaluate(inputs, outputs, c, true);
	}

	protected Queryable evaluateInnerFunctionAt(int pos, Context c, Object[] outs, boolean track, List<?>... lists)
	{
		m_function.reset();
		Object[] ins = new Object[m_function.getInputArity()];
		
		for (int j = 0; j < ins.length; j++)
		{
			ins[j] = lists[j].get(pos);
		}
		return m_function.evaluate(ins, outs, c, track);
	}
	
	public static class ApplyToAllQueryable extends FunctionQueryable
	{
		protected List<Queryable> m_innerQueryables;
		
		public ApplyToAllQueryable(String reference, List<Queryable> inner_queryables)
		{
			super(reference, 1, 1);
			m_innerQueryables = inner_queryables;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ApplyToAllQueryable duplicate(boolean with_state)
		{
			List<Queryable> lfq = new ArrayList<Queryable>(m_innerQueryables.size());
			for (Queryable fq : m_innerQueryables)
			{
				lfq.add(((StateDuplicable<Queryable>) fq).duplicate(with_state));
			}
			ApplyToAllQueryable ataq = new ApplyToAllQueryable(m_reference, lfq);
			return ataq;
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
			}
			else
			{
				int elem_index = ((NthElement) top).getIndex();
				if (elem_index < 0 || elem_index >= m_innerQueryables.size())
				{
					return factory.unknownLink(root);
				}
				Queryable inner_q = m_innerQueryables.get(elem_index);
				Designator tail = d.tail();
				if (tail == null)
				{
					tail = Designator.identity;
				}
				Tracer sub_factory = factory.getSubTracer(toString());
				ComposedDesignator cd = new ComposedDesignator(tail, NthOutput.get(0));
				TraceabilityTree tree = sub_factory.trace(q, cd, inner_q);
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
						// Input <index> of the inner function is the <elem_index>-th element
						// of input <index> of ApplyToAll
						ComposedDesignator cd2 = new ComposedDesignator(f_dob.getDesignator().tail(), NthElement.get(elem_index),
								NthInput.get(index));
						TraceabilityNode tn = factory.getObjectNode(cd2, this);
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
	public Function duplicate(boolean with_state) 
	{
		ApplyToAll ata = new ApplyToAll(m_function.duplicate(with_state));
		return ata;
	}

	@Override
	public Function duplicate() 
	{	
		return duplicate(false);
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return evaluate(inputs, outputs, null, true);
	}
	
	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, boolean track) 
	{
		return evaluate(inputs, outputs, null, track);
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