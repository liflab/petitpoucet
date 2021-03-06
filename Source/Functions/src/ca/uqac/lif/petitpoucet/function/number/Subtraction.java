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

/**
 * Subtracts from the first argument all the remaining arguments.
 * @author Sylvain Hallé
 *
 */
public class Subtraction extends AtomicFunction implements ExplanationQueryable
{
	/**
	 * Creates a new instance of the function.
	 * @param in_arity The input arity of the function
	 */
	public Subtraction(int in_arity)
	{
		super(in_arity, 1);
	}

	@Override
	public Object[] getValue(Object ... inputs)
	{
		Object[] out = new Object[1];
		float sum = ((Number) inputs[0]).floatValue();
		for (int i = 1; i < inputs.length; i++)
		{
			if (inputs[i] instanceof Number)
			{
				sum -= ((Number) inputs[i]).floatValue();
			}
		}
		out[0] = sum;
		return out;
	}

	@Override
	public String toString()
	{
		return "−";
	}
	
	@Override
	public Subtraction duplicate(boolean with_state)
	{
		Subtraction a = new Subtraction(getInputArity());
		copyInto(a, with_state);
		return a;
	}
}