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
package ca.uqac.lif.petitpoucet.circuit;

/**
 * Object that has inputs and outputs that can be associated to inputs and
 * outputs of other objects.
 * 
 * @author Sylvain Hallé
 */
public interface CircuitElement
{
	/**
	 * Associates an input of the object to the output of another object
	 * 
	 * @param index
	 *          The index of the object's input
	 * @param connection
	 *          A connection representing the link to another object's output
	 */
	public void setToInput(int index, CircuitConnection connection);

	/**
	 * Associates an output of the object to the input of another object
	 * 
	 * @param index
	 *          The index of the object's input
	 * @param connection
	 *          A connection representing the link to another object's output
	 */
	public void setToOutput(int index, CircuitConnection connection);

	/**
	 * Gets the input arity of the object, i.e. the number of inputs it has
	 * 
	 * @return The arity
	 */
	public int getInputArity();

	/**
	 * Gets the output arity of the object, i.e. the number of outputs it has
	 * 
	 * @return The arity
	 */
	public int getOutputArity();
	
	public CircuitConnection getInputConnection(int index);
	
	public CircuitConnection getOutputConnection(int index);
}
