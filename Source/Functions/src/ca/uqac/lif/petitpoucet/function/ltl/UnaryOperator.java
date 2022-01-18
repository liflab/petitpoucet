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
package ca.uqac.lif.petitpoucet.function.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * The base class of {@link Globally} and {@link Eventually}.
 * @author Sylvain Hallé
 */
public abstract class UnaryOperator extends AtomicFunction
{
	/**
	 * A list containing positions of the input vector that are witnesses of the
	 * function's verdict, the last time the function was called.
	 */
	/*@ non_null @*/ protected List<Integer> m_witnesses;
	
	/**
	 * Creates a new instance of unary operator.
	 */
	public UnaryOperator()
	{
		super(1, 1);
		m_witnesses = new ArrayList<>();
	}
	
	/**
	 * A generic form of {@link #getExplanation(Part, NodeFactory)} parameterized
	 * by the Boolean value used by the operator as a witness for explanations.
	 * @param d The part used as the starting point of the explanation
	 * @param factory The factory that provides nodes
	 * @param witness_value The Boolean value used as the witness
	 * @return A node corresponding to the root of the resulting explanation
	 * graph.
	 */
	protected PartNode getExplanation(Part d, NodeFactory factory, boolean witness_value)
	{
		PartNode root = factory.getPartNode(d, this);
		int nb_output = NthOutput.mentionedOutput(d);
		if (nb_output != 0)
		{
			return root;
		}
		int element_nb = NthElement.mentionedElement(d);
		if (element_nb < 0)
		{
			LabelledNode and = root;
			if (m_witnesses.size() > 1)
			{
				and = factory.getAndNode();
				root.addChild(and);
			}
			for (int pos : m_witnesses)
			{
				and.addChild(factory.getPartNode(NthElement.replaceNthOutputByNthInput(d, pos), this));
			}
		}
		else
		{
			for (int w_pos : m_witnesses)
			{
				if (w_pos >= element_nb)
				{
					root.addChild(factory.getPartNode(NthElement.replaceNthOutputByNthInput(d, w_pos), this));
					break;
				}
			}
		}
		return root;
	}

	protected Object[] getValue(boolean witness_value, Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (!(inputs[0] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		List<?> in_list = (List<?>) inputs[0];
		List<Boolean> out_list = new ArrayList<>(in_list.size());
		m_witnesses.clear();
		int last_witness = -1;
		for (int i = 0; i < in_list.size(); i++)
		{
			Object o = in_list.get(i);
			if (!(o instanceof Boolean))
			{
				throw new InvalidArgumentTypeException("Expected a Boolean");
			}
			boolean b = (Boolean) o;
			if (b == witness_value)
			{
				m_witnesses.add(i);
				for (int j = last_witness + 1; j <= i; j++)
				{
					out_list.add(witness_value);
				}
				last_witness = i;
			}
		}
		return new Object[] {out_list};
	}
	
	protected void copyInto(UnaryOperator o, boolean with_state)
	{
		super.copyInto(o, with_state);
		if (with_state)
		{
			o.m_witnesses.addAll(m_witnesses);
		}
	}
}
