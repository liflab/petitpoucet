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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * An atomic function taking as its input m vectors, and producing as its
 * output an arbitrary value (not necessarily a vector).
 * <p>
 * The class extends {@link AtomicFunction} by keeping in memory the last
 * input lists involved in a function call. It also overrides the
 * explanation provided by its parent {@link AtomicFunction}, and provides a
 * boilerplate explanation where the whole output is explained by the whole
 * input. Only functions that explain their output differently need to
 * override this method.
 * 
 * @author Sylvain Hallé
 */
public abstract class VectorFunction extends AtomicFunction
{
	/**
	 * The last vectors given as an input to the function.
	 */
	/*@ non_null @*/ protected List<?>[] m_lastInputs;

	/**
	 * Creates a new instance of input vector function.
	 * @param in_arity The input arity of the function
	 */
	public VectorFunction(int in_arity)
	{
		super(in_arity, 1);
		m_lastInputs = new List[in_arity];
	}

	@Override
	protected final Object[] getValue(Object ... inputs) throws InvalidArgumentTypeException
	{
		for (int i = 0; i < m_lastInputs.length; i++)
		{
			List<?> to_process = convertToList(inputs[i]);
			if (to_process == null)
			{
				throw new InvalidArgumentTypeException("Expected a list");
			}
			m_lastInputs[i] = to_process;
		}
		return new Object[] {getOutputValue(m_lastInputs)};
	}

	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, RelationNodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index == 0) // Only one output pin possible
		{
			root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(part, 0), this));
		}
		return root;
	}

	@Override
	public void reset()
	{
		super.reset();
		for (int i = 0; i < m_lastInputs.length; i++)
		{
			m_lastInputs[i] = null;			
		}
	}
	
	/**
	 * Returns the length of the shortest of the input lists given to the
	 * function the last time it was called.
	 * @return The length
	 */
	protected int getMinLength()
	{
		int len = -1;
		for (List<?> list : m_lastInputs)
		{
			if (list == null)
			{
				len = 0;
				break;
			}
			if (len < 0 || list.size() < len)
			{
				len = list.size();
			}
		}
		return len;
	}

	protected abstract Object getOutputValue(List<?> ... inputs);
	
	/**
	 * Converts an object into a generic list. If the object is already a list,
	 * it is returned as is. If the object is an array, the contents of the
	 * array are put into a list and returned. Otherwise, the method returns
	 * null. This is a helper method so that functions in this package can
	 * tolerate arrays as their input instead of lists.
	 * @param o The object to convert into a list
	 * @return The llist
	 */
	/*@ null @*/ public static List<?> convertToList(Object o)
	{
		if (o instanceof List)
		{
			return (List<?>) o;
		}
		if (o != null && o.getClass().isArray())
		{
			int len = Array.getLength(o);
			List<Object> list = new ArrayList<Object>(len);
			for (int i = 0; i < len; i++)
			{
				list.add(Array.get(o, i));
			}
			return list;
		}
		return null;
	}
	
	/**
	 * Determines if an object is of an acceptable input type for a vector
	 * function.
	 * @param o The object
	 * @return <tt>true</tt> if the object has an acceptable type, <tt>false</tt>
	 * otherwise
	 */
	public static boolean isAcceptableType(Object o)
	{
		return o instanceof List || (o != null && o.getClass().isArray());
	}

}
