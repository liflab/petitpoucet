/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
 * Part designating the return value produced by a call to a method on a Java
 * object.
 * @author Sylvain Hallé
 */
public class ReturnValue implements Part
{
	/**
	 * The method name whose return value is designated by this part.
	 */
	/*@ non_null @*/ protected final String m_methodName;
	
	/**
	 * Creates a new instance of the part.
	 * @param method_name The method name whose return value is designated by
	 * this part
	 */
	public ReturnValue(/*@ non_null @*/ String method_name)
	{
		super();
		m_methodName = method_name;
	}
	
	@Override
	public boolean appliesTo(Object o)
	{
		return true;
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
		return "Return value of " + m_methodName;
	}
	
	@Override
	public int hashCode()
	{
		return m_methodName.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ReturnValue))
		{
			return false;
		}
		return m_methodName.compareTo(((ReturnValue) o).m_methodName) == 0;
	}
}
