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
 * Designator related to the n-th input of a function.
 */
public class NthInput implements Part
{
	/**
	 * A static reference to an instance of "first input"
	 */
	public static final transient NthInput FIRST = new NthInput(0);
	
	/**
	 * A static reference to an instance of "second input"
	 */
	public static final transient NthInput SECOND = new NthInput(1);
	
	/**
	 * A static reference to an instance of "third input"
	 */
	public static final transient NthInput THIRD = new NthInput(2);
	
	/**
	 * The index of the function's input.
	 */
	private final int m_index;
	
	/**
	 * Creates a new designator instance.
	 * @param index The index of the function's input
	 */
	public NthInput(int index)
	{
		super();
		m_index = index;
	}
	
	/**
	 * Gets the input index.
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
	public NthInput head()
	{
		return this;
	}

	@Override
	public Part tail()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return "↑" + getSubscript();
	}
	
	@Override
	public int hashCode()
	{
		return m_index;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof NthInput)
		{
			return ((NthInput) o).m_index == m_index;
		}
		return false;
	}
	
	/**
	 * Retrieves the input pin index mentioned in a designator.
	 * @param d The designator
	 * @return The input pin index, or -1 if no input pin is mentioned
	 */
	public static int mentionedInput(Part d)
	{
		if (d instanceof NthInput)
		{
			return ((NthInput) d).getIndex();
		}
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = 0; i < cd.size(); i++)
			{
				Part p = cd.get(i);
				if (p instanceof NthInput)
				{
					return ((NthInput) p).getIndex();
				}
			}
		}
		return -1;
	}
	
	/**
	 * Given an arbitrary designator, replaces the first occurrence of
	 * {@link NthInput} by an instance of {@link NthOutput} with given index.
	 * @param d The designator
	 * @param index The index
	 * @return The new designator. The input object is not modified if it does
	 * not contain {@code d}
	 */
	/*@ non_null @*/ public static Part replaceInByOut(/*@ non_null @*/ Part d, int index)
	{
		return replaceInBy(d, new NthOutput(index));
	}
	
	/**
	 * Given an arbitrary designator, replaces the first occurrence of
	 * {@link NthInput} by another part.
	 * @param from The original part
	 * @param to The part to replace it with
	 * @return The new designator. The input object is not modified if it does
	 * not contain {@code d}
	 */
	/*@ non_null @*/ public static Part replaceInBy(/*@ non_null @*/ Part from, Part to)
	{
		if (from instanceof NthInput)
		{
			return to;
		}
		if (from instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) from;
			List<Part> desigs = new ArrayList<>();
			boolean replaced = false;
			for (int i = 0 ; i < cd.size(); i++)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof NthInput && !replaced)
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
	
	protected String getSubscript()
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