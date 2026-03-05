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

/**
 * A {@link Part} that is made of several other parts. The class provides methods to
 * extract the head and tail of the composite part, as well as to add new
 * parts to the composition.
 * @author Sylvain Hallé
 */
public class CompositePart implements Part
{
	/**
	 * Returns the head of the part, which is the first part of the composition. If
	 * the part is a composite part, returns the head of the composite part. Otherwise,
	 * returns the part itself.
	 * @param p The part to get the head of
	 * @return The head of the part
	 */
	public static Part head(Part p)
	{
		if (p instanceof CompositePart)
		{
			return ((CompositePart) p).head();
		}
		return p;
	}
	
	/**
	 * Returns the tail of the part, which is the second part of the composition. If
	 * the part is a composite part, returns the tail of the composite part. Otherwise,
	 * returns null.
	 * @param p The part to get the tail of
	 * @return The tail of the part, or null if the part is not a composite part
	 */
	public static Part tail(Part p)
	{
		if (p instanceof CompositePart)
		{
			return ((CompositePart) p).tail();
		}
		return null;
	}
	
	/**
	 * The list of components of the composite part.
	 */
	protected final List<Part> m_components;
	
	/**
	 * Composes two parts together. If the first part is a composite part, the second
	 * part is added to it. Otherwise, a new composite part is created with the two
	 * parts as components.
	 * @param tail The first part
	 * @param head The second part
	 * @return The composition of the two parts
	 */
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
	
	/**
	 * Creates a new composite part with the given components.
	 * @param parts The components of the composite part
	 */
	public CompositePart(Part ... parts)
	{
		super();
		m_components = new ArrayList<>();
		for (Part p : parts)
		{
			add(p);
		}
	}
	
	/**
	 * Creates a new composite part with the given components.
	 * @param parts The components of the composite part
	 */
	public CompositePart(List<? extends Part> parts)
	{
		super();
		m_components = new ArrayList<>();
		m_components.addAll(parts);
	}
	
	/**
	 * Adds a part to the composite part. If the part is itself a composite part,
	 * its components are added to the current composite part instead.
	 * @param p The part to add
	 */
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
	
	/**
	 * Gets the number of components in the composite part.
	 * @return The number of components
	 */
	/*@ pure @*/ public int size()
	{
		return m_components.size();
	}
	
	/**
	 * Gets the head of the composite part, which is the <em>last</em> component.
	 * If the composite part is empty, returns {@code null}.
	 * @return The head of the composite part
	 */
	/*@ pure null @*/ public Part head()
	{
		if (m_components.isEmpty())
		{
			return null;
		}
		return m_components.get(m_components.size() - 1);
	}
	
	/**
	 * Gets the tail of the composite part, which is the composition of all components
	 * except the head. If the composite part has only one component, returns a
	 * duplicate of that component. If the composite part is empty, returns
	 * {@code null}.
	 * @return The tail of the composite part
	 */
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
