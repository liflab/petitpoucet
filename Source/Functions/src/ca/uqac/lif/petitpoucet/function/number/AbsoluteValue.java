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
import ca.uqac.lif.petitpoucet.function.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;

/**
 * Calculates the absolute value of a number.
 * @author Sylvain Hallé
 */
public class AbsoluteValue extends AtomicFunction implements ExplanationQueryable
{
	/**
	 * Creates a new instance of the function.
	 */
	public AbsoluteValue()
	{
		super(1, 1);
	}

	@Override
	public Object[] getValue(Object ... inputs)
	{
		if (!(inputs[0] instanceof Number))
		{
			throw new InvalidArgumentTypeException("Expected a number");
		}
		return new Object[] {Math.abs(((Number) inputs[0]).floatValue())};
	}

	@Override
	public String toString()
	{
		return "ABS";
	}
	
	@Override
	public AbsoluteValue duplicate(boolean with_state)
	{
		AbsoluteValue f = new AbsoluteValue();
		copyInto(f, with_state);
		return f;
	}
}