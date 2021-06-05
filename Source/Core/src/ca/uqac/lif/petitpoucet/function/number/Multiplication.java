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
package ca.uqac.lif.petitpoucet.function.number;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.OrNode;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.NthInput;

public class Multiplication extends AtomicFunction implements ExplanationQueryable
{
	protected boolean[] m_nulls;

	public Multiplication(int in_arity)
	{
		super(in_arity, 1);
		m_nulls = new boolean[in_arity];
	}

	@Override
	public Object[] getValue(Object ... inputs)
	{
		Object[] out = new Object[1];
		float total = 1;
		for (int i = 0; i < inputs.length; i++)
		{
			if (inputs[i] instanceof Number)
			{
				float v = ((Number) inputs[i]).floatValue();
				if (v == 0)
				{
					m_nulls[i] = true;
				}
				else
				{
					m_nulls[i] = false;
				}
				total *= v;
			}
		}
		out[0] = total;
		return out;
	}

	@Override
	public void reset()
	{
		super.reset();
		for (int i = 0; i < m_nulls.length; i++)
		{
			m_nulls[i] = false;
		}
	}

	@Override
	public String toString()
	{
		return "×";
	}
	
	/**
	 * Counts the number of input arguments that were equal to 0 the last
	 * time the function was called.
	 * @return The number of null arguments
	 */
	protected int countNulls()
	{
		int num_nulls = 0;
		for (int i = 0; i < m_nulls.length; i++)
		{
			if (m_nulls[i])
			{
				num_nulls++;
			}
		}
		return num_nulls;
	}

	@Override
	public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int num_nulls = countNulls();
		if (num_nulls == 0)
		{
			// No null values: output depends on all inputs
			return super.getExplanation(part, factory);
		}
		LabelledNode ln = null;
		if (num_nulls == 1)
		{
			ln = root;
		}
		else
		{
			ln = new OrNode();
			root.addChild(ln);
		}
		for (int i = 0; i < m_nulls.length; i++)
		{
			if (m_nulls[i])
			{
				ln.addChild(factory.getPartNode(new NthInput(i), this));
			}
		}
		return root;
	}
}