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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.dag.Connectable;
import ca.uqac.lif.util.Duplicable;

/**
 * An object that receives inputs and produces outputs. To this end, a function
 * must implement a method called {@link #evaluate(Object...) evaluate()}.
 *  
 * @author Sylvain Hallé
 */
public interface Function extends Connectable, Contextualizable, Duplicable
{
	/**
	 * Evaluates a function on input arguments.
	 * @param inputs The input arguments. The number of arguments must be equal
	 * to the function's input arity.
	 * @return An array containing the output values produced by the function.
	 * The size of this array must be equal to the function's output arity. 
	 */
	/*@ non_null @*/ public Object[] evaluate(/*@ non_null @*/ Object ... inputs);
	
	/**
	 * Resets the state of the function to that of a fresh instance of the
	 * class.
	 */
	public void reset();
	
	@Override
	public Function duplicate(boolean with_state);
	
	@Override
	public Function duplicate();
}
