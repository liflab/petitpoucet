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
package ca.uqac.lif.petitpoucet.common;

import ca.uqac.lif.petitpoucet.Designator;

public abstract class StringDesignator implements Designator 
{
	@Override
	public final boolean appliesTo(Object o)
	{
		return o != null && o instanceof String;
	}
	
	/**
	 * Designates the n-th line of a character string.
	 */
	public static class NthLine extends NthOf
	{
		/**
		 * Creates a new instance of the designator
		 * @param index The number of the line to designate
		 */
		public NthLine(int index)
		{
			super(index);
		}
		
		@Override
		public String toString()
		{
			return super.toString() + " line";
		}

		@Override
		public boolean appliesTo(Object o) 
		{
			return o != null && o instanceof String;
		}
	}
	
	/**
	 * Designates a contiguous range of characters in a string.
	 */
	public static class Range extends StringDesignator
	{
		/**
		 * The start index
		 */
		protected int m_startIndex;
		
		/**
		 * The end index
		 */
		protected int m_endIndex;
		
		/**
		 * Creates a new instance of the designator
		 * @param index The number of the line to designate
		 */
		public Range(int start, int end)
		{
			super();
			m_startIndex = start;
			m_endIndex = end;
		}
		
		/**
		 * Gets the start index
		 * @return The index
		 */
		public int getStartIndex()
		{
			return m_startIndex;
		}
		
		/**
		 * Gets the end index
		 * @return The index
		 */
		public int getEndIndex()
		{
			return m_endIndex;
		}
		
		@Override
		public String toString()
		{
			return "From " + m_startIndex + " to " + m_endIndex;
		}
	}
}
