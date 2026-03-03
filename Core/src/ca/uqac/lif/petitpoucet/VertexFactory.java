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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;

public class VertexFactory
{
	protected final VertexFactory m_parent;
	
	protected final List<VertexFactory> m_children;
	
	protected final List<Vertex> m_vertices;
	
	public VertexFactory()
	{
		this(null);
	}
	
	protected VertexFactory(VertexFactory vf)
	{
		super();
		m_parent = vf;
		m_children = new ArrayList<>();
		m_vertices = new ArrayList<>();
	}
	
	public PartVertex getPart(Part p, Object s)
	{
		return getPart(new PartVertex(p, s));
	}
	
	public PartVertex getPart(PartVertex v)
	{
		int i = m_vertices.indexOf(v);
		if (i < 0)
		{
			m_vertices.add(v);
			return v;
		}
		return (PartVertex) m_vertices.get(i);
	}
	
	public AndVertex getAnd()
	{
		return new AndVertex();
	}
	
	public OrVertex getOr()
	{
		return new OrVertex();
	}
	
	public VertexFactory subfactory()
	{
		VertexFactory vf = new VertexFactory(this);
		m_children.add(vf);
		return vf;
	}
}
