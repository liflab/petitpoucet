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
package ca.uqac.lif.petitpoucet;

/**
 * Represents the part of a specific object. 
 * @author Sylvain Hallé
 */
public interface DesignatedObject 
{
	/**
	 * Gets the object that is designated
	 * @return The object
	 */
	/*@ null @*/ public Object getObject();
	
	/**
	 * Gets the part of the object that is designated
	 * @return The designator representing the part of the object
	 */
	/*@ non_null @*/ public Designator getDesignator();
}
