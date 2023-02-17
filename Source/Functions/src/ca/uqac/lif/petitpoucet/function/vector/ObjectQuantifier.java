/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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

import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.Function;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * An abstract function that evaluates another 1:1 function on each element of
 * a list, and calculates an aggregation over the output values.
 * @author Sylvain Hallé
 *
 */
public abstract class ObjectQuantifier extends AtomicFunction
{
	/**
	 * The condition to evaluate on each object of the collection.
	 */
	/*@ non_null @*/ protected final Function m_condition;

	public ObjectQuantifier(Function condition)
	{
		super(1, 1);
		m_condition = condition;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		List<?> list = VectorFunction.convertToList(inputs[0]);
		if (list == null)
		{
			throw new InvalidArgumentException("Argument should be a list or an array");
		}
		Object[] values = new Object[list.size()];
		Function[] conditions = new Function[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			Object o = list.get(i);
			Function f_o = m_condition.duplicate(false);
			conditions[i] = f_o;
			values[i] = f_o.evaluate(o)[0];
		}
		return aggregate(conditions, values);
	}

	/**
	 * Aggregates the values obtained by evaluating the condition on each element
	 * of the input.
	 * @param conditions The function instances corresponding to the evaluation
	 * of the condition on each element
	 * @param values The output value of the condition on each element
	 * @return The aggregated value that should be returned by the function
	 */
	protected abstract Object[] aggregate(Function[] conditions, Object[] values);
}
