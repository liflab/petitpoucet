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
package ca.uqac.lif.petitpoucet.function;

import java.util.List;

import ca.uqac.lif.dag.LeafCrawler.LeafFetcher;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.strings.StringEquals;
import ca.uqac.lif.petitpoucet.function.vector.VectorEquals;

/**
 * Checks the equality between two values. Two objects <tt>x</tt> and
 * <tt>y</tt>are considered equal according to the following rules:
 * <ol>
 * <li><tt>null</tt> is equal to <tt>null</tt> but not to anything else</li>
 * <li>two numbers are equal if their float value is equal</li>
 * <li>equality between two strings is evaluated according to
 * the {@link StringEquals} function</li>
 * <li>if none of these cases apply, the result of <tt>x.equals(y)</tt> is
 * returned</li>
 * </ol>
 * Note that for this latter case, the function checks if any of the two
 * objects implements the {@link ExplainableEquals} interface. If so,
 * the Petit Poucet {@link Function} returned by
 * {@link ExplainableEquals#getEqualsFunction()} is evaluated and kept in
 * memory, so that it can be queried for an explanation of the result if
 * desired.
 * 
 * @author Sylvain Hallé
 */
public class Equals extends AtomicFunction
{
	/**
	 * The "equals" function used in the last evaluation of the function, if any.
	 */
	/*@ null @*/ protected Function m_lastEqualsEvaluation;
	
	/**
	 * Creates a new instance of the function.
	 */
	public Equals()
	{
		super(2, 1);
		m_lastEqualsEvaluation = null;
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return new Object[] {areEqual(inputs[0], inputs[1])};
	}
	
	/**
	 * Determines if two objects are equal.
	 * @param o1 The first object
	 * @param o2 The second object
	 * @return {@code true} if the objects are considered equal, {@code false}
	 * otherwise
	 */
	public static boolean isEqualTo(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
		{
			return true;
		}
		if (o1 == null || o2 == null)
		{
			return false;
		}
		if (o1 instanceof Number && o2 instanceof Number)
		{
			return ((Number) o1).floatValue() == ((Number) o2).floatValue();
		}
		if (o1 instanceof String && o2 instanceof String)
		{
			return ((String) o1).compareTo((String) o2) == 0;
		}
		return o1.equals(o2);
	}
	
	/**
	 * Determines if two objects are equal and saves the evaluation context in
	 * the function's state.
	 * @param o1 The first object
	 * @param o2 The second object
	 * @return {@code true} if the objects are considered equal, {@code false}
	 * otherwise
	 */
	protected boolean areEqual(Object o1, Object o2) throws FunctionException
	{
		m_lastEqualsEvaluation = null;
		if (o1 == null && o2 == null)
		{
			return true;
		}
		if (o1 == null || o2 == null)
		{
			return false;
		}
		if (o1 instanceof Number && o2 instanceof Number)
		{
			return ((Number) o1).floatValue() == ((Number) o2).floatValue();
		}
		if (o1 instanceof String)
		{
			m_lastEqualsEvaluation = new StringEquals();
		}
		if (o1 instanceof List)
		{
			m_lastEqualsEvaluation = new VectorEquals();
		}
		if (o1 instanceof ExplainableEquals)
		{
			ExplainableEquals ee = (ExplainableEquals) o1;
			m_lastEqualsEvaluation = ee.getEqualsFunction();
		}
		if (m_lastEqualsEvaluation != null)
		{
			Object out = m_lastEqualsEvaluation.evaluate(o1, o2)[0];
			if (!(out instanceof Boolean))
			{
				throw new FunctionException("Equal function of " + o1 + " does not return a Boolean");
			}
			return (Boolean) out;
		}
		return o1.equals(o2);
	}
	
	@Override
	public PartNode getExplanation(Part p, RelationNodeFactory factory)
	{
		if (m_lastEqualsEvaluation == null || !(m_lastEqualsEvaluation instanceof ExplanationQueryable))
		{
			return super.getExplanation(p, factory);
		}
		PartNode root = factory.getPartNode(p, this);
		if (NthOutput.mentionedOutput(p) != 0)
		{
			root.addChild(factory.getUnknownNode());
			return root;
		}
		RelationNodeFactory sub_factory = factory.getFactory(p, this);
		PartNode sub_root = ((ExplanationQueryable) m_lastEqualsEvaluation).getExplanation(p, sub_factory);
		root.addChild(sub_root);
		LeafFetcher fetcher = new LeafFetcher(sub_root);
		fetcher.crawl();
		for (Node n : fetcher.getLeaves())
		{
			if (!(n instanceof PartNode))
			{
				continue;
			}
			PartNode pn = (PartNode) n;
			Part pn_p = pn.getPart();
			int input_nb = NthInput.mentionedInput(pn_p);
			if (pn.getSubject() != m_lastEqualsEvaluation || (input_nb != 0 && input_nb != 1))
			{
				continue;
			}
			PartNode child = factory.getPartNode(pn_p, this);
			pn.addChild(child);
		}
		return root;
	}
	
	@Override
	public Equals duplicate(boolean with_state)
	{
		Equals e = new Equals();
		copyInto(e, with_state);
		return e;
	}
	
	@Override
	public String toString()
	{
		return "=";
	}
	
	/**
	 * Copies the fields of the current equals function into another instance.
	 * @param e The instance to copy fields to
	 * @param with_state Set to <tt>true</tt> for a stateful copy, <tt>false</tt>
	 * otherwise
	 */
	protected void copyInto(Equals e, boolean with_state)
	{
		super.copyInto(e, with_state);
		if (with_state)
		{
			e.m_lastEqualsEvaluation = m_lastEqualsEvaluation.duplicate(with_state);
		}
	}
	
	/**
	 * Interface defining a method that produces an "equals" function for a given
	 * object. Classes implementing this interface propose an alternate way of
	 * determining if two objects are equal. Instead of calling:
	 * <pre>
	 * boolean b = x.equals(y);</pre>
	 * one instead writes:
	 * <pre>
	 * Function f = x.getEqualsFunction();
	 * boolean b = f.evaluate(x, y);</pre>
	 * The advantage of this method is that the result can be explained as for
	 * any other function; thus:
	 * <pre>
	 * PartNode n = f.getExplanation(NthOutput.FIRST);</pre>
	 * produces a tree which explains why <tt>x</tt> and <tt>y</tt> are equal (or
	 * not).
	 */
	public static interface ExplainableEquals
	{
		/**
		 * Gets an "equals" function for this object. This function must be a 2:1
		 * function returning a Boolean, and accepting two arbitrary 
		 * <tt>Object</tt>s as its arguments. 
		 * @return The function, or <tt>null</tt> if no such method is defined for
		 * this object
		 */
		/*@ null @*/ public Function getEqualsFunction();
	}
}
