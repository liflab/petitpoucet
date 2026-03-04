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
package ca.uqac.lif.petitpoucet;

/**
 * Interface implemented by objects that can create a copy of themselves.
 * The copy can either by stateless (by default) or stateful.
 * @author Sylvain Hallé
 */
public interface Duplicable
{
	/**
	 * Creates a copy of the object.
	 * @param with_state Set to {@code true} for a stateful copy, {@link false}
	 * otherwise.
	 * @return The copy
	 */
	public Object duplicate(boolean with_state);
	
	/**
	 * Creates a stateless copy of the object.
	 * @return The copy
	 */
	public default Object duplicate()
	{
		return duplicate(false);
	}
}
