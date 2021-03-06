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
package ca.uqac.lif.petitpoucet.function.vector;

import java.util.List;

import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;

/**
 * Calculates the sum of all numerical elements in a vector.
 * 
 * @author Sylvain Hallé
 */
public class VectorSum extends VectorFunction
{
	/**
	 * Creates a new instance of the function.
	 */
	public VectorSum()
	{
		super(1);
	}
	
	@Override
	protected Number getOutputValue(List<?> ... in_lists)
	{
		float total = 0;
		for (Object o : in_lists[0])
		{
			if (!(o instanceof Number))
			{
				throw new InvalidArgumentTypeException("Expected a number");
			}
			total += ((Number) o).floatValue();
		}
		return total;
	}
	
	@Override
	public String toString()
	{
		return "Σ";
	}
	
	@Override
	public VectorSum duplicate(boolean with_state)
	{
		VectorSum vs = new VectorSum();
		copyInto(vs, with_state);
		return vs;
	}
}
