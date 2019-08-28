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
 * Designator pointing to the n-th element of some compound object.
 * @author Sylvain Hallé
 */
public abstract class NthOf implements Designator
{
	/**
	 * The line index
	 */
	protected int m_index;
	
	/**
	 * Creates a new instance of the designator
	 * @param index The number of the line to designate
	 */
	public NthOf(int index)
	{
		super();
		m_index = index;
	}
	
	/**
	 * Gets the line index
	 * @return The index
	 */
	public int getIndex()
	{
		return m_index;
	}
	
	@Override
	public String toString()
	{
		return m_index + "-th";
	}
}
