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
package ca.uqac.lif.petitpoucet.function.reflect;

import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * Function that checks if an object is an instance of a given class.
 * @author Sylvain Hallé
 */
public class InstanceOf extends AtomicFunction
{
	/**
	 * The class to check.
	 */
	/*@ non_null @*/ protected Class<?> m_class;
	
	/**
	 * Creates a new instance of the function.
	 * @param clazz The class to check
	 */
	public InstanceOf(/*@ non_null @*/ Class<?> clazz)
	{
		super(1, 1);
		m_class = clazz;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		Object o = inputs[0];
		if (o == null)
		{
			return new Object[] {false};
		}
		else
		{
			return new Object[] {m_class.isInstance(o)};
		}
	}
	
	@Override
	public InstanceOf duplicate(boolean with_state)
	{
		InstanceOf i = new InstanceOf(m_class);
		copyInto(i, with_state);
		return i;
	}
	
	@Override
	public String toString()
	{
		return "InstanceOf";
	}
}
