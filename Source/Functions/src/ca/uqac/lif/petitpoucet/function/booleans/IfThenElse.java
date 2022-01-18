/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Function with three arguments that acts as an if-then-else construct. It
 * returns the second or the third, depending on whether the first is true or
 * false.
 * @author Sylvain Hallé
 */
public class IfThenElse extends AtomicFunction
{
	/**
	 * A flag that remembers the value of the first operand the last time the
	 * function was called.
	 */
	protected boolean m_firstOperand;
	
	/**
	 * The value of the second operand the last time the function was called.
	 */
	protected Object m_secondOperand;
	
	/**
	 * The value of the third operand the last time the function was called.
	 */
	protected Object m_thirdOperand;
	
	/**
	 * Creates a new instance of the function.
	 */
	public IfThenElse()
	{
		super(3, 1);
		m_outputPins[0] = new IfThenElseOutputPin(0);
		m_firstOperand = false;
		m_secondOperand = null;
		m_thirdOperand = null;
	}
	
	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (m_firstOperand)
		{
			return new Object[] {m_secondOperand};
		}
		return new Object[] {m_thirdOperand};
	}
	
	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		int out_index = NthOutput.mentionedOutput(d);
		if (out_index == 0)
		{
			LabelledNode and = root;
			if (!Equals.isEqualTo(m_secondOperand, m_thirdOperand))
			{
				and = factory.getAndNode();
				root.addChild(and);
				and.addChild(factory.getPartNode(NthInput.FIRST, this));
			}
			int other_index = m_firstOperand ? 1 : 2;
			Part np2 = NthOutput.replaceOutByIn(d, other_index);
			and.addChild(factory.getPartNode(np2, this));
		}
		return root;
	}
	
	@Override
	public IfThenElse duplicate(boolean with_state)
	{
		IfThenElse ite = new IfThenElse();
		copyInto(ite, with_state);
		return ite;
	}
	
	protected void copyInto(IfThenElse ite, boolean with_state)
	{
		super.copyInto(ite, with_state);
		if (with_state)
		{
			ite.m_firstOperand = m_firstOperand;
			ite.m_secondOperand = m_secondOperand;
			ite.m_thirdOperand = m_thirdOperand;
		}
	}
	
	@Override
	public String toString()
	{
		return "?";
	}
	
	protected class IfThenElseOutputPin extends AtomicFunctionOutputPin
	{
		public IfThenElseOutputPin(int index)
		{
			super(index);
		}
		
		@Override
		public Object getValue()
		{
			if (m_evaluated)
			{
				return m_value;
			}
			Object o = m_inputPins[0].getValue();
			if (!(o instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Expected a Boolean");
			}
			m_firstOperand = (Boolean) o;
			m_evaluated = true;
			try
			{
				m_secondOperand = m_inputPins[1].getValue();
			}
			catch (FunctionException e)
			{
				m_secondOperand = null;
			}
			try
			{
				m_thirdOperand = m_inputPins[2].getValue();
			}
			catch (FunctionException e)
			{
				m_thirdOperand = null;
			}
			Object[] outs = IfThenElse.this.getValue();
			m_outputPins[0].setValue(outs[0]);
			return m_value;
		}
		
		@Override
		public IfThenElseOutputPin duplicate(boolean with_state)
		{
			IfThenElseOutputPin afop = new IfThenElseOutputPin(m_index);
			copyInto(afop, false);
			return afop;
		}
		
	}
}