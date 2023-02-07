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

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * Function that checks the equality between two strings. If they are not
 * equal, it can explain the result by identifying the character ranges where
 * they differ.
 * <p>
 * The function can accept arbitrary objects for its two arguments, but will
 * return <tt>false</tt> if any of them is not a string.
 * 
 * @author Sylvain Hallé
 */
public class StringEquals extends AtomicFunction
{
	/**
	 * The list of character ranges where the two strings differ, for the last
	 * arguments given to the function.
	 */
	/*@ null @*/ protected List<Range> m_differentRanges;

	/**
	 * A range indicating characters in excess in the first argument (when its
	 * string is longer than the second argument).
	 */
	/*@ null @*/ protected Range m_excessFirst;

	/**
	 * A range indicating characters in excess in the second argument (when its
	 * string is longer than the first argument).
	 */
	/*@ null @*/ protected Range m_excessSecond;

	/**
	 * Creates a new instance of the function.
	 */
	public StringEquals()
	{
		super(2, 1);
		m_differentRanges = null;
	}

	@Override
	public StringEquals duplicate(boolean with_state)
	{
		StringEquals se = new StringEquals();
		copyInto(se, with_state);
		return se;
	}

	/**
	 * Copies the fields of the current function into another instance.
	 * @param se The instance to copy fields to
	 * @param with_state Set to <tt>true</tt> for a stateful copy, <tt>false</tt>
	 * otherwise
	 */
	protected void copyInto(StringEquals se, boolean with_state)
	{
		super.copyInto(se, with_state);
		if (with_state && m_differentRanges != null)
		{
			se.m_differentRanges = new ArrayList<Range>(m_differentRanges.size());
			se.m_differentRanges.addAll(m_differentRanges);
		}
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (!(inputs[0] instanceof String) || !(inputs[1] instanceof String))
		{
			return new Object[] {false};
		}
		String s1 = (String) inputs[0];
		String s2 = (String) inputs[1];
		m_excessFirst = null;
		m_excessSecond = null;
		if (s1.compareTo(s2) == 0)
		{
			m_differentRanges = null;
			return new Object[] {true};
		}
		m_differentRanges = new ArrayList<Range>();
		int start = -1;
		int min_len = Math.min(s1.length(), s2.length());
		for (int i = 0; i < min_len; i++)
		{
			String c1 = s1.substring(i, i + 1);
			String c2 = s2.substring(i, i + 1);
			if (c1.compareTo(c2) == 0)
			{
				if (start >= 0)
				{
					m_differentRanges.add(new Range(start, i - 1));
					start = -1;
				}
			}
			else
			{
				if (start < 0)
				{
					start = i;
				}
			}
		}
		if (start >= 0)
		{
			m_differentRanges.add(new Range(start, min_len - 1));
		}
		if (s1.length() > s2.length())
		{
			m_excessFirst = new Range(min_len, s1.length() - 1);
		}
		if (s2.length() > s1.length())
		{
			m_excessSecond = new Range(min_len, s2.length() - 1);
		}
		return new Object[] {false};
	}
	
	@Override
	public PartNode getExplanation(Part p, RelationNodeFactory factory)
	{
		int num_ranges = 0;
		if (m_differentRanges != null)
		{
			num_ranges += m_differentRanges.size();
		}
		if (m_excessFirst != null)
		{
			num_ranges++;
		}
		if (m_excessSecond != null)
		{
			num_ranges++;
		}
		if (num_ranges == 0)
		{
			return super.getExplanation(p, factory);
		}
		PartNode root = factory.getPartNode(p, this);
		if (NthOutput.mentionedOutput(p) != 0)
		{
			root.addChild(factory.getUnknownNode());
			return root;
		}
		LabelledNode or = root;
		if (num_ranges > 1)
		{
			LabelledNode n_or = factory.getOrNode();
			root.addChild(n_or);
			or = n_or;
		}
		if (m_differentRanges != null)
		{
			for (Range r : m_differentRanges)
			{
				LabelledNode in_and = factory.getAndNode();
				in_and.addChild(factory.getPartNode(ComposedPart.compose(r, NthInput.FIRST), this));
				in_and.addChild(factory.getPartNode(ComposedPart.compose(r, NthInput.SECOND), this));
				or.addChild(in_and);
			}
		}
		if (m_excessFirst != null)
		{
			or.addChild(factory.getPartNode(ComposedPart.compose(m_excessFirst, NthInput.FIRST), this));
		}
		if (m_excessSecond != null)
		{
			or.addChild(factory.getPartNode(ComposedPart.compose(m_excessSecond, NthInput.SECOND), this));
		}
		return root;
	}

	@Override
	public String toString()
	{
		return "=";
	}
}