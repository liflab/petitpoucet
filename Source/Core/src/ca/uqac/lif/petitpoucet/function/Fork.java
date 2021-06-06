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

/**
 * Duplicates the value on an input pin to multiple output pins.
 */
public class Fork extends AtomicFunction
{
	/**
	 * Creates a new fork.
	 * @param out_arity The output arity
	 */
	public Fork(int out_arity)
	{
		super(1, out_arity);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		Object[] out = new Object[getOutputArity()];
		for (int i = 0; i < out.length; i++)
		{
			out[i] = inputs[0];
		}
		return out;
	}
	
	@Override
	public String toString()
	{
		return "\u2aaa";
	}
	
	@Override
	public Fork duplicate(boolean with_state)
	{
		Fork f = new Fork(getOutputArity());
		copyInto(f, with_state);
		return f;
	}
}
