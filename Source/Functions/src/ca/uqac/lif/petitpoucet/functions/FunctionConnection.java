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
package ca.uqac.lif.petitpoucet.functions;

import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;

public class FunctionConnection implements CircuitConnection
{
	protected int m_index;

	protected Function m_element;

	protected Object m_value;

	public FunctionConnection(int index, Function element)
	{
		super();
		m_index = index;
		m_element = element;
		m_value = null;
	}

	public void reset()
	{
		m_value = null;
	}

	@Override
	public int getIndex()
	{
		return m_index;
	}

	@Override
	public Function getObject()
	{
		return m_element;
	}

	public Object pullValue()
	{
		if (m_value != null)
		{
			return m_value;
		}
		Object[] outs = m_element.evaluate();
		m_value = outs[m_index];
		return m_value;
	}
}
