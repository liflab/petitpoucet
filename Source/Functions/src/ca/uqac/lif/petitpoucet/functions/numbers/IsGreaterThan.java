/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.functions.numbers;

import ca.uqac.lif.petitpoucet.functions.NaryFunction;

/**
 * Determines if a number is greater than another number
 * @author Sylvain Hallé
 *
 */
public class IsGreaterThan extends NaryFunction
{
	/**
	 * Creates a new instance of the function
	 */
	public IsGreaterThan()
	{
		super(2);
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_inputs[0] = inputs[0];
		m_inputs[1] = inputs[1];
		boolean b = false;
		if (inputs[0] instanceof Number && inputs[1] instanceof Number)
		{
			b = ((Number) inputs[0]).floatValue() > ((Number) inputs[1]).floatValue();
		}
		m_returnedValue[0] = b;
		outputs[0] = b;
	}
}
