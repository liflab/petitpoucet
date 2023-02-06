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
package ca.uqac.lif.petitpoucet.function.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * The LTL "next" modality.
 * @author Sylvain Hallé
 */
public class Next extends AtomicFunction
{
	/**
	 * The length of the input vector the last time the function was called.
	 */
	protected int m_inputLength;
	
	/**
	 * Creates a new instance of the operator.
	 */
	public Next()
	{
		super(1, 1);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (!(inputs[0] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		List<?> in_list = (List<?>) inputs[0];
		List<Boolean> out_list = new ArrayList<>(in_list.size() - 1);
		for (int i = 1; i < in_list.size(); i++)
		{
			Object o = in_list.get(i);
			if (!(o instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Expected a Boolean");
			}
			out_list.add((Boolean) in_list.get(i));
		}
		m_inputLength = in_list.size();
		return new Object[] {out_list};
	}
	
	@Override
	public PartNode getExplanation(Part d, RelationNodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		int out_pos = NthOutput.mentionedOutput(d);
		if (out_pos != 0)
		{
			return root;
		}
		int element_nb = NthElement.mentionedElement(d);
		if (element_nb < 0)
		{
			root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(d, 0), this));
		}
		else
		{
			if (element_nb < m_inputLength)
			{
				root.addChild(factory.getPartNode(NthElement.replaceNthOutputByNthInput(d, element_nb + 1), this));	
			}
		}
		return root;
	}
	
	@Override
	public Next duplicate(boolean with_state)
	{
		Next n = new Next();
		copyInto(n, with_state);
		return n;
	}
	
	protected void copyInto(Next n, boolean with_state)
	{
		if (with_state)
		{
			n.m_inputLength = m_inputLength;
		}
	}
	
	@Override
	public String toString()
	{
		return "X";
	}
}
