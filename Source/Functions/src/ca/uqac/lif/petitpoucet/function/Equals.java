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

/**
 * Checks the equality between two values.
 * @author Sylvain Hallé
 */
public class Equals extends AtomicFunction
{
	/**
	 * Creates a new instance of the function.
	 */
	public Equals()
	{
		super(2, 1);
	}

	@Override
	protected Object[] getValue(Object... inputs) throws InvalidNumberOfArgumentsException
	{
		return new Object[] {isEqualTo(inputs[0], inputs[1])};
	}
	
	/**
	 * Determines if two objects are equal.
	 * @param o1 The first object
	 * @param o2 The second object
	 * @return {@code true} if the objects are considered equal, {@code false}
	 * otherwise
	 */
	public static boolean isEqualTo(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
		{
			return true;
		}
		if (o1 == null || o2 == null)
		{
			return false;
		}
		if (o1 instanceof Number && o2 instanceof Number)
		{
			return ((Number) o1).floatValue() == ((Number) o2).floatValue();
		}
		if (o1 instanceof String && o2 instanceof String)
		{
			return ((String) o1).compareTo((String) o2) == 0;
		}
		return o1.equals(o2);
	}
	
	@Override
	public Equals duplicate(boolean with_state)
	{
		Equals e = new Equals();
		copyInto(e, with_state);
		return e;
	}
	
	@Override
	public String toString()
	{
		return "=";
	}
}
