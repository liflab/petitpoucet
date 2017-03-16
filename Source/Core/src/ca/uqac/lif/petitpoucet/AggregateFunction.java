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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AggregateFunction implements NodeFunction
{
	/**
	 * The list of nodes the function is applied on
	 */
	protected final List<NodeFunction> m_nodes;
	
	/**
	 * The name of the aggregate function
	 */
	protected final String m_name;
	
	public AggregateFunction(String name, Collection<NodeFunction> nodes)
	{
		super();
		m_name = name;
		m_nodes =  new ArrayList<NodeFunction>(nodes.size());
		m_nodes.addAll(nodes);
	}
	
	public AggregateFunction(String name, NodeFunction ... nodes)
	{
		super();
		m_name = name;
		m_nodes =  new ArrayList<NodeFunction>(nodes.length);
		for (NodeFunction nf : nodes)
		{
			m_nodes.add(nf);
		}
	}
	
	public List<NodeFunction> getDependencyNodes()
	{
		return m_nodes;
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}

	@Override
	public String getDataPointId()
	{
		return "";
	}

	@Override
	public NodeFunction dependsOn() 
	{
		return this;
	}
	
	@Override
	public int hashCode()
	{
		int code = 0;
		for (NodeFunction nf : m_nodes)
		{
			code += nf.hashCode();
		}
		return code;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof AggregateFunction))
		{
			return false;
		}
		AggregateFunction af = (AggregateFunction) o;
		return af.getDataPointId().compareTo(getDataPointId()) == 0;
	}
}
