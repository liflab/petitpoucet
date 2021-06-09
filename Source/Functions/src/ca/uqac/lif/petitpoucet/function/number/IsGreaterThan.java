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
package ca.uqac.lif.petitpoucet.function.number;

import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * Determines if a number is greater than another one.
 * @author Sylvain Hallé
 */
public class IsGreaterThan extends AtomicFunction
{
	/**
	 * Creates a new instance of the function.
	 */
	public IsGreaterThan()
	{
		super(2, 1);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (!(inputs[0] instanceof Number) || !(inputs[1] instanceof Number))
		{
			throw new InvalidArgumentTypeException("Expected a number");
		}
		Number n1 = (Number) inputs[0];
		Number n2 = (Number) inputs[1];
		return new Object[] {n1.floatValue() > n2.floatValue()};
	}
	
	@Override
	public String toString()
	{
		return "&gt;";
	}
	
	@Override
	public IsGreaterThan duplicate(boolean with_state)
	{
		IsGreaterThan f = new IsGreaterThan();
		copyInto(f, with_state);
		return f;
	}
}
