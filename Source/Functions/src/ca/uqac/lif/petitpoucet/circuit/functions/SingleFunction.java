/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.circuit.functions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;

public abstract class SingleFunction extends Function
{
	public SingleFunction(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}

	/**
	 * Evaluates the function with concrete values. Note that this implementation is
	 * not very efficient, as a function that is involved as the input of 
	 * @return outputs An array of output arguments
	 */
	@Override
	public final Object[] evaluate()
	{
		if (m_evaluated)
		{
			return copyArray(m_returnedValue);
		}
		for (int i = 0; i < m_inArity; i++)
		{
			FunctionConnection fc = m_inputConnections[i];
			if (fc == null)
			{
				m_inputs[i] = null;
			}
			else
			{
				m_inputs[i] = fc.pullValue();
			}
		}
		getValue(m_inputs, m_returnedValue);
		m_evaluated = true;
		return copyArray(m_returnedValue);
	}

	@Override
	public List<TraceabilityNode> query(TraceabilityQuery q, Designator d, TraceabilityNode root, NodeFactory factory)
	{
		List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
		Designator top = d.peek();
		if (!(top instanceof NthInput) && !(top instanceof NthOutput))
		{
			// Can't answer queries that are not about inputs or outputs
			leaves.add(factory.getUnknownNode());
		}
		if (top instanceof NthInput)
		{
			// Ask for an input: find the output to which it is connected
			NthInput ni = (NthInput) top;
			FunctionConnection conn = m_inputConnections[ni.getIndex()];
			Designator tail = d.tail();
			if (tail == null)
			{
				tail = Designator.identity;
			}
			if (conn != null)
			{
				ComposedDesignator new_cd = new ComposedDesignator(tail, new NthOutput(conn.getIndex()));
				ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new_cd, conn.getObject());
				TraceabilityNode tn = factory.getObjectNode(dob);
				leaves.add(tn);
				root.addChild(tn, Quality.EXACT);
			}
		}
		else if (top instanceof NthOutput)
		{
			// Ask for an output: delegate whether it is a provenance or causality query
			int output_nb = ((NthOutput) top).getIndex();
			Designator tail = d.tail();
			if (tail != null)
			{
				answerQuery(q, output_nb, tail, root, factory, leaves);
			}
			else
			{
				answerQuery(q, output_nb, Designator.identity, root, factory, leaves);
			}
		}
		return leaves;
	}

	protected abstract void answerQuery(TraceabilityQuery q, int output_nb, Designator d, TraceabilityNode root, NodeFactory factory, List<TraceabilityNode> leaves);
}
