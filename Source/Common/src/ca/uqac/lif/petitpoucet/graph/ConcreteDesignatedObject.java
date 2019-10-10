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
package ca.uqac.lif.petitpoucet.graph;

import java.util.Stack;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;

/**
 * Concrete implementation of the {@link DesignatedObject} interface
 * 
 * @author Sylvain Hallé
 */
public class ConcreteDesignatedObject implements DesignatedObject
{
	/**
	 * The object that is designated
	 */
	protected Object m_object;

	/**
	 * The part of the object that is designated
	 */
	protected Designator m_designator;
	
	/**
	 * The context of the designated object
	 */
	protected Stack<Object> m_context;

	/**
	 * Creates a new concrete designated object
	 * 
	 * @param d
	 *          The part of the object that is designated
	 * @param o
	 *          The object that is designated
	 */
	public ConcreteDesignatedObject(Designator d, Object o)
	{
		super();
		m_object = o;
		m_designator = d;
		m_context = new Stack<Object>();
	}
	
	/**
	 * Creates a new concrete designated object
	 * 
	 * @param d
	 *          The part of the object that is designated
	 * @param o
	 *          The object that is designated
	 * @param c
	 * 			The object's context
	 */
	public ConcreteDesignatedObject(Designator d, Object o, Stack<Object> c)
	{
		super();
		m_object = o;
		m_designator = d;
		m_context = c;
	}

	@Override
	public Object getObject()
	{
		return m_object;
	}

	@Override
	public Designator getDesignator()
	{
		return m_designator;
	}

	@Override
	public String toString()
	{
		return m_designator + " of " + m_object + "(" + m_context + ")";
	}

	@Override
	public int hashCode()
	{
		if (m_object == null)
		{
			return m_designator.hashCode();
		}
		return m_object.hashCode() + m_designator.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ConcreteDesignatedObject))
		{
			return false;
		}
		ConcreteDesignatedObject cdo = (ConcreteDesignatedObject) o;
		return (m_object == null && cdo.m_object == null)
				|| (m_object != null && m_object.equals(cdo.m_object))
						&& m_designator.equals(cdo.m_designator) && sameContext(cdo);
	}
	
	@Override
	public Stack<Object> getObjectContext()
	{
		return m_context;
	}
	
	/**
	 * Checks if two designated objects have the same context.
	 * @param cdo The designated object to compare to
	 * @return <tt>true</tt> if both objects have the same context,
	 * <tt>false</tt> otherwise
	 */
	protected boolean sameContext(ConcreteDesignatedObject cdo)
	{
		Stack<Object> cdo_s = cdo.m_context;
		if (m_context.size() != cdo_s.size())
		{
			return false;
		}
		for (int i = 0; i < m_context.size(); i++)
		{
			if (!m_context.get(i).equals(cdo_s.get(i)))
			{
				return false;
			}
		}
		return true;
	}
}
