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
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.common.StringDesignator.Range;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

public class Split extends UnaryFunction<String,String>
{
	protected String m_regex;

	public Split(String regex)
	{
		super(String.class, String.class);
		m_regex = regex;
	}

	@Override
	public String toString()
	{
		return "Split on " + m_regex;
	}

	@Override
	public SplitQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		String s = inputs[0].toString();
		String[] parts = s.split(m_regex);
		List<Integer> offsets = new ArrayList<Integer>(parts.length + 1);
		List<String> l_parts = new ArrayList<String>(parts.length);
		int pos = 0;
		for (int i = 0; i < parts.length; i++)
		{
			String part = parts[i];
			l_parts.add(part);
			offsets.add(pos);
			pos += part.length();
			if (i < parts.length - 1)
			{
				pos += m_regex.length();
			}
		}
		offsets.add(pos);
		outputs[0] = l_parts;
		return new SplitQueryable(toString(), offsets);
	}

	public static class SplitQueryable extends FunctionQueryable
	{
		protected List<Integer> m_offsets;

		public SplitQueryable(String reference, List<Integer> offsets)
		{
			super(reference, 1, 1);
			m_offsets = offsets;
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			Designator top = d.peek();
			Designator tail = d.tail();
			if (!(top instanceof NthElement))
			{
				return factory.unknownLink(root);
			}
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			int pos = ((NthElement) top).getIndex();
			Designator h_tail = tail.peek();
			Designator cd;
			if (h_tail instanceof Range)
			{
				// Offset the range by the position in the input string
				Range r = (Range) h_tail;
				Designator t_tail = tail.tail();
				cd = new ComposedDesignator(t_tail, new Range(r.getStartIndex() + m_offsets.get(pos), r.getEndIndex() + m_offsets.get(pos)), NthInput.get(0));
			}
			else
			{
				cd = new ComposedDesignator(tail, new Range(m_offsets.get(pos), m_offsets.get(pos + 1) - 1), NthInput.get(0));
			}
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.EXACT);
			leaves.add(child);
			return leaves;
		}

		@Override
		public SplitQueryable duplicate(boolean with_state)
		{
			List<Integer> offsets = new ArrayList<Integer>(m_offsets.size());
			offsets.addAll(m_offsets);
			SplitQueryable s = new SplitQueryable(m_reference, offsets);
			return s;
		}
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		return printer.print(m_regex);
	}

	@Override
	public Split read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		Object r_o = reader.read(o);
		if (!(r_o instanceof String))
		{
			throw new ReadException("Unexpected object format");
		}
		return new Split((String) r_o);
	}

	@Override
	public Split duplicate(boolean with_state) 
	{
		return this;
	}
}