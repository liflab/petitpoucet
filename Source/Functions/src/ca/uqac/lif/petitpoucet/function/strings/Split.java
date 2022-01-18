/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

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

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * Splits a string into parts according to a regular expression.
 * @author Sylvain Hallé
 */
public class Split extends AtomicFunction
{
	/**
	 * The regular expression used to split the string.
	 */
	/*@ non_null @*/ protected String m_regex;

	/**
	 * The list of positions of the first character in each of the parts
	 * calculated the last time the function was called.
	 */
	/*@ non_null @*/ protected List<Integer> m_offsets;

	/**
	 * The list of string parts calculated the last time the function was
	 * called.
	 */
	/*@ non_null @*/ protected List<String> m_parts;

	/**
	 * Creates a new split function.
	 * @param regex The regular expression used to split the string
	 */
	public Split(/*@ non_null @*/ String regex)
	{
		super(1, 1);
		m_regex = regex;
		m_offsets = new ArrayList<>();
		m_parts = new ArrayList<>();
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		String s = inputs[0].toString();
		String[] parts = s.split(m_regex);
		List<String> out_parts = new ArrayList<>(parts.length);
		int pos = 0;
		for (int i = 0; i < parts.length; i++)
		{
			String part = parts[i];
			out_parts.add(part);
			m_offsets.add(pos);
			pos += part.length();
			if (i < parts.length - 1)
			{
				pos += m_regex.length();
			}
		}
		m_offsets.add(pos);
		m_parts.clear();
		m_parts.addAll(out_parts);
		return new Object[] {out_parts};
	}
	
	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		if (NthOutput.mentionedOutput(d) != 0)
		{
			return root;
		}
		int pos = NthElement.mentionedElement(d);
		if (pos < 0)
		{
			// No specific element
			return super.getExplanation(d);
		}
		Part new_p = replaceNthOutputByRange(d, new Range(m_offsets.get(pos), m_offsets.get(pos) + m_parts.get(pos).length() - 1));
		root.addChild(factory.getPartNode(new_p, this));
		return root;
	}
	
	@Override
	public Split duplicate(boolean with_state)
	{
		Split s = new Split(m_regex);
		copyInto(s, with_state);
		return s;
	}
	
	protected void copyInto(Split s, boolean with_state)
	{
		super.copyInto(s, with_state);
		if (with_state)
		{
			s.m_offsets.addAll(m_offsets);
			s.m_parts.addAll(m_parts);
		}
	}
	
	@Override
	public String toString()
	{
		return "/" + m_regex;
	}
	
	/**
	 * Creates a new designator by replacing the sequence "m-th element of output 0"
	 * by "range of input 0" inside a composed designator. 
	 * @param d The part
	 * @param r The range to insert in the output part
	 * @return The new part
	 */
	public static Part replaceNthOutputByRange(Part d, Range r)
	{
		if (!(d instanceof ComposedPart))
		{
			return d; // Nothing to do
		}
		ComposedPart cd = (ComposedPart) d;
		List<Part> desigs = new ArrayList<>();
		for (int i = 0; i < cd.size(); i++)
		{
			desigs.add(cd.get(i));
		}
		for (int i = desigs.size() - 1; i >= 1; i--)
		{
			if (desigs.get(i) instanceof NthOutput && desigs.get(i - 1) instanceof NthElement)
			{
				desigs.set(i, NthInput.FIRST);
				desigs.set(i - 1, r);
			}
		}
		return new ComposedPart(desigs);
	}
}
