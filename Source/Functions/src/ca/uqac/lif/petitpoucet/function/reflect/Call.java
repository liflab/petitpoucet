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
package ca.uqac.lif.petitpoucet.function.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * Calls a no-args method on a Java object and returns its value.
 * @author Sylvain Hallé
 */
public class Call extends AtomicFunction
{
	/**
	 * The name of the method to call on the object.
	 */
	/*@ non_null @*/ protected final String m_methodName;
	
	/**
	 * The part designating the return value of the method.
	 */
	/*@ non_null @*/ protected final ReturnValue m_part;
	
	/**
	 * Creates a new instance of the function.
	 * @param name The name of the method to call on the object
	 */
	public Call(/*@ non_null @*/ String name)
	{
		super(1, 1);
		m_methodName = name;
		m_part = new ReturnValue(m_methodName);
	}

	@Override
	public Call duplicate(boolean with_state)
	{
		return new Call(m_methodName);
	}
	
	@Override
	public String toString()
	{
		return m_methodName + "()";
	}

	@Override
	protected Object[] getValue(Object ... inputs) throws InvalidNumberOfArgumentsException
	{
		try
		{
			Object o = inputs[0];
			if (o == null)
			{
				throw new InvalidArgumentTypeException("Input object is null");
			}
			Method m = o.getClass().getMethod(m_methodName);
			Object out = m.invoke(inputs[0]);
			return new Object[] {out};
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new InvalidArgumentTypeException("Input object has no method " + m_methodName);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new FunctionException(e);
		}
	}
	
	@Override
	public PartNode getExplanation(Part p, RelationNodeFactory f)
	{
		PartNode root = f.getPartNode(p, this);
		Part new_p = NthOutput.replaceOutBy(p, ComposedPart.compose(m_part, NthInput.FIRST));
		root.addChild(f.getPartNode(new_p, this));
		return root;
	}
}
