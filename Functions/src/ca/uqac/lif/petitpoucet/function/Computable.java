/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.circuit.Connectable;

/**
 * Interface implemented by objects that can compute a result.
 * @author Sylvain Hallé
 */
public interface Computable extends Connectable
{
	/**
	 * Calculates the result for a given output port of the object.
	 * @param index The index of the output port
	 * @return The result
	 */
	public Object compute(int index);
	
	/**
	 * Calculates the result for output port 0 of the object.
	 * @return The result
	 */
	public default Object compute()
	{
		return compute(0);
	}
	
	/**
	 * Evaluates the node's output values from its input values. This method is called
	 * when the node is computed for the first time after a reset. The input values are
	 * passed in the {@code input} array, and the output values must be stored in
	 * the {@code output} array.
	 * @param input The input values, in the order of the node's inputs
	 * @param output The output values, in the order of the node's outputs.
	 * This array is pre-allocated and must be filled by this method.
	 */
	void evaluate(Object[] inputs, Object[] outputs);
	
	/**
	 * Resets the internal state of the object.
	 */
	public void reset();
	
	public FunctionDelegate delegate();
}
