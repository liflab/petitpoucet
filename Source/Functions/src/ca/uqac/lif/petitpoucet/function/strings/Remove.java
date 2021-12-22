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
 * Removes a substring from an input string, based on a start and an
 * end index. This function can be seen as the opposite of {@link Substring}.
 */
public class Remove extends RangeStringMappingFunction
{	
	/**
	 * Creates a new instance of the function.
	 * @param start The start index of the range
	 * @param end The end index of the range
	 */
	public Remove(int start, int end)
	{
		super(start, end);
	}

	@Override
	protected String applyOnRange(String s, int start, int end)
	{
		StringBuilder out = new StringBuilder();
		if (start > 0)
		{
			m_mapping.add(new Range(0, start - 1), new Range(0, start - 1));
			out.append(s.substring(0, start));
		}
		if (end < s.length())
		{
			int remaining = s.length() - end - 1;
			m_mapping.add(new Range(end, end + remaining), new Range(start, start + remaining));
			out.append(s.substring(end));
		}
		return out.toString();
	}
	
	@Override
	public Remove duplicate(boolean with_state)
	{
		Remove r = new Remove(m_start, m_end);
		copyInto(r, with_state);
		return r;
	}
	
	@Override
	public String toString()
	{
		return "Remove " + m_start + "-" + m_end;
	}
}
