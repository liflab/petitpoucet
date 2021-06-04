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
package ca.uqac.lif.util;

/**
 * Interface implemented by objects that can be assigned context elements, i.e.
 * a memory made of key-value pairs. Keys are non-null strings, and values are
 * any object (including null).
 */
public interface Contextualizable
{
	/**
	 * Gets a value from the object's context.
	 * @param key The key
	 * @return The value
	 */
	/*@ null @*/ public Object getContext(/*@ non_null @*/ String key);

	/**
	 * Sets a value for a key in the object's context.
	 * @param key The key
	 * @param value The value
	 */
	public void setContext(/*@ non_null @*/ String key, /*@ null @*/ Object value);
}
