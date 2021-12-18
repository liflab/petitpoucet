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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds a regex pattern in a character string and replaces it by another.
 * @author Sylvain Hallé
 */
public class Replace extends StringMappingFunction
{
	/**
	 * The pattern to look for in the string.
	 */
	/*@ non_null @*/ protected final String m_from;

	/**
	 * The replacement pattern.
	 */
	/*@ non_null @*/ protected final String m_to;
	
	/**
	 * A flag set to <tt>true</tt> to replace all matches, <tt>false</tt> to
	 * replace only the first match.
	 */
	protected boolean m_all;
	
	/**
	 * The start position where the replacements should be done.
	 */
	protected int m_startPos;

	/**
	 * Creates a new instance of the function.
	 * @param from The pattern to look for in the string
	 * @param to The replacement pattern
	 * @param all Set to <tt>true</tt> to replace all matches, <tt>false</tt> to
	 * replace only the first match
	 * @param start_pos The start position where the replacements should be done
	 */
	public Replace(/*@ non_null @*/ String from, /*@ non_null @*/ String to, boolean all, int start_pos)
	{
		super();
		m_from = from;
		m_to = to;
		m_all = all;
		m_startPos = start_pos;
	}
	
	/**
	 * Creates a new instance of the function, instructing it to replace all
	 * matches.
	 * @param from The pattern to look for in the string
	 * @param to The replacement pattern
	 */
	public Replace(/*@ non_null @*/ String from, /*@ non_null @*/ String to)
	{
		this(from, to, true, 0);
	}

	@Override
	protected String transformString(String input)
	{
		int pos = 0, out_len = 0;
		Pattern pat = Pattern.compile(m_from);
		Matcher mat = pat.matcher(input);
		StringBuilder output = new StringBuilder();
		while (mat.find(Math.max(pos, m_startPos)))
		{
			int index = mat.start();
			if (index > pos)
			{
				output.append(input.substring(pos, index));
				m_mapping.add(new Range(pos, index - 1), new Range(out_len, out_len + (index - pos) - 1));
				out_len += index - pos;
			}
			String matched = mat.group();
			String replacement = m_to;
			for (int g = 1; g <= mat.groupCount(); g++)
			{
				replacement = replacement.replaceAll("\\$" + g, mat.group(g));
			}
			if (!replacement.isEmpty())
			{
				m_mapping.add(new Range(index, index + matched.length() - 1), new Range(out_len, out_len + replacement.length() - 1));
				output.append(replacement);
				out_len += replacement.length();
			}
			pos = index + matched.length();
		}
		if (pos < input.length())
		{
			int remaining = input.length() - pos;
			output.append(input.substring(pos));
			m_mapping.add(new Range(pos, pos + remaining - 1), new Range(out_len, out_len + remaining - 1));
		}
		return output.toString();
	}

	@Override
	public Replace duplicate(boolean with_state)
	{
		Replace r = new Replace(m_from, m_to);
		copyInto(r, with_state);
		return r;
	}

	@Override
	public String toString()
	{
		return "Replace " + m_from + " by " + m_to;
	}
}
