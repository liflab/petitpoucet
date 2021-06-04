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

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;

/**
 * Function that has no input and returns a constant value.
 */
public class Constant extends AtomicFunction
{
	/**
	 * The value to be returned by the function
	 */
	protected Object m_value;
	
	/**
	 * Creates a new constant.
	 * @param o The value to be returned by the function
	 */
	public Constant(Object o)
	{
		super(0, 1);
		m_value = o;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return new Object[] {m_value};
	}
	
	@Override
	public Constant duplicate(boolean with_state)
	{
		Constant c = new Constant(m_value);
		copyInto(c, with_state);
		return c;
	}
	
	@Override
	public PartNode getExplanation(Part part)
	{
		PartNode root = new PartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index >= 0)
		{
			root.addChild(new PartNode(NthOutput.replaceOutBy(part, Part.all), m_value));
		}
		return root;
	}
	
	@Override
	public String toString()
	{
		if (m_value != null)
		{
			return m_value.toString();
		}
		return "null";
	}
}
