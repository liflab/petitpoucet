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
 * Object that can delegate traceability queries about some of its parts.
 * 
 * @author Sylvain Hallé
 */
public interface Trackable
{
	/**
	 * Gets an instance of a queryable object that can answer queries on this
	 * object's behalf. This method can return the called object itself (if it
	 * also implements {@link Queryable}, or any other object that implements the
	 * {@link Queryable} interface. The latter case can happen if traceability
	 * questions need to be answered beyond the original object's lifespan (i.e.
	 * after its destruction).
	 * 
	 * @return A queryable object
	 */
	/* @ null @ */ public Queryable getQueryable();
}
