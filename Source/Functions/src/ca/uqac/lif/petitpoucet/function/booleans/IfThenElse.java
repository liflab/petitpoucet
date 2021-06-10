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

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
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
	protected boolean m_lastOperand;
	
	/**
	 * Creates a new instance of the function.
	 */
	public IfThenElse()
	{
		super(3, 1);
		m_lastOperand = false;
	}
	
	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (!(inputs[0] instanceof Boolean))
		{
			throw new InvalidArgumentTypeException("Expected a Boolean");
		}
		m_lastOperand = (Boolean) inputs[0];
		if (m_lastOperand)
		{
			return new Object[] {inputs[1]};
		}
		return new Object[] {inputs[2]};
	}
	
	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(d, this);
		int out_index = NthOutput.mentionedOutput(d);
		if (out_index == 0)
		{
			AndNode and = factory.getAndNode();
			root.addChild(and);
			and.addChild(factory.getPartNode(NthInput.FIRST, this));
			int other_index = m_lastOperand ? 1 : 2;
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
			ite.m_lastOperand = m_lastOperand;
		}
	}
	
	@Override
	public String toString()
	{
		return "?";
	}
}
