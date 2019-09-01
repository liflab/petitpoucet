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
package ca.uqac.lif.petitpoucet.functions.logic;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

/**
 * Generic first-order quantifier
 * @author Sylvain HalléS
 *
 */
public abstract class Quantifier extends NaryFunction
{
	/**
	 * The condition to evaluate on the set
	 */
	protected Function m_function;

	protected boolean[] m_conditions;

	public Quantifier(Function phi)
	{
		super(1);
		m_function = phi;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_inputs[0] = inputs[0];
		boolean b = getStartValue();
		if (m_inputs[0] instanceof List)
		{
			List<?> list = (List<?>) m_inputs[0];
			m_conditions = new boolean[list.size()];
			int i = 0;
			for (Object o : list)
			{
				Object[] f_in = new Object[1];
				Object[] f_out = new Object[1];
				f_in[0] = o;
				m_function.getValue(f_in, f_out);
				if (f_out[0] instanceof Boolean)
				{
					b = update(b, (Boolean) f_out[0]);
					m_conditions[i] = (Boolean) f_out[0];
				}
				i++;
			}
		}
		outputs[0] = b;
		m_returnedValue[0] = b;
	}

	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		if (!(q instanceof CausalityQuery))
		{
			super.answerQuery(q, output_nb, d, root, factory, leaves);
		}
		if ((Boolean) m_returnedValue[0] == getStartValue() || m_conditions == null)
		{
			super.answerQuery(q, output_nb, d, root, factory, leaves);
		}
		for (int i = 0; i < m_conditions.length; i++)
		{
			TraceabilityNode or = factory.getOrNode();
			if (m_conditions[i] == !getStartValue())
			{
				ComposedDesignator cd = new ComposedDesignator(new CollectionDesignator.NthElement(i),
						new CircuitDesignator.NthInput(0), d);
				TraceabilityNode child = factory.getObjectNode(cd, this);
				or.addChild(child, Quality.EXACT);
			}
			leaves.add(or);
			root.addChild(or, Quality.EXACT);
		}
	}

	protected abstract boolean getStartValue();

	protected abstract boolean update(boolean b1, boolean b2);
}
