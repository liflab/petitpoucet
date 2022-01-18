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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentException;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Filters the elements of a list based on the Boolean values of another.
 * @author Sylvain Hallé
 */
public class Filter extends AtomicFunction
{
	/**
	 * A list keeping track of the position in the input list of each element
	 * of the output list, the last time the function was called.
	 */
	/*@ non_null @*/ protected List<Integer> m_positions;

	/**
	 * Creates a new instance of the function.
	 */
	public Filter()
	{
		super(2, 1);
		m_positions = new ArrayList<>();
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		m_positions.clear();
		if (!(inputs[0] instanceof List) || !(inputs[1] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		List<?> list1 = (List<?>) inputs[0];
		List<?> list2 = (List<?>) inputs[1];
		if (list1.size() != list2.size())
		{
			throw new InvalidArgumentException("Lists must be the same size");
		}
		List<Object> out_list = new ArrayList<>();
		for (int i = 0; i < list1.size(); i++)
		{
			Object o2 = list2.get(i);
			if (!(o2 instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Second list must contain Booleans");
			}
			if (Boolean.TRUE.equals(o2))
			{
				out_list.add(list1.get(i));
				m_positions.add(i);
			}
		}
		return new Object[] {out_list};
	}

	@Override
	public void reset()
	{
		super.reset();
		m_positions.clear();
	}

	@Override
	public Filter duplicate(boolean with_state)
	{
		Filter f = new Filter();
		copyInto(f, with_state);
		return f;
	}

	protected void copyInto(Filter f, boolean with_state)
	{
		super.copyInto(f, with_state);
		if (with_state)
		{
			f.m_positions.addAll(m_positions);
		}
	}

	@Override
	public String toString()
	{
		return "Filter";
	}

	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		int output_nb = NthOutput.mentionedOutput(d);
		if (output_nb != 0)
		{
			return root;
		}
		int elem_nb = NthElement.mentionedElement(d);
		if (elem_nb < 0)
		{
			// No element mentioned, return all elements
			LabelledNode and = root;
			if (m_positions.size() > 1)
			{
				and = factory.getAndNode();
				root.addChild(and);
			}
			for (int i : m_positions)
			{
				appendExplanation(and, i, factory);
			}
		}
		else
		{
			if (elem_nb > m_positions.size())
			{
				root.addChild(factory.getUnknownNode());
			}
			else
			{
				appendExplanation(root, m_positions.get(elem_nb), factory);
			}
		}
		return root;
	}
	
	/**
	 * Appends to a node the explanation for the i-th element of the output. In
	 * the case of the filter, the i-th output element is related to the j-th
	 * element of both input vectors, where j is the position of i in the input.
	 * @param root The node where children will be appended
	 * @param j The position of the element in the input
	 * @param factory The factory used to obtain node instances
	 */
	protected void appendExplanation(LabelledNode root, int j, NodeFactory factory)
	{
		LabelledNode in_and = factory.getAndNode();
		{
			Part new_part = NthOutput.replaceOutBy(NthOutput.FIRST, ComposedPart.compose(new NthElement(j), NthInput.FIRST));
			in_and.addChild(factory.getPartNode(new_part, this));
		}
		{
			Part new_part = NthOutput.replaceOutBy(NthOutput.FIRST, ComposedPart.compose(new NthElement(j), NthInput.SECOND));
			in_and.addChild(factory.getPartNode(new_part, this));
		}
		root.addChild(in_and);
	}
}
