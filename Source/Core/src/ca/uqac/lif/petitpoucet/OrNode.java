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
package ca.uqac.lif.petitpoucet;

import ca.uqac.lif.dag.LabelledNode;

/**
 * Node representing a disjunction of two lineage graphs.
 * @author Sylvain Hallé
 */
public class OrNode extends LabelledNode
{
	/**
	 * Creates a new or node. The constructor has {@code package} visibility to
	 * force the use of a {@link NodeFactory} to obtain instances of this class.
	 */
	protected OrNode()
	{
		super("∨");
	}
	
	@Override
	public OrNode duplicate(boolean with_state)
	{
		OrNode n = new OrNode();
		copyInto(n, with_state);
		return n;
	}
}
