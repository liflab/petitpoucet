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
import ca.uqac.lif.petitpoucet.function.NthInput;
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
public class StringMappingFunction extends AtomicFunction
{
	/**
	 * The range mapping keeping track of the relation between input and output
	 * characters.
	 */
	/*@ non_null @*/ protected final RangeMapping m_mapping;
	
	/**
	 * The length of the input string processed by the function.
	 */
	protected int m_inLength;
	
	/**
	 * The length of the output string produced by the function.
	 */
	protected int m_outLength;
	
	/**
	 * Creates a new instance of the function.
	 */
	public StringMappingFunction()
	{
		super(1, 1);
		m_mapping = new RangeMapping();
		m_inLength = -1;
		m_outLength = -1;
	}
	
	/**
	 * Creates a new instance of the function by directly populating its member
	 * fields.
	 * @param map The range mapping keeping track of the relation between input
	 * and output characters
	 * @param in_length The length of the input string processed by the function
	 * @param out_length The length of the output string produced by the function
	 */
	protected StringMappingFunction(RangeMapping map, int in_length, int out_length)
	{
		super(1, 1);
		m_mapping = map;
		m_inLength = in_length;
		m_outLength = out_length;
	}
	
	/**
	 * Gets the mapping between input and output ranges computed by this
	 * function.
	 * @return The mapping
	 */
	/*@ pure non_null @*/ public RangeMapping getMapping()
	{
		return m_mapping;
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
		m_outLength = s.length();
		return new Object[] {s};
	}
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, NodeFactory factory)
	{
		if (NthInput.mentionedInput(part) == 0)
		{
			return explainInput(part, factory);
		}
		if (NthOutput.mentionedOutput(part) == 0)
		{
			return explainOutput(part, factory);
		}
		PartNode root = factory.getPartNode(part, this);
		root.addChild(factory.getUnknownNode());
		return root;
	}
	
	/**
	 * Computes the explanation of an input in terms of the output of the
	 * function.
	 * @param part The part designating the input
	 * @param factory A node factory to create explanation nodes
	 * @return The root of the generated tree
	 */
	protected PartNode explainInput(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		List<Range> exps;
		Part new_p = NthInput.replaceInByOut(part, 0);
		Range mentioned_range = Range.mentionedRange(part);
		if (mentioned_range != null)
		{
			// Asking about a part of the input
			exps = m_mapping.trackToOutput(mentioned_range);
			new_p = Range.removeRange(new_p);
		}
		else
		{
			// Asking about the whole output
			exps = m_mapping.trackToOutput();
		}
		if (exps.size() == 1)
		{
			Range r = exps.get(0);
			if (r.getStart() == 0 && r.getEnd() == m_outLength - 1)
			{
				// The range is the whole output string
				PartNode child = factory.getPartNode(new_p, this);
				root.addChild(child);
			}
			else
			{
				// The range is a part of the output string
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
		else if (exps.isEmpty())
		{
			PartNode child = factory.getPartNode(ComposedPart.compose(new_p, Part.nothing), this);
			root.addChild(child);
		}
		return root;
	}
	
	/**
	 * Computes the explanation of an output in terms of the input of the
	 * function.
	 * @param part The part designating the output
	 * @param factory A node factory to create explanation nodes
	 * @return The root of the generated tree
	 */
	protected PartNode explainOutput(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		List<Range> exps;
		Part new_p = NthOutput.replaceOutByIn(part, 0);
		Range mentioned_range = Range.mentionedRange(part);
		if (mentioned_range != null)
		{
			// Asking about a part of the output
			exps = m_mapping.trackToInput(mentioned_range);
			new_p = Range.removeRange(new_p);
		}
		else
		{
			// Asking about the whole output
			exps = m_mapping.trackToInput();
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
		else if (exps.isEmpty())
		{
			PartNode child = factory.getPartNode(ComposedPart.compose(Part.nothing, new_p), this);
			root.addChild(child);
		}
		return root;
	}
	
	/**
	 * Transforms an input string into an output string.
	 * @param s The string to transform
	 * @return The output string
	 */
	protected String transformString(String s)
	{
		return s;
	}
	
	/**
	 * Creates a string mapping function where the input and output mappings are
	 * inverted with respect to the current function.
	 * @return
	 */
	public StringMappingFunction reverse()
	{
		return new StringMappingFunction(m_mapping.reverse(), m_outLength, m_inLength);
	}
}
