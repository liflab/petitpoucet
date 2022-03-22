/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.function.number;

/**
 * Determines if a number is less than another one.
 * @author Sylvain Hallé
 */
public class IsLessThan extends NumberComparison
{
	/**
	 * Creates a new instance of the function.
	 */
	public IsLessThan()
	{
		super();
	}
	
	@Override
	public String toString()
	{
		return "&lt;";
	}
	
	@Override
	public IsLessThan duplicate(boolean with_state)
	{
		IsLessThan f = new IsLessThan();
		copyInto(f, with_state);
		return f;
	}

	@Override
	protected boolean compare(Number n1, Number n2)
	{
		return n1.doubleValue() < n2.doubleValue();
	}
}