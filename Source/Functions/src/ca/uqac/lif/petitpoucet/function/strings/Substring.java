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

/**
 * Extracts a substring out of an input string, based on a start and an
 * end index.
 */
public class Substring extends StringMappingFunction
{
	/**
	 * The start index of the range.
	 */
	protected final int m_start;
	
	/**
	 * The end index of the range.
	 */
	protected final int m_end;
	
	/**
	 * Creates a new instance of the function.
	 * @param start The start index of the range
	 * @param end The end index of the range
	 */
	public Substring(int start, int end)
	{
		super();
		m_start = start;
		m_end = end;
	}

	@Override
	protected String transformString(String s)
	{
		if (s.length() < m_start)
		{
			return "";
		}
		int end = Math.min(m_end,  s.length());
		m_mapping.add(new Range(m_start, end - 1), new Range(0, end - m_start - 1));
		return s.substring(m_start, end);
	}
	
	@Override
	public Substring duplicate(boolean with_state)
	{
		Substring r = new Substring(m_start, m_end);
		copyInto(r, with_state);
		return r;
	}
	
	@Override
	public String toString()
	{
		return "Substring " + m_start + "-" + m_end;
	}
}
