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
package ca.uqac.lif.petitpoucet.function.booleans;

import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * Function implementing logical conjunction.
 * @author Sylvain Hallé
 */
public class And extends BooleanConnective
{
	/**
	 * Creates a new instance of the "and" connective.
	 * @param in_arity The input arity of the connective
	 * @param fail_fast Set to {@code true} to make it a fail-fast connective
	 */
	public And(int in_arity, boolean fail_fast)
	{
		super(in_arity, false, fail_fast);
	}
	
	/**
	 * Creates a new instance of the "and" connective.
	 * @param in_arity The input arity of the connective
	 */
	public And(int in_arity)
	{
		this(in_arity, false);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		boolean value = true;
		for (int i = 0; i < inputs.length; i++)
		{
			if (!(inputs[i] instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Expected a Boolean");
			}
			m_arguments[i] = (Boolean) inputs[i];
			if (!m_arguments[i])
			{
				value = false;
				if (m_failFast)
				{
					break;
				}
			}
		}
		return new Object[] {value};
	}
	
	@Override
	public And duplicate(boolean with_state)
	{
		And a = new And(getInputArity(), m_failFast);
		copyInto(a, with_state);
		return a;
	}
	
	@Override
	public String toString()
	{
		return "∧";
	}
}
