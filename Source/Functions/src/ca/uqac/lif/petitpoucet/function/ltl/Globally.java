/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

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

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.InvalidNumberOfArgumentsException;

/**
 * The LTL "globally" modality.
 * @author Sylvain Hallé
 */
public class Globally extends UnaryOperator
{
	/**
	 * Creates a new instance of the operator.
	 */
	public Globally()
	{
		super();
	}
	
	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return getValue(false, inputs);
	}

	@Override
	public PartNode getExplanation(Part d, NodeFactory factory)
	{
		return getExplanation(d, factory, false);
	}

	@Override
	public Globally duplicate(boolean with_state)
	{
		Globally g = new Globally();
		copyInto(g, with_state);
		return g;
	}
	
	@Override
	public String toString()
	{
		return "G";
	}
}
