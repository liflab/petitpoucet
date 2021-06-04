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

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * An atomic function taking as its input a single vector, and producing as its
 * output a single vector (of possibly different size).
 * <p>
 * The class extends {@link AtomicFunction} by keeping in memory the last pair
 * of input/output lists involved in a function call. It also overrides the
 * explanation provided by its parent {@link AtomicFunction}, and provides a
 * boilerplate explanation where:
 * <ul>
 * <li>The whole output is explained by the whole input</li>
 * <li>A single element of the output vector is explained by every element
 * of the input vector</li>
 * </ul>
 * Only vector functions that explain their output differently need to override
 * this method.
 */
public abstract class VectorFunction extends AtomicFunction
{
	/**
	 * The last vector given as an input to the function.
	 */
	protected List<?> m_lastInputs;
	
	/**
	 * The last vector produced as an output by the function.
	 */
	protected List<?> m_lastOutputs;
	
	/**
	 * Creates a new instance of vector function.
	 */
	public VectorFunction()
	{
		super(1, 1);
	}
	
	@Override
	protected final Object[] getValue(Object ... inputs) throws InvalidArgumentTypeException
	{
		if (!(inputs[0] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		m_lastInputs = (List<?>) inputs[0];
		m_lastOutputs = getVectorValue(m_lastInputs);
		return new Object[] {m_lastOutputs};
	}
	
	/**
	 * Computes the output list based on an input list.
	 * @param in_list The input list
	 * @return The output list
	 */
	protected abstract List<?> getVectorValue(List<?> in_list);
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part)
	{
		PartNode root = new PartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index == 0) // Only one output pin possible
		{
			int elem_index = mentionedElement(part);
			if (elem_index < 0)
			{
				// No specific element is mentioned
				root.addChild(new PartNode(NthOutput.replaceOutByIn(part, 0), this));
				return root;
			}
			if (elem_index > m_lastOutputs.size())
			{
				// Element index outside of size of last output
				return root;
			}
			LabelledNode and = root;
			if (m_lastInputs.size() > 1)
			{
				and = new AndNode();
				root.addChild(and);
			}
			for (int i = 0; i < m_lastInputs.size(); i++)
			{
				PartNode in = new PartNode(NthElement.replaceNthOutputByNthInput(part, i), this);
				and.addChild(in);
			}
		}
		return root;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_lastInputs = null;
		m_lastOutputs = null;
	}
	
	/**
	 * Retrieves the element of the output vector mentioned in a designator.
	 * If multiple {@link NthElement} are present, the one closest to the
	 * designator mentioning the function's output is kept.
	 * @param d The designator
	 * @return The element index, or -1 if no specific element is mentioned
	 */
	protected int mentionedElement(Part d)
	{
		int index = -1;
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = cd.size() - 1; i >= 0; i--)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof NthElement)
				{
					index = ((NthElement) in_d).getIndex();
				}
			}
		}
		return index;
	}
	
}
