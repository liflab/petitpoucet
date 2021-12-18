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
import ca.uqac.lif.petitpoucet.Part;

/**
 * Part representing a contiguous sequence of characters in a string.
 * @author Sylvain Hallé
 */
public class Range implements Part, Comparable<Range>
{
	/**
	 * The start position of the range.
	 */
	protected int m_startIndex;
	
	/**
	 * The end position of the range.
	 */
	protected int m_endIndex;
	
	/**
	 * Creates a new range.
	 * @param start The start position of the range
	 * @param end The end position of the range
	 */
	public Range(int start, int end)
	{
		super();
		m_startIndex = start;
		m_endIndex = end;
	}
	
	/**
	 * Gets the start index of the range.
	 * @return The start index
	 */
	/*@ pure @*/ public int getStart()
	{
		return m_startIndex;
	}
	
	/**
	 * Gets the end index of the range.
	 * @return The end index
	 */
	/*@ pure @*/ public int getEnd()
	{
		return m_endIndex;
	}
	
	/**
	 * Gets the length of the range.
	 * @return The length
	 */
	/*@ pure @*/ public int length()
	{
		return m_endIndex - m_startIndex;
	}
	
	/**
	 * Creates a range from the current one by shfiting its positions by a fixed
	 * offset.
	 * @param offset The offset; may be positive or negative
	 * @return The new range
	 */
	/*@ pure non_null @*/ public Range shift(int offset)
	{
		return new Range(m_startIndex + offset, m_endIndex + offset);
	}
	
	/**
	 * Determines if the current range overlaps with another one.
	 * @param r The other range
	 * @return <tt>true</tt> if it overlaps, <tt>false</tt> otherwrise
	 */
	/*@ pure @*/ public boolean overlaps(/*@ non_null @*/ Range r)
	{
		return !(r.getEnd() < m_startIndex || m_endIndex < r.getStart());
	}
	
	/**
	 * Intersects a range with the current one.
	 * @param r The other range, may be null
	 * @return A new range representing the intersection, or <tt>null</tt> if
	 * the two ranges are disjoint
	 */
	/*@ pure null @*/ public Range intersect(/*@ null @*/ Range r)
	{
		if (r == null || !overlaps(r))
		{
			return null;
		}
		return new Range(Math.max(m_startIndex, r.getStart()), Math.min(m_endIndex, r.getEnd()));
	}
	
	@Override
	public boolean appliesTo(Object o)
	{
		return o instanceof String;
	}

	@Override
	public Part head()
	{
		return this;
	}

	@Override
	public Part tail()
	{
		return null;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Range))
		{
			return false;
		}
		Range r = (Range) o;
		return r.m_startIndex == m_startIndex && r.m_endIndex == m_endIndex;
	}
	
	@Override
	public int hashCode()
	{
		return m_startIndex + m_endIndex;
	}
	
	@Override
	public String toString()
	{
		if (m_startIndex < m_endIndex)
		{
			return "I" + m_startIndex + "-" + m_endIndex;
		}
		return "I" + m_startIndex;
	}
	
	@Override
	public int compareTo(Range r)
	{
		if (m_startIndex < r.m_startIndex)
		{
			return -1;
		}
		if (m_startIndex == r.m_startIndex)
		{
			if (m_endIndex < r.m_endIndex)
			{
				return -1;
			}
			return 0;
		}
		return 1;
	}
	
	/**
	 * Retrieves the range of the output string mentioned in a designator.
	 * If multiple {@link Range}s are present, the one closest to the
	 * designator mentioning the function's output is kept.
	 * @param d The designator
	 * @return The range, or null if no specific range is mentioned
	 */
	/*@ null @*/ public static Range mentionedRange(Part d)
	{
		Range r = null;
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = cd.size() - 1; i >= 0; i--)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof Range)
				{
					r = (Range) in_d;
				}
			}
		}
		return r;
	}
	
	/**
	 * Removes the range in a designator.
	 * If multiple {@link Range}s are present, the one closest to the
	 * designator mentioning the function's input or output is kept.
	 * @param d The designator
	 * @return The new designator with range deleted
	 */
	public static Part removeRange(Part d)
	{
		List<Part> parts = new ArrayList<Part>();
		boolean removed = false;
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = cd.size() - 1; i >= 0; i--)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof Range && !removed)
				{
					removed = true;
				}
				else
				{
					parts.add(0, in_d);
				}
			}
		}
		return ComposedPart.compose(parts);
	}
}
