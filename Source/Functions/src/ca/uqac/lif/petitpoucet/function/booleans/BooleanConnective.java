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
package ca.uqac.lif.petitpoucet.function.booleans;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Abstract class used as a basis for the implementation of conjunction and
 * disjunction.
 * @author Sylvain Hallé
 */
public abstract class BooleanConnective extends AtomicFunction
{
	/**
	 * An array keeping track of the input arguments the last time the function
	 * was called.
	 */
	/*@ non_null @*/ protected boolean[] m_arguments;
	
	/**
	 * Creates a new Boolean connective.
	 * @param in_arity The input arity of the connective
	 */
	public BooleanConnective(int in_arity)
	{
		super(in_arity, 1);
		m_arguments = new boolean[in_arity];
	}
	
	public PartNode getExplanation(Part d, NodeFactory factory, boolean witness_value)
	{
		PartNode root = factory.getPartNode(d, factory);
		int output_nb = NthOutput.mentionedOutput(d);
		if (output_nb != 0)
		{
			return root;
		}
		int nb_witnesses = countWitnesses(witness_value);
		if (nb_witnesses == 0)
		{
			// Output depends on all inputs
			return super.getExplanation(d, factory);
		}
		LabelledNode and = root;
		if (nb_witnesses > 1)
		{
			and = factory.getAndNode();
			root.addChild(and);
		}
		for (int i = 0; i < m_arguments.length; i++)
		{
			if (m_arguments[i] == witness_value)
			{
				and.addChild(factory.getPartNode(new NthInput(i), this));
			}
		}
		return root;
	}
	
	protected int countWitnesses(boolean witness_value)
	{
		int count = 0;
		for (int i = 0; i < m_arguments.length; i++)
		{
			if (m_arguments[i] == witness_value)
			{
				count++;
				if (count > 1)
				{
					break;
				}
			}
		}
		return count;
	}
	
	protected void copyInto(BooleanConnective bc, boolean with_state)
	{
		super.copyInto(bc, with_state);
		if (with_state)
		{
			for (int i = 0; i < m_arguments.length; i++)
			{
				bc.m_arguments[i] = m_arguments[i];
			}
		}
	}
}
