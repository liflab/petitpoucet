/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

/**
 * Extracts parts of a string based on a regular expression. The function
 * returns a list of all matches.
 * @author Sylvain Hallé
 */
public class RegexFindAll extends UnaryFunction<String,String>
{
	/**
	 * The pattern to find
	 */
	protected transient Pattern m_pattern;

	protected transient RegexNotFoundQueryable m_notFoundQueryable;

	/**
	 * Creates a new instance of the function
	 * @param regex The regex to find
	 */
	public RegexFindAll(String regex)
	{
		super(String.class, String.class);
		m_pattern = Pattern.compile(regex);
		m_notFoundQueryable = new RegexNotFoundQueryable(toString());
	}

	@Override
	public RegexFindAllQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		String s = inputs[0].toString();
		int length = s.length();
		RegexFindAllQueryable q = new RegexFindAllQueryable("findAll");
		Matcher mat = m_pattern.matcher(s);
		List<String> occurrences = new ArrayList<String>();
		while (mat.find())
		{
			occurrences.add(mat.group());
			if (track)
			{
				int start_index = mat.start();
				int end_index = mat.end() - 1;
				q.addLocation(new RegexFoundQueryable(toString(), start_index, end_index, length));
			}
		}
		outputs[0] = occurrences;
		if (track)
		{
			return q;
		}
		return null;
	}

	public static class RegexFindAllQueryable extends FunctionQueryable
	{
		protected List<RegexFoundQueryable> m_locations;
		
		public RegexFindAllQueryable(String reference)
		{
			super(reference, 1, 1);
			m_locations = new ArrayList<RegexFoundQueryable>();
		}
		
		public void addLocation(RegexFoundQueryable found)
		{
			m_locations.add(found);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			Designator top = d.peek();
			Designator tail = d.tail();
			if (top instanceof Designator.Identity)
			{
				TraceabilityNode and = factory.getAndNode();
				for (RegexFoundQueryable rfq : m_locations)
				{
					List<TraceabilityNode> sub_leaves = rfq.query(TraceabilityQuery.ProvenanceQuery.instance, tail, and, factory);
					leaves.addAll(sub_leaves);
				}
			}
			return leaves;
		}
	}

	public static class RegexFoundQueryable extends FunctionQueryable
	{
		/**
		 * The start index of the pattern on the last string that was processed
		 */
		protected int m_startIndex = -1;

		/**
		 * The end index of the pattern on the last string that was processed
		 */
		protected int m_endIndex = -1;

		/**
		 * The length of the last evaluated string
		 */
		protected int m_length;

		public RegexFoundQueryable(String reference, int start_index, int end_index, int length)
		{
			super(reference, 1, 1);
			m_startIndex = start_index;
			m_endIndex = end_index;
			m_length = length;
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
			}
			else if (top instanceof Designator.Identity)
			{
				int start = Math.min(m_startIndex, m_length);
				int end = Math.min(m_endIndex, m_length);
				ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(start, end), NthInput.get(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				root.addChild(child, Quality.OVER);
				leaves.add(child);
			}
			return leaves;
		}

		@Override
		public RegexFoundQueryable duplicate(boolean with_state)
		{
			return new RegexFoundQueryable(m_reference, m_startIndex, m_endIndex, m_length);
		}
	}

	public static class RegexNotFoundQueryable extends RegexFindAllQueryable
	{
		public RegexNotFoundQueryable(String reference)
		{
			super(reference);
		}
	}

	@Override
	public String toString()
	{
		return "Find all /" + m_pattern.pattern() + "/";
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		return printer.print(m_pattern.pattern());
	}

	@Override
	public RegexFindAll read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		Object r_o = reader.read(o);
		if (!(r_o instanceof String))
		{
			throw new ReadException("Unexpected object format");
		}
		return new RegexFindAll((String) r_o);
	}

	@Override
	public Function duplicate(boolean with_state) 
	{
		return new RegexFindAll(m_pattern.pattern());
	}
}
