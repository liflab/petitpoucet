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
package ca.uqac.lif.petitpoucet.function.vector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class VectorTestUtilities
{
	@Test
	public void dummyTest()
	{
		// Do nothing
	}
	
	/**
	 * Creates a list object out of a list of arguments.
	 * @param arguments The arguments
	 * @return The list containing the arguments
	 */
	public static List<?> getList(Object ... arguments)
	{
		List<Object> list = new ArrayList<Object>(arguments.length);
		for (Object o : arguments)
		{
			list.add(o);
		}
		return list;
	}
	
}
