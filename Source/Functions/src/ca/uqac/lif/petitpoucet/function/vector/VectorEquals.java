/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.dag.LeafCrawler.LeafFetcher;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * Function that checks the equality between two vectors (i.e. lists). If they
 * are not equal, it can explain the result by identifying the indices where
 * they have different elements.
 * <p>
 * The function can accept arbitrary objects for its two arguments, but will
 * return <tt>false</tt> if any of them is not a list.
 * 
 * @author Sylvain Hallé
 */
public class VectorEquals extends AtomicFunction
{
	/**
	 * The list of position-wise equality comparisons that have been
	 * calculated the last time the function was called.
	 */
	/*@ non_null @*/ protected final List<Equals> m_comparisons;

	/**
	 * The list of indices in the lists where they have different values,
	 * the last time the function was called.
	 */
	/*@ non_null @*/ protected final List<Integer> m_unequalIndices;

	/**
	 * A flag indicating if the first argument given to the function was a list,
	 * the last time it was evaluated.
	 */
	protected boolean m_isListFirst;

	/**
	 * A flag indicating if the second argument given to the function was a list,
	 * the last time it was evaluated.
	 */
	protected boolean m_isListSecond;

	/**
	 * The difference between the number of elements in the first list and the
	 * second list, the last time the function was evaluated. A positive value
	 * indicates that the <em>first</em> list has more elements, while a
	 * negative value indicates that the <em>second</em> list has more elements. 
	 */
	protected int m_sizeDifference;

	/**
	 * Creates a new instance of the function.
	 */
	public VectorEquals()
	{
		super(2, 1);
		m_comparisons = new ArrayList<Equals>();
		m_unequalIndices = new ArrayList<Integer>();
		m_isListFirst = false;
		m_isListSecond = false;
		m_sizeDifference = 0;
	}

	@Override
	public VectorEquals duplicate(boolean with_state)
	{
		VectorEquals e = new VectorEquals();
		if (with_state)
		{
			e.m_comparisons.addAll(m_comparisons);
			e.m_unequalIndices.addAll(m_unequalIndices);
			e.m_isListFirst = m_isListFirst;
			e.m_isListSecond = m_isListSecond;
			e.m_sizeDifference = m_sizeDifference;
		}
		return e;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		m_comparisons.clear();
		m_unequalIndices.clear();
		m_isListFirst = inputs[0] instanceof List;
		m_isListSecond = inputs[1] instanceof List;
		if (!m_isListFirst || !m_isListSecond)
		{
			m_sizeDifference = 0;
			return new Object[] {false};
		}
		List<?> list1 = (List<?>) inputs[0];
		List<?> list2 = (List<?>) inputs[1];
		int min_len = Math.min(list1.size(), list2.size());
		for (int i = 0; i < min_len; i++)
		{
			Equals eq = new Equals();
			boolean b = (Boolean) eq.evaluate(list1.get(i), list2.get(i))[0];
			m_comparisons.add(eq);
			if (!b)
			{
				m_unequalIndices.add(i);
			}
		}
		m_sizeDifference = list1.size() - list2.size();
		return new Object[] {m_unequalIndices.isEmpty() && m_sizeDifference == 0};
	}

	@Override
	public PartNode getExplanation(Part p, RelationNodeFactory factory)
	{
		PartNode root = factory.getPartNode(p, this);
		if (NthOutput.mentionedOutput(p) != 0)
		{
			root.addChild(factory.getUnknownNode());
			return root;
		}
		if (!m_isListFirst || !m_isListSecond)
		{
			explainNotAList(p, root, factory);
			return root;
		}
		if (m_unequalIndices.isEmpty() && m_sizeDifference == 0)
		{
			explainEqualLists(p, root, factory);
			return root;
		}
		explainUnequalLists(p, root, factory);
		return root;
	}

