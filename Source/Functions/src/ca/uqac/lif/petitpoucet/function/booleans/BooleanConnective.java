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

import java.util.Arrays;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
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
	 * The value used as a witness for the Boolean connective.
	 */
	protected boolean m_witnessValue;
	
	/**
	 * A flag specifying if the connective is fail-fast.
	 */
	protected boolean m_failFast;
	
	/**
	 * Creates a new Boolean connective.
	 * @param in_arity The input arity of the connective
	 * @param witness_value The value used as a witness for the Boolean
	 * connective
	 * @param fail_fast Set to {@code true} to make it a fail-fast connective
	 */
	protected BooleanConnective(int in_arity, boolean witness_value, boolean fail_fast)
	{
		super(in_arity, 1);
		m_arguments = new boolean[in_arity];
		m_failFast = fail_fast;
		if (fail_fast)
		{
			m_outputPins[0] = new FailFastOutputPin();
		}
		m_witnessValue = witness_value;
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
		int nb_witnesses = countWitnesses(m_witnessValue);
		if (nb_witnesses == 0)
		{
			// Output depends on all inputs
			return super.getExplanation(d, factory);
		}
		LabelledNode and = root;
		if (nb_witnesses > 1 && !m_failFast)
		{
			and = factory.getOrNode();
			root.addChild(and);
		}
		for (int i = 0; i < m_arguments.length; i++)
		{
			if (m_arguments[i] == m_witnessValue)
			{
				and.addChild(factory.getPartNode(new NthInput(i), this));
				if (m_failFast)
				{
					break;
				}
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
		bc.m_witnessValue = m_witnessValue;
		if (with_state)
		{
			bc.m_arguments = Arrays.copyOf(m_arguments, m_arguments.length);
		}
	}
	
	public class FailFastOutputPin extends AtomicFunctionOutputPin
	{
		/**
		 * Creates a new fail-fast output pin.
		 */
		public FailFastOutputPin()
		{
			super(0);
		}

		@Override
		public Object getValue()
		{
			if (m_evaluated)
			{
				return m_value;
			}
			Object[] ins = new Object[getInputArity()];
			for (int i = 0; i < m_inputPins.length; i++)
			{
				ins[i] = m_inputPins[i].getValue();
				if (!(ins[i] instanceof Boolean))
				{
					throw new InvalidArgumentTypeException("Expected a Boolean");
				}
				boolean b = (Boolean) ins[i];
				m_arguments[i] = b;
				if (b == m_witnessValue)
				{
					// Don't evaluate other input arguments
					break;
				}
			}
			Object[] outs = BooleanConnective.this.getValue(ins);
			for (int i = 0; i < m_outputPins.length; i++)
			{
				m_outputPins[i].setValue(outs[i]);
			}
			if (!m_evaluated)
			{
				throw new FunctionException("Cannot get value");
			}
			return m_value;
		}
		
		@Override
		public FailFastOutputPin duplicate(boolean with_state)
		{
			FailFastOutputPin afop = new FailFastOutputPin();
			copyInto(afop, false);
			return afop;
		}
	}
}
