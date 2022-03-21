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
package ca.uqac.lif.petitpoucet.function.vector;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * A n:1 function that turns its input arguments into a list made of its
 * input arguments. For example, an instance of this function with an input
 * arity of n=3 evaluated on the arguments "foo", 0, false would produce as
 * its output the list ["foo", 0, false].
 * @author Sylvain Hallé
 */
public class AsList extends AtomicFunction
{
	/**
	 * Creates a new instance of the function.
	 * @param in_arity The input arity of the function
	 */
	public AsList(int in_arity)
	{
		super(in_arity, 1);
	}

	@Override
	public AsList duplicate(boolean with_state)
	{
		return new AsList(getInputArity());
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		List<Object> list = new ArrayList<Object>(inputs.length);
		for (Object o : inputs)
		{
			list.add(o);
		}
		return new Object[] {list};
	}
	
	@Override
	public PartNode getExplanation(Part p, NodeFactory f)
	{
		PartNode root = f.getPartNode(p, this);
		int out_index = NthOutput.mentionedOutput(p);
		if (out_index != 0)
		{
			root.addChild(f.getUnknownNode());
			return root;
		}
		int mentioned_element = NthElement.mentionedElement(p);
		if (mentioned_element < 0)
		{
			// No mentioned element: point to all input arguments
			LabelledNode to_add = root;
			if (getInputArity() > 1)
			{
				LabelledNode and = f.getAndNode();
				root.addChild(and);
				to_add = and;
			}
			for (int i = 0; i < getInputArity(); i++)
			{
				Part new_part = NthOutput.replaceOutByIn(p, i);
				to_add.addChild(f.getPartNode(new_part, this));
			}
		}
		else
		{
			// One element is mentioned: point to corresponding input argument
			Part new_part = replaceNthOutputElementByNthInput(p);
			root.addChild(f.getPartNode(new_part, this));
		}
		return root;
	}
	
	@Override
	public String toString()
	{
		return "AsList";
	}
	
	/**
	 * Creates a new designator by replacing the sequence "m-th element of output 0"
	 * by the sequence "n-th input" inside a composed designator. 
	 * @param d The designator
	 * @return The new designator
	 */
	public static Part replaceNthOutputElementByNthInput(Part d)
	{
		if (!(d instanceof ComposedPart))
		{
			return d; // Nothing to do
		}
		ComposedPart cd = (ComposedPart) d;
		List<Part> desigs = new ArrayList<>();
		for (int i = 0; i < cd.size(); i++)
		{
			desigs.add(cd.get(i));
		}
		for (int i = desigs.size() - 1; i >= 1; i--)
		{
			if (desigs.get(i) instanceof NthOutput && desigs.get(i - 1) instanceof NthElement)
			{
				int in_index = ((NthOutput) desigs.get(i)).getIndex();
				desigs.set(i, new NthInput(in_index));
			}
		}
		return ComposedPart.compose(desigs);
	}
}
