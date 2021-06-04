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

import ca.uqac.lif.petitpoucet.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;

public class Addition extends AtomicFunction implements ExplanationQueryable
{
	public Addition(int in_arity)
	{
		super(in_arity, 1);
	}

	@Override
	public Object[] getValue(Object ... inputs)
	{
		Object[] out = new Object[1];
		float sum = 0;
		for (Object o : inputs)
		{
			if (o instanceof Number)
			{
				sum += ((Number) o).floatValue();
			}
		}
		out[0] = sum;
		return out;
	}

	@Override
	public String toString()
	{
		return "+";
	}
}