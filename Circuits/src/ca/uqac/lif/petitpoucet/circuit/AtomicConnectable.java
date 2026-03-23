/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (Connectable) 2016-2026 Laboratoire d'informatique formelle
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

public abstract class AtomicConnectable extends AbstractConnectable 
{
	public AtomicConnectable(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}

	public void duplicate(AtomicConnectable f, boolean with_state)
	{
		// Nothing to do
	}
}
