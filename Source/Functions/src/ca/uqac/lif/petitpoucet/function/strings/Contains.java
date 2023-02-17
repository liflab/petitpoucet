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
package ca.uqac.lif.petitpoucet.function.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * Checks that a string contains another string. A call to Contains(x,y) is
 * equivalent to the Java instruction <tt>x.contains(y)</tt>.
 * @author Sylvain Hallé
 */
public class Contains extends AtomicFunction
{
	/**
	 * The list of character ranges where the pattern is found in the input
	 * string the last time the function was evaluated.
	 */
	/*@ non_null @*/ protected final List<Range> m_matches;
	
	/**
	 * Creates a new instance of the function.
	 */
	public Contains()
	{
		super(2, 1);
		m_matches = new ArrayList<Range>();
	}

	@Override
	public Contains duplicate(boolean with_state)
	{
		Contains c = new Contains();
		if (with_state)
		{
			c.m_matches.addAll(m_matches);
		}
		return c;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		m_matches.clear();
		if (!(inputs[0] instanceof String) || !(inputs[1] instanceof String))
		{
			throw new InvalidArgumentException("Expected a string");
		}
		String haystack = (String) inputs[0];
		String needle = (String) inputs[1];
		Pattern pat = Pattern.compile(needle);
		Matcher mat = pat.matcher(haystack);
		while (mat.find())
		{
			m_matches.add(new Range(mat.start(), mat.end()));
		}
		return new Object[] {!m_matches.isEmpty()};
	}
	
	@Override
	public PartNode getExplanation(Part p, RelationNodeFactory factory)
	{
		PartNode root = factory.getPartNode(p, this);
		if (m_matches.isEmpty())
		{
			root.addChild(factory.getPartNode(NthInput.FIRST, this));
			return root;
		}
		LabelledNode or = root;
		if (m_matches.size() > 1)
		{
			LabelledNode new_or = factory.getOrNode();
			root.addChild(new_or);
			or = new_or;
		}
		for (Range r : m_matches)
		{
			or.addChild(factory.getPartNode(ComposedPart.compose(r, NthInput.FIRST), this));
		}
		return root;
	}
	
	@Override
	public String toString()
	{
		return "Contains";
	}
}
