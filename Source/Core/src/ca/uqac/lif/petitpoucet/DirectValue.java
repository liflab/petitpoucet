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

import java.util.Collection;

public class DirectValue extends AggregateFunction 
{
	public static final transient DirectValue instance = new DirectValue();
	
	public DirectValue(Collection<NodeFunction> nodes)
	{
		super(getCaption(nodes.size()), nodes);
	}
	
	public DirectValue(NodeFunction ... nodes)
	{
		super(getCaption(nodes.length), nodes);
	}
	
	public void add(NodeFunction node)
	{
		m_nodes.add(node);
	}
	
	protected static String getCaption(int n)
	{
		if (n > 1)
		{
			return "The values of";
		}
		return "The value of";
	}	
}
