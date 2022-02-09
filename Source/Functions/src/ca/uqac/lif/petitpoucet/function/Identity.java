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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;

/**
 * Function that directly returns its input as its output without any
 * modification. Possible uses of this function are for debugging, or to
 * provide an object when a function is needed but no specific processing
 * needs to be done.
 * 
 * @author Sylvain Hallé
 *
 */
public class Identity extends AtomicFunction
{
	/**
	 * Creates a new instance of the function.
	 * @param arity The input and output arity of the function
	 */
	public Identity(int arity)
	{
		super(arity, arity);
	}
	
	@Override
	public String toString()
	{
		return "I";
	}

	@Override
	public AtomicFunction duplicate(boolean with_state)
	{
		Identity i = new Identity(getInputArity());
		copyInto(i, with_state);
		return i;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return inputs;
	}
	
	@Override
	public PartNode getExplanation(Part d, NodeFactory f)
	{
		PartNode root = f.getPartNode(d, this);
		int mentioned_output = NthOutput.mentionedOutput(d);
		if (mentioned_output < 0 || mentioned_output > getInputArity())
		{
			root.addChild(f.getUnknownNode());
			return root;
		}
		root.addChild(f.getPartNode(NthOutput.replaceOutByIn(d, mentioned_output), this));
		return root;
	}
}
