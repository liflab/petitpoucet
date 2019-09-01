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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.StringDesignator;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

/**
 * Determines if part of a string matches a regular expression 
 * @author Sylvain Hallé
 */
public class RegexMatches extends NaryFunction
{
	/**
	 * The pattern to find
	 */
	protected transient Pattern m_pattern;
	
	/**
	 * The start index of the pattern on the last string that was processed
	 */
	protected int m_startIndex = -1;
	
	/**
	 * The end index of the pattern on the last string that was processed
	 */
	protected int m_endIndex = -1;
	
	/**
	 * Creates a new instance of the function
	 * @param regex The regex to find
	 */
	public RegexMatches(String regex)
	{
		super(1);
		m_pattern = Pattern.compile(regex);
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		String s = inputs[0].toString();
		Matcher mat = m_pattern.matcher(s);
		if (mat.find())
		{
			outputs[0] = true;
			m_startIndex = mat.start();
			m_endIndex = mat.end() - 1;
		}
		else
		{
			outputs[0] = false;
		}
		m_returnedValue[0] = outputs[0];
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
		if (!m_evaluated)
		{
			// We did not evaluate the function; nothing to say
			TraceabilityNode child = factory.getUnknownNode();
			root.addChild(child, Quality.EXACT);
			leaves.add(child);
			return;
		}
		if (top instanceof Designator.Identity)
		{
			ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(m_startIndex, m_endIndex), new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.OVER);
			leaves.add(child);
			return;
		}
	}
	
	@Override
	public String toString()
	{
		return "Match /" + m_pattern.pattern() + "/";
	}
}
