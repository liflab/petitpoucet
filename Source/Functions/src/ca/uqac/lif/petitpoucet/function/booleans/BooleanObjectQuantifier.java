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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.dag.LeafCrawler.LeafFetcher;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.function.Function;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.vector.ObjectQuantifier;
import ca.uqac.lif.petitpoucet.function.vector.VectorOutputFunction;

/**
 * An object quantifier evaluating a Boolean condition on each element.
 * This class forms the basis for the {@link AllObjects} and {@link SomeObject}
 * quantifiers.
 * @author Sylvain Hallé
 */
public abstract class BooleanObjectQuantifier extends ObjectQuantifier
{
	/**
	 * A list keeping the condition instances for each of the objects
	 * passed to the quantifier the last time it was evaluated. 
	 */
	/*@ non_null @*/ protected List<FunctionIndex> m_conditions;
	
	/**
	 * The value of the quantifier the last time it was evaluated.
	 */
	/*@ null @*/ protected Boolean m_verdict;

	/**
	 * Creates a new instance of the quantifier.
	 * @param condition A 1:1 function to evaluate on each element of the input.
	 * This function is expected to return a <tt>boolean</tt> value.
	 */
	public BooleanObjectQuantifier(Function condition)
	{
		super(condition);
		m_verdict = null;
		m_conditions = new ArrayList<FunctionIndex>();
	}
	
	@Override
	public PartNode getExplanation(Part p, NodeFactory f)
	{
		PartNode root = f.getPartNode(p, this);
		if (m_conditions.isEmpty())
		{
			root.addChild(f.getPartNode(Part.nothing, this));
			return root;
		}
		LabelledNode to_add = root;
		if (m_conditions.size() > 1)
		{
			LabelledNode n = getConnective(f);
			to_add.addChild(n);
			to_add = n;
		}
		for (int i = 0; i < m_conditions.size(); i++)
		{
			FunctionIndex fi = m_conditions.get(i);
			Function func = fi.m_function;
			NodeFactory sub_factory = f.getFactory(p, func);
			PartNode sub_root = ((ExplanationQueryable) func).getExplanation(p, sub_factory);
			to_add.addChild(sub_root);
			LeafFetcher lf = new LeafFetcher(sub_root);
			lf.crawl();
			for (Node leaf : lf.getLeaves())
			{
				if (!(leaf instanceof PartNode))
				{
					continue;
				}
				PartNode pn_leaf = (PartNode) leaf;
				Part pn_part = pn_leaf.getPart();
				if (NthInput.mentionedInput(pn_part) == 0)
				{
					Part new_part = VectorOutputFunction.replaceInputByElement(pn_part, 0, fi.m_index);
					pn_leaf.addChild(f.getPartNode(new_part, this));
				}
			}
		}
		return root;
	}
	
	@Override
	protected Object[] aggregate(Function[] conditions, Object[] values)
	{
		List<FunctionIndex> trues = new ArrayList<FunctionIndex>(conditions.length);
		List<FunctionIndex> falses = new ArrayList<FunctionIndex>(conditions.length);
		for (int i = 0; i < values.length; i++)
		{
			FunctionIndex fi = new FunctionIndex(i, conditions[i]);
			if (Boolean.TRUE.equals(values[i]))
			{
				trues.add(fi);
			}
			else
			{
				falses.add(fi);
			}
		}
		m_verdict = getVerdict(trues, falses);
		return new Object[] {m_verdict};
	}

	/**
	 * Gets the node instance used to connect the individual explanations of the
	 * condition. This is either an {@link AndNode} or an {@link OrNode},
	 * depending on the verdict produced by the quantifier.
	 * @param f A factory to obtain the node instance from
	 * @return The node instance
	 */
	protected abstract LabelledNode getConnective(NodeFactory f);
	
	/**
	 * Gets the verdict (i.e. Boolean output) of the quantifier, based on
	 * instances of the condition separated into those that evaluate to
	 * <tt>true</tt> on one side, and those that evaluate to
	 * <tt>false</tt> on the other. 
	 * @param trues The list of function indices that evaluate to <tt>true</tt>
	 * @param falses The list of function indices that evaluate to <tt>false</tt>
	 * @return The quantifier's verdict
	 */
	protected abstract boolean getVerdict(List<FunctionIndex> trues, List<FunctionIndex> falses);
	
	/**
	 * An association between an instance of the quantifier's condition and the
	 * position of the element in the input on which it was evaluated. 
	 */
	protected static class FunctionIndex
	{
		/**
		 * The function evaluated on an object.
		 */
		/*@ non_null @*/ protected final Function m_function;
		
		/**
		 * The index of the object.
		 */
		protected final int m_index;
		
		/**
		 * Creates a new function index.
		 * @param index The position of the element in the input on which the
		 * condition was evaluated
		 * @param f The function evaluated on an object
		 */
		public FunctionIndex(int index, /*@ non_null @*/ Function f)
		{
			super();
			m_index = index;
			m_function = f;
		}
	}

}
