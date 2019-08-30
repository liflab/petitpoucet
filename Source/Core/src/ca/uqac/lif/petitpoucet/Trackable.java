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

import java.util.List;

/**
 * Object that can answer traceability queries about some of its parts.
 * @author Sylvain Hallé
 */
public interface Trackable 
{
	/**
	 * Gets an instance of a trackable object that can answer queries on this
	 * object's behalf. This method can return the called object itself, or
	 * any other object that implements the {@link Trackable} interface. The
	 * latter case can happen if traceability questions need to be answered
	 * beyond the original object's lifespan (i.e. after its destruction).
	 * @return A trackable object
	 */
	/*@ null @*/ public Trackable getTrackable();
	
	/**
	 * Answers a traceability query.
	 * @param q The query
	 * @param d The part of the object that is the subject of the query
	 * @return A list of list of links
	 */
	/*@ non_null @*/ public List<List<DesignatorLink>> query(/*@ non_null @*/ TraceabilityQuery q, 
	    /*@ non_null @*/ Designator d);
}
