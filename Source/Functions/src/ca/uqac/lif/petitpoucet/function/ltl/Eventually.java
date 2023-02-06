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
package ca.uqac.lif.petitpoucet.function.ltl;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;
import ca.uqac.lif.petitpoucet.function.RelationNodeFactory;

/**
 * The LTL "eventually" modality.
 * @author Sylvain Hallé
 */
public class Eventually extends UnaryOperator
{
	/**
	 * Creates a new instance of the operator.
	 */
	public Eventually()
	{
		super();
	}
	
	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return getValue(true, inputs);
	}

	@Override
	public PartNode getExplanation(Part d, RelationNodeFactory factory)
	{
		return getExplanation(d, factory, true);
	}

	@Override
	public Eventually duplicate(boolean with_state)
	{
		Eventually g = new Eventually();
		copyInto(g, with_state);
		return g;
	}
	
	@Override
	public String toString()
	{
		return "F";
	}
}
