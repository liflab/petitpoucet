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
package ca.uqac.lif.petitpoucet.function.reflect;

import ca.uqac.lif.petitpoucet.Part;

/**
 * Part that designates a member field of some object.
 * 
 * @author Sylvain Hallé
 */
public class Field implements Part, Comparable<Field>
{
	/**
	 * The name of the field.
	 */
	/*@ non_null @*/ protected final String m_name;
	
	/**
	 * Creates a new instance of the par.
	 * @param name The name of the field
	 */
	public Field(/*@ non_null @*/ String name)
	{
		super();
		m_name = name;
	}
	
	/**
	 * Gets the name of the field.
	 * @return The name
	 */
	/*@ pure non_null @*/ public String getName()
	{
		return m_name;
	}
	
	@Override
	public boolean appliesTo(Object o)
	{
		return o != null;
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
	public String toString()
	{
		return "Field " + m_name;
	}
	
	@Override
	public int hashCode()
	{
		return m_name.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Field))
		{
			return false;
		}
		return m_name.compareTo(((Field) o).m_name) == 0;
	}

	@Override
	public int compareTo(Field f)
	{
		return m_name.compareTo(f.m_name);
	}
}
