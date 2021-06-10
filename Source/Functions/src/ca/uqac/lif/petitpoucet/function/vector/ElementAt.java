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

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Gets an element at a given position inside a vector.
 * 
 * @author Sylvain Hallé
 */
public class ElementAt extends VectorFunction
{
	/**
	 * The position of the element to get in the input vector.
	 */
	public int m_position;
	
	/**
	 * Gets a new instance of the function.
	 * @param position The position of the element to get in the input vector
	 */
	public ElementAt(int position)
	{
		super();
		m_position = position;
	}
	
	@Override
	protected Object getOutputValue(List<?> inputs)
	{
		if (m_position < 0 || m_position >= inputs.size())
		{
			throw new FunctionException("Position outside of bounds");
		}
		return inputs.get(m_position);
	}
	
	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		int output_index = NthOutput.mentionedOutput(d);
		if (output_index != 0)
		{
			return root;
		}
		Part new_p = NthOutput.replaceOutByIn(d, output_index);
		new_p = VectorOutputFunction.replaceInputByElement(new_p, m_position);
		root.addChild(factory.getPartNode(new_p, this));
		return root;
	}
	
	@Override
	public String toString()
	{
		return "[" + m_position + "]";
	}
	
	@Override
	public ElementAt duplicate(boolean with_state)
	{
		ElementAt ea = new ElementAt(m_position);
		copyInto(ea, with_state);
		return ea;
	}
}
