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
package ca.uqac.lif.petitpoucet.function;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;

/**
 * Designator related to the n-th output of a function.
 */
public class NthOutput implements Part
{
	/**
	 * A static reference to an instance of "first output"
	 */
	public static final transient NthOutput FIRST = new NthOutput(0);
	
	/**
	 * A static reference to an instance of "second output"
	 */
	public static final transient NthOutput SECOND = new NthOutput(1);
	
	/**
	 * The index of the function's input.
	 */
	private final int m_index;

	/**
	 * Creates a new designator instance.
	 * @param index The index of the function's output
	 */
	public NthOutput(int index)
	{
		super();
		m_index = index;
	}
	
	/**
	 * Gets the output index.
	 * @return The index
	 */
	public int getIndex()
	{
		return m_index;
	}

	@Override
	public boolean appliesTo(Object o)
	{
		return o instanceof Function;
	}

	@Override
	public Part head()
	{
		return this;
	}

	@Override
	public Part tail()
	{
		return Part.nothing;
	}

	@Override
	public String toString()
	{
		return "↓" + getSuperscript();
	}

	@Override
	public int hashCode()
	{
		return m_index;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof NthOutput)
		{
			return ((NthOutput) o).m_index == m_index;
		}
		return false;
	}
	
	/**
	 * Given an arbitrary designator, replaces the first occurrence of
	 * {@link NthOutput} by an instance of {@link NthInput} with given index.
	 * @param d The designator
	 * @param index The index
	 * @return The new designator. The input object is not modified if it does
	 * not contain {@code d}
	 */
	/*@ non_null @*/ public static Part replaceOutByIn(/*@ non_null @*/ Part d, int index)
	{
		return replaceOutBy(d, new NthInput(index));
	}
	
	/**
	 * Given an arbitrary designator, replaces the first occurrence of
	 * {@link NthOutput} by an instance of {@link NthInput} with given index.
	 * @param from The original part
	 * @param to The part to replace it with
	 * @return The new designator. The input object is not modified if it does
	 * not contain {@code d}
	 */
	/*@ non_null @*/ public static Part replaceOutBy(/*@ non_null @*/ Part from, Part to)
	{
		if (from instanceof NthOutput)
		{
			return to;
		}
		if (from instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) from;
			List<Part> desigs = new ArrayList<Part>();
			boolean replaced = false;
			for (int i = 0 ; i < cd.size(); i++)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof NthOutput && !replaced)
				{
					desigs.add(to);
					replaced = true;
				}
				else
				{
					desigs.add(in_d);
				}
			}
			if (!replaced)
			{
				// Return input object if no replacement was done
				return from;
			}
			return new ComposedPart(desigs);
		}
		return from;
	}
	
	/**
	 * Retrieves the output pin index mentioned in a designator.
	 * @param d The designator
	 * @return The output pin index, or -1 if no output pin is mentioned
	 */
	public static int mentionedOutput(Part d)
	{
		if (d instanceof NthOutput)
		{
			return ((NthOutput) d).getIndex();
		}
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = 0; i < cd.size(); i++)
			{
				Part p = cd.get(i);
				if (p instanceof NthOutput)
				{
					return ((NthOutput) p).getIndex();
				}
			}
		}
		return -1;
	}
	
	protected String getSuperscript()
	{
		switch (m_index)
		{
		case 0:
			return "\u2080";
		case 1:
			return "\u2081";
		case 2:
			return "\u2082";
		case 3:
			return "\u2083";
		case 4:
			return "\u2084";
		case 5:
			return "\u2085";
		case 6:
			return "\u2086";
		case 7:
			return "\u2087";
		case 8:
			return "\u2088";
		case 9:
			return "\u2089";
		default:
			return "" + m_index;
		}
	}
}