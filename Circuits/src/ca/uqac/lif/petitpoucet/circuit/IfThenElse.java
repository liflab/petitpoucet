/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

public class IfThenElse extends Node
{
	/**
	 * The condition of the if-then-else. This is used to store the value of
	 * the condition when it is computed, so that it can be used in the
	 * explanation phase.
	 */
	/*@ null @*/ protected Boolean m_condition;
	
	public IfThenElse()
	{
		super(3, 1);
		m_condition = null;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_condition = null;
	}

	@Override
	public IfThenElse duplicate(boolean with_state)
	{
		return new IfThenElse();
	}

	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		if (!(input[0] instanceof Boolean))
		{
			throw new IllegalArgumentException("Expected a Boolean");
		}
		m_condition = (Boolean) input[0];
		output[0] = m_condition ? input[1] : input[2];
	}
	
	@Override
	protected Vertex explain(int index, Part tail, VertexFactory f) throws ExplanationException
	{
		if (m_condition == null)
		{
			throw new ExplanationException("Condition not computed");
		}
		Vertex.OrVertex v = f.getOr();
		v.addChild(f.getPart(new Connectable.InputPart(0), this));
		int in_index = m_condition ? 1 : 2;
		Part in_part = CompositePart.compose(tail, new Connectable.InputPart(in_index));
		v.addChild(f.getPart(in_part, this));
		return v;
	}
	
	@Override
	public String toString()
	{
		return "\u2443"; // Fork
	}
}
