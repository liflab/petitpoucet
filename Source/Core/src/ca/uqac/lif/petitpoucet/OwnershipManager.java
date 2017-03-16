/*
  Little Poucet - Trace data elements back to their sources
  Copyright (C) 2017 Sylvain Hallé

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet;

import java.util.HashMap;

public class OwnershipManager extends HashMap<String,Object>
{
	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = 1L;

	public Object getObjectWithId(String id)
	{
		if (containsKey(id))
		{
			return get(id);
		}
		return null;
	}
}
