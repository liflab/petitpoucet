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
package ca.uqac.lif.petitpoucet.functions.strings;

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
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.common.StringDesignator;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;

public class Concatenate implements Function
{	
	protected int m_arity;

	public Concatenate(int in_arity)
	{
		super();
		m_arity = in_arity;
	}

	public Concatenate()
	{
		this(2);
	}
	
	@Override
	public int size()
	{
		return 1;
	}

	@Override
	public ConcatenateQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		int[] borders = new int[inputs.length];
		int[] lengths = new int[inputs.length];
		String out = "";
		int len = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			String s = "";
			if (inputs[i] != null)
			{
				s = inputs[i].toString();
			}
			out += s;
			if (track)
			{
				len += s.length();
				borders[i] = len;
				lengths[i] = s.length();
			}
		}
		outputs[0] = out;
		if (track)
		{
			return new ConcatenateQueryable(toString(), borders, lengths);
		}
		return null;
	}

	@Override
	public String toString()
	{
		return ".";
	}

	public static class ConcatenateQueryable extends FunctionQueryable
	{
		/*@ non_null @*/ protected int[] m_borders;

		/*@ non_null @*/ protected int[] m_lengths;

		public ConcatenateQueryable(/*@ non_null @*/ String reference, /*@ non_null @*/ int[] borders, /*@ non_null @*/ int[] lengths)
		{
			super(reference, borders.length, 1);
			m_borders = borders;
			m_lengths = lengths;
		}

		@Override
		public ConcatenateQueryable duplicate(boolean with_state)
		{
			int[] borders = new int[m_borders.length];
			for (int i = 0; i < m_borders.length; i++)
			{
				borders[i] = m_borders[i];
			}
			int[] lengths = new int[m_lengths.length];
			for (int i = 0; i < m_lengths.length; i++)
			{
				lengths[i] = m_lengths[i];
			}
			return new ConcatenateQueryable(m_reference, borders, lengths);
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			Designator top = d.peek();
			Designator tail = d.tail();
			if (!(top instanceof StringDesignator.Range))
			{
				return allInputsLink(output_nb, d, root, factory);
			}
			int start = ((StringDesignator.Range) top).getStartIndex();
			int end = ((StringDesignator.Range) top).getEndIndex();
			int last = 0;
			TraceabilityNode and = factory.getAndNode();
			for (int i = 0; i < m_borders.length; i++)
			{
				int cur = m_borders[i];
				if (start <= cur && end >= last)
				{
					int range_start = Math.max(0, start - last);
					int range_end = Math.max(0, Math.min(m_lengths[i] - 1, m_lengths[i] + end - cur - 1));
					ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(range_start, range_end), NthInput.get(i));
					TraceabilityNode tn = factory.getObjectNode(cd, this);
					and.addChild(tn, Quality.EXACT);
					leaves.add(root);
				}
				last = cur;
			}
			root.addChild(and, Quality.EXACT);
			return leaves;
		}
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException 
	{
		return printer.print(m_arity);
	}

	@Override
	public Concatenate read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		Object r_o = reader.read(o);
		if (!(r_o instanceof Integer))
		{
			throw new ReadException("Unexpected object format");
		}
		return new Concatenate((Integer) r_o);
	}

	@Override
	public Function duplicate(boolean with_state) 
	{
		return this;
	}

	@Override
	public Function duplicate() 
	{
		return duplicate(false);
	}

	@Override
	public ConcatenateQueryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return (ConcatenateQueryable) evaluate(inputs, outputs, null, true);
	}
	
	@Override
	public ConcatenateQueryable evaluate(Object[] inputs, Object[] outputs, boolean track) 
	{
		return (ConcatenateQueryable) evaluate(inputs, outputs, null, track);
	}
	
	@Override
	public ConcatenateQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
	{
		return (ConcatenateQueryable) evaluate(inputs, outputs, c, true);
	}

	@Override
	public Class<?> getInputType(int index)
	{
		return String.class;
	}

	@Override
	public Class<?> getOutputType(int index)
	{
		if (index == 0)
		{
			return String.class;
		}
		return null;
	}

	@Override
	public int getInputArity() 
	{
		return m_arity;
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
