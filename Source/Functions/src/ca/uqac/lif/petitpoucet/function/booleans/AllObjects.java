/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.function.booleans;

import java.util.List;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.Function;

/**
 * Boolean object quantifier asserting that a condition evaluates to true
 * on all objects of its input. This function performs the equivalent of
 * universal quantification on a collection of elements.
 * 
 * @author Sylvain Hallé
 */
public class AllObjects extends BooleanObjectQuantifier
{
	/**
	 * Creates a new instance of the quantifier.
	 * @param condition A 1:1 function to evaluate on each element of the input.
	 * This function is expected to return a <tt>boolean</tt> value.
	 */
	public AllObjects(Function condition)
	{
		super(condition);
	}

	@Override
	protected LabelledNode getConnective(NodeFactory f)
	{
		if (Boolean.TRUE.equals(m_verdict))
		{
			return f.getAndNode();
		}
		return f.getOrNode();
	}

	@Override
	public AtomicFunction duplicate(boolean with_state)
	{
		return new AllObjects(m_condition);
	}

	@Override
	protected boolean getVerdict(List<FunctionIndex> trues, List<FunctionIndex> falses)
	{
		if (falses.isEmpty())
		{
			m_conditions = trues;
			return true;
		}
		m_conditions = falses;
		return false;
	}

}
