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
package ca.uqac.lif.petitpoucet.function.strings;

import java.util.List;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Function that transforms an input string into an output string, and keeps
 * track of the associations between characters of the input and the output
 * through the use of a {@link RangeMapping} object. Descendants of this
 * function need only to implement their functionality in
 * {@link #transformString(String)}, and add the mappings between input/output
 * ranges in the internal {@link RangeMapping} object. The
 * {@link StringMappingFunction} takes care of computing explanations based
 * on the contents of the mapping.
 */
public abstract class StringMappingFunction extends AtomicFunction
{
	/**
	 * The range mapping keeping track of the relation between input and output
	 * characters.
	 */
	protected final RangeMapping m_mapping;
	
	/**
	 * The length of the input string processed by the function
	 */
	protected int m_inLength;
	
	/**
	 * Creates a new instance of the function.
	 */
	public StringMappingFunction()
	{
		super(1, 1);
		m_mapping = new RangeMapping();
		m_inLength = -1;
	}
	
	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (inputs.length != 1)
		{
			throw new InvalidNumberOfArgumentsException("Function expects 1 argument");
		}
		if (!(inputs[0] instanceof String))
		{
			throw new InvalidArgumentTypeException("Function expects a String");
		}
		String s = transformString((String) inputs[0]);
		m_inLength = ((String) inputs[0]).length();
		return new Object[] {s};
	}
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		if (NthOutput.mentionedOutput(part) != 0)
		{
			root.addChild(factory.getUnknownNode());
			return root;
		}
		List<Range> exps;
		Part new_p = NthOutput.replaceOutByIn(part, 0);
		Range mentioned_range = Range.mentionedRange(part);
		if (mentioned_range != null)
		{
			// Asking about a part of the output
			exps = m_mapping.invert(mentioned_range);
			new_p = Range.removeRange(new_p);
		}
		else
		{
			// Asking about the whole output
			exps = m_mapping.invert();
		}
		if (exps.size() == 1)
		{
			Range r = exps.get(0);
			if (r.getStart() == 0 && r.getEnd() == m_inLength - 1)
			{
				// The range is the whole input string
				PartNode child = factory.getPartNode(new_p, this);
				root.addChild(child);
			}
			else
			{
				// The range is a part of the input string
				Part head_p = ComposedPart.compose(new_p, r);
				PartNode child = factory.getPartNode(head_p, this);
				root.addChild(child);
			}
		}
		else if (exps.size() > 1)
		{
			AndNode and = factory.getAndNode();
			root.addChild(and);
			for (Range r : exps)
			{
				Part head_p = ComposedPart.compose(new_p, r);
				PartNode child = factory.getPartNode(head_p, this);
				and.addChild(child);
			}
		}
		return root;
	}
	
	/**
	 * Transforms an input string into an output string.
	 * @param s The string to transform
	 * @return The output string
	 */
	protected abstract String transformString(String s);
}
