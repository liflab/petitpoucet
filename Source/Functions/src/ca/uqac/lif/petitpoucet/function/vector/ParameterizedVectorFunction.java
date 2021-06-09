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
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.function.Function;

/**
 * A function that applies another function repeatedly on elements of an input
 * vector. The two notable descendants of this class are {@link VectorApply}
 * and {@link Window}.
 * 
 * @author Sylvain Hallé
 */
public abstract class ParameterizedVectorFunction extends VectorOutputFunction
{
	/**
	 * The function to apply.
	 */
	/*@ non_null @*/ protected Function m_function;

	/**
	 * An instance of the function for each application on the elements
	 * of the last input vector.
	 */
	/*@ non_null @*/ protected List<Function> m_lastInstances;

	public ParameterizedVectorFunction(/*@ non_null @*/ Function f)
	{
		super();
		m_function = f;
		m_lastInstances = new ArrayList<Function>();
	}

	/**
	 * Produces a {@link NestedNode} containing the explanation tree for the
	 * evaluation of a given instance of the inner function.
	 * @param p The part corresponding to the starting point
	 * @param elem_index The index of the function instance
	 * @param factory A factory to obtain node instances
	 * @return A nested node corresponding to the explanation tree
	 */
	/*@ null @*/ protected NestedNode getSubNode(Part p, int elem_index, NodeFactory factory)
	{
		Function f = m_lastInstances.get(elem_index);
		if (!(f instanceof ExplanationQueryable))
		{
			// Cannot explain
			return null;
		}
		Part in_d = VectorOutputFunction.replaceElementByOutput(p);
		PartNode sub_root = ((ExplanationQueryable) f).getExplanation(in_d, factory);
		return NestedNode.createFromTree(sub_root);
	}

	@Override
	public void reset()
	{
		super.reset();
		m_lastInstances.clear();
	}

	protected void copyInto(ParameterizedVectorFunction pvf, boolean with_state)
	{
		super.copyInto(pvf, with_state);
		if (with_state)
		{
			for (Function f : m_lastInstances)
			{
				pvf.m_lastInstances.add((Function) f.duplicate(with_state));
			}
		}
	}
}
