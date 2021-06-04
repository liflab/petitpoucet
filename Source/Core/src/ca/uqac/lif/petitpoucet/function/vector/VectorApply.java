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

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Function;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class VectorApply extends VectorFunction
{
	/**
	 * The function to apply on each element of the vector.
	 */
	/*@ non_null @*/ protected Function m_function;
	
	/**
	 * An instance of the function for each application on the elements
	 * of the last input vector.
	 */
	/*@ non_null @*/ protected List<Function> m_lastInstances; 
	
	public VectorApply(/*@ non_null @*/ Function f)
	{
		super();
		m_function = f;
		m_lastInstances = new ArrayList<Function>();
	}
	
	@Override
	protected List<?> getVectorValue(List<?> in_list)
	{
		m_lastInstances.clear();
		List<Object> out_list = new ArrayList<Object>(in_list.size());
		for (Object o : in_list)
		{
			Function new_f = (Function) m_function.duplicate(true);
			Object[] out = new_f.evaluate(new Object[] {o});
			out_list.add(out[0]);
			m_lastInstances.add(new_f);
		}
		return out_list;
	}
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part)
	{
		PartNode root = new PartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index == 0) // Only one output pin possible
		{
			int elem_index = mentionedElement(part);
			if (elem_index < 0)
			{
				// No specific element is mentioned
				root.addChild(new PartNode(NthOutput.replaceOutByIn(part, 0), this));
				return root;
			}
		// Get sub-tree corresponding to evaluation of the inner function
			NestedNode sub_node = getSubNode(part, elem_index);
			if (sub_node == null)
			{
				return root;
			}
			root.addChild(sub_node);
			// Append to any leaves of that sub-tree nodes referring to the input vector
			for (int i = 0; i < sub_node.getOutputArity(); i++)
			{
				Pin<? extends Node> pin = sub_node.getAssociatedOutput(i);
				Node n = pin.getNode();
				if (n instanceof PartNode)
				{
					PartNode pn = (PartNode) n;
					int input_nb = NthInput.mentionedInput(pn.getPart());
					if (input_nb >= 0)
					{
						// This leaf mentions an input of the inner function
						NodeConnector.connect(sub_node, i, new PartNode(replaceInputByElement(pn.getPart(), elem_index), this), 0);
					}
				}
			}
		}
		return root;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_lastInstances.clear();
	}
	
	/*@ null @*/ protected NestedNode getSubNode(Part p, int elem_index)
	{
		Function f = m_lastInstances.get(elem_index);
		if (!(f instanceof ExplanationQueryable))
		{
			// Cannot explain
			return null;
		}
		Part in_d = replaceElementByOutput(p);
		PartNode sub_root = ((ExplanationQueryable) f).getExplanation(in_d);
		return NestedNode.createFromTree(sub_root);
	}
	
	/**
	 * Replaces "n-th element of output 0" by "output 0" in a designator.
	 * @param d The part to replace
	 * @return The replaced part
	 */
	public static Part replaceElementByOutput(Part d)
	{
		if (!(d instanceof ComposedPart))
		{
			return d; // Nothing to do
		}
		ComposedPart cd = (ComposedPart) d;
		boolean replaced = false;
		List<Part> parts = new ArrayList<Part>();
		for (int i = 0; i < cd.size() - 1; i++)
		{
			Part in_d = cd.get(i);
			if (in_d instanceof NthElement && cd.get(i + 1) instanceof NthOutput)
			{
				parts.add(cd.get(i + 1));
				replaced = true;
				i++;
			}
			else
			{
				parts.add(in_d);
			}
		}
		if (!replaced)
		{
			return d;
		}
		return ComposedPart.create(parts);
	}
	
	/**
	 * Replaces "input 0" by "n-th element of input 0" in a designator.
	 * @param d The part to replace
	 * @param n The value of n in the description above
	 * @return The replaced part
	 */
	public static Part replaceInputByElement(Part d, int n)
	{
		if (d instanceof NthInput)
		{
			return ComposedPart.create(new NthElement(n), NthInput.FIRST);
		}
		if (!(d instanceof ComposedPart))
		{
			return d; // Nothing to do
		}
		ComposedPart cd = (ComposedPart) d;
		boolean replaced = false;
		List<Part> parts = new ArrayList<Part>();
		for (int i = 0; i < cd.size(); i++)
		{
			Part in_d = cd.get(i);
			if (in_d instanceof NthInput)
			{
				parts.add(new NthElement(n));
				parts.add(in_d);
				replaced = true;
			}
			else
			{
				parts.add(in_d);
			}
		}
		if (!replaced)
		{
			return d;
		}
		return ComposedPart.create(parts);
	}

}
