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

import ca.uqac.lif.petitpoucet.Part;

/**
 * Part representing a contiguous sequence of characters in a string.
 * @author Sylvain Hallé
 */
public class Range implements Part
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
	public String toString()
	{
		return "C" + m_startIndex + "-" + m_endIndex;
	}
}
