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
import ca.uqac.lif.petitpoucet.function.NthInput;

/**
 * A vector function that applies a Boolean operator on elements on an input
 * vector (made of Boolean values).
 * @author Sylvain Hallé
 */
public abstract class VectorBooleanConnective extends VectorFunction
{
	/**
	 * A list keeping track of what input elements were equal to zero the last
	 * time the function was called. A different explanation is produced if some
	 * elements are null than when they are all different from zero.
	 */
	/*@ non_null @*/ protected List<Boolean> m_witnesses;
	
	/**
	 * Creates a new vector Boolean connective.
	 */
	public VectorBooleanConnective()
	{
		super(1);
		m_witnesses = new ArrayList<Boolean>();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_witnesses.clear();
	}
	
	/**
	 * Counts the number of input elements that were equal to false the last
	 * time the function was called.
	 * @return The number of false elements
	 */
	protected int countWitnesses()
	{
		int num_nulls = 0;
		for (boolean b : m_witnesses)
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
		int num_nulls = countWitnesses();
		if (num_nulls == 0)
		{
			// No witnesses: output depends on all inputs
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
		for (int i = 0; i < m_witnesses.size(); i++)
		{
			if (m_witnesses.get(i))
			{
				ln.addChild(factory.getPartNode(ComposedPart.compose(new NthElement(i), NthInput.FIRST), this));
			}
		}
		return root;
	}
	
	protected void copyInto(VectorBooleanConnective vp, boolean with_state)
	{
		super.copyInto(vp, with_state);
		if (with_state)
		{
			vp.m_witnesses.addAll(m_witnesses);
		}
	}
}
