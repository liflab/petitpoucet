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
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Function;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Creates an output vector by applying a function on successive "windows" of
 * elements in an input vector.
 * 
 * @author Sylvain Hallé
 */
public class Window extends ParameterizedVectorFunction
{
	/**
	 * The width of the sliding window.
	 */
	protected int m_width;
	
	/**
	 * Creates a new sliding window function.
	 * @param f The function to apply on each window
	 * @param width The width of the sliding window
	 */
	public Window(/*@ non_null @*/ Function f, int width)
	{
		super(f);
		m_width = width;
	}
	
	@Override
	protected List<?> getVectorValue(List<?> in_list)
	{
		List<Object> out_list = new ArrayList<Object>();
		for (int i = 0; i < in_list.size() - m_width + 1; i++)
		{
			List<?> sub_list = in_list.subList(i, i + m_width);
			Function f = (Function) m_function.duplicate(true);
			Object[] out = f.evaluate(new Object[] {sub_list});
			m_lastInstances.add(f);
			out_list.add(out[0]);
		}
		return out_list;
	}
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index == 0) // Only one output pin possible
		{
			int elem_index = NthElement.mentionedElement(part);
			if (elem_index < 0)
			{
				// No specific element is mentioned
				root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(part, 0), this));
				return root;
			}
			// Get sub-tree corresponding to evaluation of the inner function
			NestedNode sub_node = getSubNode(part, elem_index, factory);
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
					Part pn_p = pn.getPart();
					int input_nb = NthInput.mentionedInput(pn_p);
					if (input_nb >= 0)
					{
						// This leaf mentions an input of the inner function
						int mentioned_elem = NthElement.mentionedElement(pn_p);
						if (mentioned_elem >= 0)
						{
							// This leaf points to a precise element of its input vector; offset by window position
							NodeConnector.connect(sub_node, i, factory.getPartNode(offsetElement(pn.getPart(), elem_index), this), 0);
						}
						else
						{
							// Leaf points to the whole input, which corresponds to all elements of the window
							Node and = sub_node;
							if (m_width > 1)
							{
								and = factory.getAndNode();
								NodeConnector.connect(sub_node, i, and, 0);
								for (int j = 0; j < m_width; j++)
								{
									NodeConnector.connect(and, 0, factory.getPartNode(replaceInputByElement(pn.getPart(), elem_index + j), this), 0);
								}
							}
							else
							{
								NodeConnector.connect(sub_node, i, factory.getPartNode(replaceInputByElement(pn.getPart(), elem_index), this), 0);
							}
						}
					}
				}
			}
		}
		return root;
	}
	
	public static Part offsetElement(Part d, int offset)
	{
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
			if (in_d instanceof NthElement && cd.get(i + 1) instanceof NthInput)
			{
				parts.add(new NthElement(((NthElement) in_d).getIndex() + offset));
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
		return ComposedPart.compose(parts);
	}
	
	@Override
	public Window duplicate(boolean with_state)
	{
		Window w = new Window((Function) m_function.duplicate(with_state), m_width);
		copyInto(w, with_state);
		return w;
	}
	
	@Override
	public String toString()
	{
		return "W(" + m_width + "," + m_function.toString() + ")";
	}
}
