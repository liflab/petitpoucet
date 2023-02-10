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
package ca.uqac.lif.petitpoucet.function.reflect;

import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.booleans.And;

/**
 * A circuit evaluating the condition that corresponding fields of two objects
 * are equal.
 * 
 * @author Sylvain Hallé
 */
public class EqualFields extends Circuit
{
	/*@ non_null @*/ protected final String[] m_fields;
	
	public EqualFields(String ... fields)
	{
		super(2, 1);
		m_fields = fields;
		Fork f1 = new Fork(fields.length);
		Fork f2 = new Fork(fields.length);
		And a = new And(fields.length);
		for (int i = 0; i < fields.length; i++)
		{
			GetField gf1 = new GetField(fields[i]);
			NodeConnector.connect(f1, i, gf1, 0);
			GetField gf2 = new GetField(fields[i]);
			NodeConnector.connect(f2, i, gf2, 0);
			Equals eq = new Equals();
			NodeConnector.connect(gf1, 0, eq, 0);
			NodeConnector.connect(gf2, 0, eq, 1);
			NodeConnector.connect(eq, 0, a, i);
			addNodes(gf1, gf2, eq);
		}
		addNodes(f1, f2, a);
		associateInput(0, f1.getInputPin(0));
		associateInput(1, f2.getInputPin(0));
		associateOutput(0, a.getOutputPin(0));
	}
	
	@Override
	public EqualFields duplicate(boolean with_state)
	{
		return new EqualFields(m_fields);
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("Equal fields ");
		for (int i = 0; i < m_fields.length; i++)
		{
			if (i > 0)
			{
				out.append(",");
			}
			out.append(m_fields[i]);
		}
		return out.toString();
	}
}
