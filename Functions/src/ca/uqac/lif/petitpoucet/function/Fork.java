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
package ca.uqac.lif.petitpoucet.function;

public class Fork extends AtomicFunction
{
	public Fork(int arity)
	{
		super(1, arity);
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs)
	{
		for (int i = 0; i < m_outs.size(); i++)
		{
			outputs[i] = inputs[0];
		}
	}

	@Override
	public Fork duplicate(boolean with_state)
	{
		return new Fork(m_outs.size());
	}
}
