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

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * Gets the value of a field in an object.
 * 
 * @author Sylvain Hallé
 */
public class GetField extends AtomicFunction
{
	/**
	 * The field to extract from an object.
	 */
	/*@ non_null @*/ protected final Field m_field;
	
	/**
	 * Creates a new instance of the function.
	 * @param name The name of the field to extract from an object
	 */
	public GetField(String name)
	{
		this(new Field(name));
	}
	
	/**
	 * Creates a new instance of the function.
	 * @param f The field to extract from an object
	 */
	public GetField(Field f)
	{
		super(1, 1);
		m_field = f;
	}

	@Override
	public GetField duplicate(boolean with_state)
	{
		return new GetField(m_field);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		if (inputs[0] == null)
		{
			throw new FunctionException("Input argument is null");
		}
		Object o = inputs[0];
		try
		{
			java.lang.reflect.Field f = o.getClass().getDeclaredField(m_field.getName());
			f.setAccessible(true);
			return new Object[] {f.get(o)};
		}
		catch (NoSuchFieldException e)
		{
			throw new FunctionException(e);
		}
		catch (SecurityException e)
		{
			throw new FunctionException(e);
		}
		catch (IllegalArgumentException e)
		{
			throw new FunctionException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new FunctionException(e);
		}
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
		Part new_part = NthOutput.replaceOutBy(p, ComposedPart.compose(m_field, NthInput.FIRST));
		root.addChild(factory.getPartNode(new_part, this));
		return root;
	}
	
	@Override
	public String toString()
	{
		return "Get " + m_field;
	}
}
