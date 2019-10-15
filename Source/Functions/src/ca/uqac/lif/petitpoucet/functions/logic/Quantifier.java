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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.ObjectNode;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.StateDuplicable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityTree;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.ContextVariable.ContextDesignator;
import ca.uqac.lif.petitpoucet.functions.ContextVariable.ContextVariableQueryable;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

/**
 * Generic first-order quantifier
 * @author Sylvain Hallé
 *
 */
public abstract class Quantifier extends UnaryFunction<Object,Boolean>
{
	/**
	 * The condition to evaluate on the set
	 */
	protected CircuitFunction m_function;

	protected Function m_domain;

	protected String m_variable;

	protected boolean[] m_conditions;

	public Quantifier(String variable, Function domain, CircuitFunction phi)
	{
		super(Object.class, Boolean.class);
		m_function = phi;
		m_domain = domain;
		m_variable = variable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public QuantifierQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		boolean b = getStartValue();
		Object[] out = new Object[1];
		Queryable dom_q = ((StateDuplicable<Queryable>) m_domain.evaluate(inputs, out, c)).duplicate(true);
		List<Object> domain = (List<Object>) out[0];
		List<Boolean> inner_values = new ArrayList<Boolean>(domain.size());
		List<Queryable> inner_queryables = new ArrayList<Queryable>(domain.size());
		for (Object o : domain)
		{
			CircuitFunction m_dup = m_function.duplicate(true);
			m_dup.setContext(m_variable, o);
			Object[] f_in = new Object[1];
			Object[] f_out = new Object[1];
			f_in[0] = inputs[0];
			Queryable q = m_dup.evaluate(f_in, f_out);
			if (f_out[0] instanceof Boolean)
			{
				boolean new_b = (Boolean) f_out[0];
				Queryable q_dup = ((StateDuplicable<Queryable>) q).duplicate(true);
				inner_queryables.add(q_dup);
				inner_values.add(new_b);
				b = update(b, new_b);
			}
		}
		outputs[0] = b;
		return new QuantifierQueryable(toString(), m_variable, getStartValue(), b, dom_q, inner_queryables, inner_values);
	}

	public static class QuantifierQueryable extends FunctionQueryable
	{
		protected boolean m_startValue;

		protected boolean m_value;

		protected List<Queryable> m_innerQueryables;

		protected List<Boolean> m_innerValues;

		protected Queryable m_domainQueryable;

		protected String m_varName;

		public QuantifierQueryable(String reference, String var_name, boolean start_value, boolean value, Queryable domain_queryable, List<Queryable> inner_queryables, List<Boolean> inner_values)
		{
			super(reference, 1, 1);
			m_startValue = start_value;
			m_value = value;
			m_innerQueryables = inner_queryables;
			m_domainQueryable = domain_queryable;
			m_innerValues = inner_values;
			m_varName = var_name;
		}

		@SuppressWarnings("unchecked")
		@Override
		public QuantifierQueryable duplicate(boolean with_state)
		{
			List<Queryable> inner_q = new ArrayList<Queryable>(m_innerQueryables.size());
			for (Queryable cq : m_innerQueryables)
			{
				inner_q.add(((StateDuplicable<Queryable>) cq).duplicate(with_state));
			}
			Queryable dom_q = ((StateDuplicable<Queryable>) m_domainQueryable).duplicate(with_state);
			List<Boolean> inner_v = new ArrayList<Boolean>(m_innerValues.size());
			inner_v.addAll(m_innerValues);
			return new QuantifierQueryable(m_reference, m_varName, m_startValue, m_value, dom_q, inner_q, inner_v);
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			TraceabilityNode new_root;
			if (m_value == m_startValue)
			{
				new_root = factory.getAndNode();
			}
			else
			{
				new_root = factory.getOrNode();
			}
			root.addChild(new_root, Quality.EXACT);
			for (int i = 0; i < m_innerQueryables.size(); i++)
			{
				Queryable inner_q = m_innerQueryables.get(i);
				boolean value = m_innerValues.get(i);
				if (q instanceof CausalityQuery && value != m_value)
				{
					// Not part of the cause
					continue;
				}
				Tracer sub_factory = factory.getSubTracer(toString());
				ComposedDesignator cd = new ComposedDesignator(d, new NthOutput(0));
				TraceabilityTree tree = sub_factory.trace(q, cd, inner_q);
				new_root.addChild(tree.getRoot(), Quality.EXACT);
				List<TraceabilityNode> l_f_links = tree.getLeaves();
				for (TraceabilityNode l_f_link : l_f_links)
				{
					if (l_f_link instanceof ObjectNode)
					{
						ObjectNode on = (ObjectNode) l_f_link;
						DesignatedObject dob = on.getDesignatedObject();
						Designator head = dob.getDesignator().peek();
						if (!(head instanceof ContextDesignator) || !(dob.getObject() instanceof ContextVariableQueryable))
						{
							leaves.add(on);
							continue;
						}
						String var_name = ((ContextVariableQueryable) dob.getObject()).getName();
						if (var_name.compareTo(m_varName) != 0)
						{
							leaves.add(l_f_link);
							continue;
						}
						Tracer sub_sub_factory = sub_factory.getSubTracer("Context");
						ComposedDesignator c_cd = new ComposedDesignator(new NthElement(i), new NthOutput(0));
						TraceabilityTree c_tree = sub_sub_factory.trace(q, c_cd, m_domainQueryable);
						on.addChild(c_tree.getRoot(), Quality.EXACT);
						List<TraceabilityNode> context_leaves = c_tree.getLeaves();
						for (TraceabilityNode context_leaf : context_leaves)
						{
							if (!(context_leaf instanceof ObjectNode))
							{
								leaves.add(context_leaf);
								continue;
							}
							if (((ObjectNode) context_leaf).getDesignatedObject().getDesignator().peek() instanceof NthInput)
							{
								Designator context_leaf_tail = ((ObjectNode) context_leaf).getDesignatedObject().getDesignator().tail();
								ComposedDesignator new_leaf_d = new ComposedDesignator(context_leaf_tail, new NthInput(0));
								TraceabilityNode new_leaf = factory.getObjectNode(new_leaf_d, this);
								context_leaf.addChild(new_leaf, Quality.EXACT);
								leaves.add(new_leaf);
							}
						}
					}
				}
			}
			return leaves;
		}
	}

	protected abstract boolean getStartValue();

	protected abstract boolean update(boolean b1, boolean b2);
}
