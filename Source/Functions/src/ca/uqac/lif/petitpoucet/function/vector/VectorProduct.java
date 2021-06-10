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
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthInput;

/**
 * Calculates the product of all numerical elements in a vector.
 * 
 * @author Sylvain Hallé
 */
public class VectorProduct extends VectorFunction
{
	/**
	 * A list keeping track of what input elements were equal to zero the last
	 * time the function was called. A different explanation is produced if some
	 * elements are null than when they are all different from zero.
	 */
	/*@ non_null @*/ protected List<Boolean> m_nulls;
	
	public VectorProduct()
	{
		super();
		m_nulls = new ArrayList<Boolean>();
	}
	
	@Override
	protected Number getOutputValue(List<?> in_list)
	{
		float total = 1;
		m_nulls.clear();
		for (Object o : in_list)
		{
			if (!(o instanceof Number))
			{
				throw new InvalidArgumentTypeException("Expected a number");
			}
			float v = ((Number) o).floatValue();
			total *= v;
			if (v == 0)
			{
				m_nulls.add(true);
			}
			else
			{
				m_nulls.add(false);
			}
		}
		return total;
	}
	
	/**
	 * Counts the number of input elements that were equal to 0 the last
	 * time the function was called.
	 * @return The number of null elements
	 */
	protected int countNulls()
	{
		int num_nulls = 0;
		for (boolean b : m_nulls)
		{
			if (b)
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
			ln = factory.getOrNode();
			root.addChild(ln);
		}
		for (int i = 0; i < m_nulls.size(); i++)
		{
			if (m_nulls.get(i))
			{
				ln.addChild(factory.getPartNode(ComposedPart.compose(new NthElement(i), NthInput.FIRST), this));
			}
		}
		return root;
	}
	
	@Override
	public String toString()
	{
		return "Π";
	}
	
	@Override
	public VectorProduct duplicate(boolean with_state)
	{
		VectorProduct vs = new VectorProduct();
		copyInto(vs, with_state);
		return vs;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_nulls.clear();
	}
	
	protected void copyInto(VectorProduct vp, boolean with_state)
	{
		super.copyInto(vp, with_state);
		if (with_state)
		{
			vp.m_nulls.addAll(m_nulls);
		}
	}
}
