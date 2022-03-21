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
package ca.uqac.lif.petitpoucet.function.number;

import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * Function that compares two numbers.
 * @author Sylvain Hallé
 *
 */
public abstract class NumberComparison extends AtomicFunction
{
	/**
	 * Creates a new instance of the function.
	 */
	public NumberComparison()
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
		return new Object[] {compare(n1, n2)};
	}
	
	/**
	 * Compares two numbers.
	 * @param n1 The first number
	 * @param n2 The second number
	 * @return The result of the comparison
	 */
	protected abstract boolean compare(Number n1, Number n2);
}
