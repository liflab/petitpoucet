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

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.DesignatorLink;

/**
 * Concrete implementation of the {@link DesignatorLink} interface
 * @author Sylvain Hallé
 */
public class ConcreteDesignatorLink implements DesignatorLink
{
	/**
	 * The quality of the link
	 */
	protected Quality m_quality;
	
	/**
	 * The object designated by this link
	 */
	protected DesignatedObject m_dob;
	
	/**
	 * Creates a new concrete designator link
	 * @param dob The object designated by this link
	 * @param q The quality of the link
	 */
	public ConcreteDesignatorLink(DesignatedObject dob, Quality q)
	{
		super();
		m_quality = q;
		m_dob = dob;
	}
	
	@Override
	public Quality getQuality() 
	{
		return m_quality;
	}

	@Override
	public DesignatedObject getDesignatedObject()
	{
		return m_dob;
	}

}
