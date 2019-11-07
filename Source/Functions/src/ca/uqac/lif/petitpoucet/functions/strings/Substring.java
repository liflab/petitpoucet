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
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

public class Substring extends UnaryFunction<String,String>
{	
	/**
	 * The start index of the substring
	 */
	protected int m_startIndex;
	
	/**
	 * The end index of the substring
	 */
	protected int m_endIndex;
	
	/**
	 * Creates a new instance of the substring function.
	 * @param start The start index of the substring
	 * @param end The end index of the substring
	 */
	public Substring(int start, int end)
	{
		super(String.class, String.class);
		m_startIndex = start;
		m_endIndex = end;
	}
		
	@Override
	public SubstringQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		String s = inputs[0].toString();
		int length = s.length();
		int start = Math.min(m_startIndex, length);
		int end = Math.min(m_endIndex, length);
		outputs[0] = s.substring(start, end);
		return new SubstringQueryable(toString(), m_startIndex, m_endIndex, length);
	}
	
	public static class SubstringQueryable extends FunctionQueryable
	{
		/**
		 * The start index of the substring
		 */
		protected int m_startIndex;
		
		/**
		 * The end index of the substring
		 */
		protected int m_endIndex;
		
		/**
		 * The length of the last evaluated string
		 */
		protected int m_length;
		
		public SubstringQueryable(String reference, int start, int end, int length)
		{
			super(reference, 1, 1);
			m_startIndex = start;
			m_endIndex = end;
			m_length = length;
		}
		
		@Override
		public SubstringQueryable duplicate(boolean with_state)
		{
			return new SubstringQueryable(m_reference, m_startIndex, m_endIndex, m_length);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			Designator top = d.peek();
			Designator tail = d.tail();
			if (top instanceof StringDesignator.Range)
			{
				StringDesignator.Range sdr = (StringDesignator.Range) top;
				int offset = Math.min(m_startIndex, m_length);
				int len = Math.min(sdr.getLength(), m_length);
				int start = sdr.getStartIndex() + offset;
				int end = offset + len;
				ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(start, end), NthInput.get(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				root.addChild(child, Quality.OVER);
				leaves.add(child);
				return leaves;
			}
			if (top instanceof Designator.Identity)
			{
				int start = Math.min(m_startIndex, m_length);
				int end = Math.min(m_endIndex, m_length);
				ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(start, end), NthInput.get(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				root.addChild(child, Quality.OVER);
				leaves.add(child);
				return leaves;
			}
			return leaves;
		}

	}
		
	@Override
	public String toString()
	{
		return "Subtring " + m_startIndex + "-" + m_endIndex;
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException 
	{
		List<Integer> list = new ArrayList<Integer>(2);
		list.add(m_startIndex);
		list.add(m_endIndex);
		return printer.print(list);
	}

	@Override
	public Substring read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		Object r_o = reader.read(o);
		if (r_o == null || !(r_o instanceof List))
		{
			throw new ReadException("Unexpected object format");
		}
		List<?> list = (List<?>) o;
		if (list.size() != 2 || !(list.get(0) instanceof Integer) || !(list.get(0) instanceof Integer))
		{
			throw new ReadException("Unexpected object format");
		}
		return new Substring((Integer) list.get(0), (Integer) list.get(1));
	}

	@Override
	public Substring duplicate(boolean with_state) 
	{
		return new Substring(m_startIndex, m_endIndex);
	}
}
