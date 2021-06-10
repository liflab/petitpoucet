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
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * Converts an object into a number.
 * @author Sylvain Hallé
 */
public class NumberCast extends AtomicFunction
{
	public NumberCast()
	{
		super(1, 1);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		Number n = 0;
		if (inputs[0] instanceof Number)
		{
			n = (Number) inputs[0];
		}
		else if (inputs[0] instanceof String)
		{
			try
			{
				n = Float.parseFloat((String) inputs[0]);
			}
			catch (NumberFormatException e)
			{
				// Do nothing
			}
		}
		return new Object[] {n};
	}
	
	@Override
	public NumberCast duplicate(boolean with_state)
	{
		NumberCast nc = new NumberCast();
		copyInto(nc, with_state);
		return nc;
	}
	
	@Override
	public String toString()
	{
		return "#";
	}
}