	/**
	 * Generates the explanation graph for the case where one of the arguments is
	 * not a list.
	 * @param p The part corresponding to the starting point of the explanation
	 * @param root The root node under which other nodes should be added
	 * @param factory The factory to obtain new node instances 
	 */
	protected void explainNotAList(Part p, LabelledNode root, RelationNodeFactory factory)
	{
		int num_non_lists = (m_isListFirst ? 0 : 1) + (m_isListSecond ? 0 : 1);
		if (num_non_lists > 0)
		{
			// One of the arguments is not a list
			if (num_non_lists == 2)
			{
				LabelledNode or = factory.getOrNode();
				or.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 0), this));
				or.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 1), this));
				root.addChild(or);
				return;
			}
			if (!m_isListFirst)
			{
				root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 0), this));
				return;
			}
			if (!m_isListSecond)
			{
				root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 1), this));
				return;
			}
		}
	}

	/**
	 * Generates the explanation graph for the case where the two input lists are
	 * equal.
	 * @param p The part corresponding to the starting point of the explanation
	 * @param root The root node under which other nodes should be added
	 * @param factory The factory to obtain new node instances 
	 */
	protected void explainEqualLists(Part p, LabelledNode root, RelationNodeFactory factory)
	{
		// The two lists are equal
		switch (m_comparisons.size())
		{
		case 0: // Two empty lists
			root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 0), this));
			root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(p, 1), this));
			break;
		case 1: // A single element
			compareElements(0, root, factory);
			break;
		default: // Two or more elements
			LabelledNode and = factory.getAndNode();
			for (int i = 0; i < m_comparisons.size(); i++)
			{
				compareElements(i, and, factory);
			}
			root.addChild(and);
			break;
		}
	}
	
	/**
	 * Generates the explanation graph for the case where the two lists are not
	 * equal.
	 * @param p The part corresponding to the starting point of the explanation
	 * @param root The root node under which other nodes should be added
	 * @param factory The factory to obtain new node instances 
	 */
	protected void explainUnequalLists(Part p, LabelledNode root, RelationNodeFactory factory)
	{
		int num_comparisons = m_unequalIndices.size() + m_sizeDifference;
		if (num_comparisons > 1)
		{
			LabelledNode or = factory.getOrNode();
			root.addChild(or);
			root = or;
		}
		for (int index : m_unequalIndices)
		{
			compareElements(index, root, factory);
		}
		NthInput nth_input = new NthInput(m_sizeDifference > 0 ? 0 : 1);
		for (int index = m_comparisons.size(); index < m_comparisons.size() + Math.abs(m_sizeDifference); index++)
		{
			root.addChild(factory.getPartNode(ComposedPart.compose(new NthElement(index), nth_input), this));
		}
	}

	/**
	 * Generates the explanation graph comparing two elements at the same index
	 * in both input lists. This is done by first computing the explanation graph
	 * for the underlying {@link Equals} function instance, and then linking any
	 * mention of that function's input to the input of the {@link VectorEquals}
	 * function.
	 * @param index The position in the lists
	 * @param root The root node under which other nodes should be added
	 * @param factory The factory to obtain new node instances 
	 */
	protected void compareElements(int index, LabelledNode root, RelationNodeFactory factory)
	{
		Equals eq = m_comparisons.get(index);
		RelationNodeFactory sub_factory = factory.getFactory(NthOutput.FIRST, eq);
		PartNode sub_root = eq.getExplanation(NthOutput.FIRST, sub_factory);
		LeafFetcher fetcher = new LeafFetcher(sub_root);
		fetcher.crawl();
		for (Node n : fetcher.getLeaves())
		{
			if (!(n instanceof PartNode))
			{
				continue;
			}
			PartNode pn = (PartNode) n;
			Part p = pn.getPart();
			int mentioned_input = NthInput.mentionedInput(p);
			if (mentioned_input == 0)
			{
				Part new_part = NthInput.replaceInBy(p, ComposedPart.compose(new NthElement(index), NthInput.FIRST));
				pn.addChild(factory.getPartNode(new_part, this));
			}
			else if (mentioned_input == 1)
			{
				Part new_part = NthInput.replaceInBy(p, ComposedPart.compose(new NthElement(index), NthInput.SECOND));
				pn.addChild(factory.getPartNode(new_part, this));
			}
		}
		root.addChild(sub_root);
	}

	@Override
	public String toString()
	{
		return "=";
	}
}
