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
package ca.uqac.lif.petitpoucet.common;

import ca.uqac.lif.petitpoucet.Designator;

/**
 * Designator that represents a part of a parameter passed to an object.
 * Depending on the context, a parameter could, for example, represent
 * an argument passed to the object's constructor.
 * @author Sylvain Hallé
 */
public class Parameter implements Designator
{
	/**
	 * The name of the parameter
	 */
	protected String m_name;
	
	/**
	 * The part of the parameter that is the subject of this designator
	 */
	protected Designator m_parameterPart;
	
	/**
	 * The object that is the target of this designator
	 */
	protected Object m_object;
	
	/**
	 * Creates a new instance of this designator.
	 * @param target The object that is the target of this designator
	 * @param name The name of the parameter
	 * @param part The part of the parameter that is the subject of this designator
	 */
	public Parameter(Object target, String name, Designator part)
	{
		super();
		m_object = target;
		m_name = name;
		m_parameterPart = part;
	}

	@Override
	public boolean appliesTo(Object o)
	{
		return o == m_object;
	}
	
	@Override
	public String toString()
	{
		return m_parameterPart + " of " + m_name + " in " + m_object;
	}
}
