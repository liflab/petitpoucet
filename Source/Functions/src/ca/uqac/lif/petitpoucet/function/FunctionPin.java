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
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.dag.Connectable;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.util.Duplicable;

public abstract class FunctionPin<T extends Connectable> extends Pin<T> implements Duplicable
{
	protected Object m_value;

	protected boolean m_evaluated;

	protected FunctionPin(T function, int index)
	{
		super(function, index);
		m_evaluated = false;
		m_value = null;
	}

	public void copyInto(FunctionPin<?> pin, boolean with_state)
	{
		if (with_state)
		{
			pin.m_evaluated = m_evaluated;
			pin.m_value = m_value;
		}
	}

	public void reset()
	{
		m_evaluated = false;
	}

	public void setValue(Object v)
	{
		m_evaluated = true;
		m_value = v;
	}
	
	/*@ pure @*/ public boolean isEvaluated()
	{
		return m_evaluated;
	}

	@Override
	public FunctionPin<T> duplicate()
	{
		return duplicate(false);
	}

	@Override
	public abstract FunctionPin<T> duplicate(boolean with_state);

	public abstract Object getValue();
}
