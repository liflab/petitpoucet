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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * Determines if part of a string matches a regular expression 
 * @author Sylvain Hallé
 */
public class RegexMatches extends UnaryFunction<String,Boolean>
{
	/**
	 * The pattern to find
	 */
	protected transient Pattern m_pattern;
	
	/**
	 * Creates a new instance of the function
	 * @param regex The regex to find
	 */
	public RegexMatches(String regex)
	{
		super(String.class, Boolean.class);
		m_pattern = Pattern.compile(regex);
	}

	@Override
	public RegexMatchesQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		String s = inputs[0].toString();
		Matcher mat = m_pattern.matcher(s);
		if (mat.find())
		{
			outputs[0] = true;
			int start_index = mat.start();
			int end_index = mat.end() - 1;
			return new RegexMatchesQueryable(toString(), start_index, end_index);
		}
		else
		{
			outputs[0] = false;
			return new RegexMatchesQueryable(toString(), -1, -1);
		}
	}
	
	public static class RegexMatchesQueryable extends FunctionQueryable
	{
		/**
		 * The start index of the pattern on the last string that was processed
		 */
		protected int m_startIndex = -1;
		
		/**
		 * The end index of the pattern on the last string that was processed
		 */
		protected int m_endIndex = -1;
		
		public RegexMatchesQueryable(String reference, int start_index, int end_index)
		{
			super(reference, 1, 1);
			m_startIndex = start_index;
			m_endIndex = end_index;
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			Designator top = d.peek();
			Designator tail = d.tail();
			if (top instanceof Designator.Identity)
			{
				ComposedDesignator cd;
				if (m_startIndex >= 0 && m_endIndex >= m_startIndex)
				{
					cd = new ComposedDesignator(tail, new StringDesignator.Range(m_startIndex, m_endIndex), NthInput.get(0));
				}
				else
				{
					cd =  new ComposedDesignator(tail, NthInput.get(0));
				}
				TraceabilityNode child = factory.getObjectNode(cd, this);
				root.addChild(child, Quality.OVER);
				leaves.add(child);
			}
			return leaves;
		}
		
		@Override
		public RegexMatchesQueryable duplicate(boolean with_state)
		{
			return new RegexMatchesQueryable(m_reference, m_startIndex, m_endIndex);
		}
	}
	
	@Override
	public String toString()
	{
		return "Match /" + m_pattern.pattern() + "/";
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		return printer.print(m_pattern.pattern());
	}

	@Override
	public RegexMatches read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		Object r_o = reader.read(o);
		if (!(r_o instanceof String))
		{
			throw new ReadException("Unexpected object foramt");
		}
		return new RegexMatches((String) r_o);
	}

	@Override
	public RegexMatches duplicate(boolean with_state)
	{
		return new RegexMatches(m_pattern.pattern());
	}
}
