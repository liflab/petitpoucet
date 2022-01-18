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
 * Calculates the conjunction of all Boolean elements in a vector.
 * 
 * @author Sylvain Hallé
 */
public class VectorAnd extends VectorBooleanConnective
{
	/**
	 * Creates a new instance of the function.
	 */
	public VectorAnd()
	{
		super();
	}
	
	@Override
	protected Boolean getOutputValue(List<?> ... in_lists)
	{
		boolean total = true;
		m_witnesses.clear();
		for (Object o : in_lists[0])
		{
			if (!(o instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Expected a Boolean");
			}
			boolean b = (Boolean) o;
			total = total && b;
			m_witnesses.add(!b);
		}
		return total;
	}
	
	@Override
	public String toString()
	{
		return "⩓";
	}
	
	@Override
	public VectorAnd duplicate(boolean with_state)
	{
		VectorAnd vs = new VectorAnd();
		copyInto(vs, with_state);
		return vs;
	}

}
