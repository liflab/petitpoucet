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
package ca.uqac.lif.petitpoucet.functions.strings;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.StringDesignator;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

public class Concatenate extends NaryFunction
{
	protected int[] m_borders;
	
	protected int[] m_lengths;
	
	public Concatenate(int in_arity)
	{
		super(in_arity);
		m_borders = new int[in_arity];
		m_lengths = new int[in_arity];
	}
	
	public Concatenate()
	{
		this(2);
	}

	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator top = d.peek();
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		if (!m_evaluated)
		{
			// We did not evaluate the function; the best we can say is that the output depends on
			// all the inputs, but this is an over-approximation
			answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.OVER);
			return;
		}
		if (top instanceof StringDesignator.Range)
		{
			int start = ((StringDesignator.Range) top).getStartIndex();
			int end = ((StringDesignator.Range) top).getEndIndex();
			int last = 0;
			TraceabilityNode and = factory.getAndNode();
			for (int i = 0; i < m_borders.length; i++)
			{
				int cur = m_borders[i];
				if (start <= cur && end >= last)
				{
					int range_start = Math.max(0, start - last);
					int range_end = Math.max(0, Math.min(m_lengths[i] - 1, m_lengths[i] + end - cur - 1));
					ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.Range(range_start, range_end), new NthInput(i));
					TraceabilityNode tn = factory.getObjectNode(cd, this);
					and.addChild(tn, Quality.EXACT);
					leaves.add(root);
				}
				last = cur;
			}
			root.addChild(and, Quality.EXACT);
		}
		else
		{
			super.answerQuery(q, output_nb, d, root, factory, leaves);
		}
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		String out = "";
		int len = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			String s = "";
			if (inputs[i] != null)
			{
				s = inputs[i].toString();
			}
			out += s;
			len += s.length();
			m_borders[i] = len;
			m_lengths[i] = s.length();
		}
		outputs[0] = out;
	}
	
	@Override
	public String toString()
	{
		return ".";
	}
}
