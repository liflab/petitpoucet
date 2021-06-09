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
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Function;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Applies a 1:1 function to every element of an input vector.
 * @author Sylvain Hallé
 */
public class VectorApply extends ParameterizedVectorFunction
{
	/**
	 * Creates a new instance of the function.
	 * @param f The function to apply on each element of the input vector
	 */
	public VectorApply(/*@ non_null @*/ Function f)
	{
		super(f);		
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
					int input_nb = NthInput.mentionedInput(pn.getPart());
					if (input_nb >= 0)
					{
						// This leaf mentions an input of the inner function
						NodeConnector.connect(sub_node, i, factory.getPartNode(VectorOutputFunction.replaceInputByElement(pn.getPart(), elem_index), this), 0);
					}
				}
			}
		}
		return root;
	}
	
	@Override
	public VectorApply duplicate(boolean with_state)
	{
		VectorApply w = new VectorApply((Function) m_function.duplicate(with_state));
		copyInto(w, with_state);
		return w;
	}
	
	@Override
	public String toString()
	{
		return "α(" + m_function.toString() + ")";
	}
}
