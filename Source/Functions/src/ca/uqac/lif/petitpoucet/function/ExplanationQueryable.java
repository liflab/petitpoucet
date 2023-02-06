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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.Queryable;

/**
 * Advertises that an object can be queried for lineage using a relation called
 * <em>explanation</em>.
 */
public interface ExplanationQueryable extends Queryable
{
	/**
	 * Produces an explanation graph for a given part. The method creates a new
	 * node factory by itself.
	 * @param part The part used as the starting point of the explanation
	 * @return A node corresponding to the root of the resulting explanation
	 * graph. The root of this graph is always a {@link PartNode} with
	 * {@code part} as its part, and the callee as the subject. Other nodes
	 * may or may not be attached to this root, depending on the subject and
	 * the explanation.
	 */
	/*@ non_null @*/ public PartNode getExplanation(/*@ non_null @*/ Part part);
	
	/**
	 * Produces an explanation graph for a given part, using nodes provided by
	 * a given factory.
	 * @param part The part used as the starting point of the explanation
	 * @param factory The factory that provides nodes
	 * @return A node corresponding to the root of the resulting explanation
	 * graph. The root of this graph is always a {@link PartNode} with
	 * {@code part} as its part, and the callee as the subject. Other nodes
	 * may or may not be attached to this root, depending on the subject and
	 * the explanation.
	 */
	/*@ non_null @*/ public PartNode getExplanation(/*@ non_null @*/ Part part, /*@ non_null @*/ RelationNodeFactory factory);
}
