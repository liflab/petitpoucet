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

public class CompositePart implements Part
{
	protected final List<Part> m_components;
	
	public static Part compose(Part tail, Part head)
	{
		if (tail == null)
		{
			return head;
		}
		if (tail instanceof CompositePart)
		{
			((CompositePart) tail).add(head);
			return tail;
		}
		return new CompositePart(tail, head);
	}
	
	public CompositePart(Part ... parts)
	{
		super();
		m_components = new ArrayList<>();
		for (Part p : parts)
		{
			add(p);
		}
	}
	
	public CompositePart(List<? extends Part> parts)
	{
		super();
		m_components = new ArrayList<>();
		m_components.addAll(parts);
	}
	
	public void add(Part p)
	{
		if (p instanceof CompositePart)
		{
			CompositePart cp = (CompositePart) p;
			for (Part in_p : cp.m_components)
			{
				add(in_p);
			}
		}
		else
		{
			m_components.add(p);
		}
	}
	
	@Override
	/*@ pure @*/ public int hashCode()
	{
		int h = 0;
		for (Part p : m_components)
		{
			h += p.hashCode();
		}
		return h;
	}
	
	@Override
	/*@ pure @*/ public boolean equals(Object o)
	{
		if ((o instanceof CompositePart))
		{
			CompositePart cp = (CompositePart) o;
			if (cp.size() != size())
			{
				return false;
			}
			for (int i = 0; i < size(); i++)
			{
				if (!m_components.get(i).equals(cp.m_components.get(i)))
				{
					return false;
				}
			}
			return true;
		}
		if (o instanceof Part)
		{
			if (size() == 1)
			{
				return m_components.get(0).equals(o);
			}
			return false;
		}
		return false;
	}
	
	/*@ pure @*/ public int size()
	{
		return m_components.size();
	}
	
	/*@ pure null @*/ public Part head()
	{
		if (m_components.isEmpty())
		{
			return null;
		}
		return m_components.get(m_components.size() - 1);
	}
	
	/*@ pure null @*/ public Part tail()
	{
		if (m_components.size() <= 1)
		{
			return null;
		}
		if (m_components.size() == 2)
		{
			return m_components.get(0).duplicate();
		}
		CompositePart cp = new CompositePart();
		for (int i = 0; i < m_components.size() - 1; i++)
		{
			cp.add(m_components.get(i).duplicate());
		}
		return cp;
	}
	
	@Override
	/*@ pure @*/ public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\u27e8");
		for (int i = 0; i < m_components.size(); i++)
		{
			if (i > 0)
			{
				sb.append("\u2218");
			}
			sb.append(m_components.get(i));
		}
		sb.append("\u27e9");
		return sb.toString();
	}
	
	@Override
	public CompositePart duplicate(boolean with_state)
	{
		CompositePart cp = new CompositePart();
		for (Part p : m_components)
		{
			cp.add(p.duplicate());
		}
		return cp;
	}
}
