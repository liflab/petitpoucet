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
package ca.uqac.lif.petitpoucet;

import java.util.ArrayList;
import java.util.List;

/**
 * Part made of a composition of other "atomic" parts. This part is composed
 * of a "head" and a "tail". The head is a {@link Part} that points to a part
 * of an object. The tail is a sequence of parts that further refine that part
 * into a smaller part.
 * @author Sylvain Hallé
 */
public class ComposedPart implements Part
{
	/**
	 * The list of designators representing the composition. The head of the
	 * designator is the last element of the list, while the tail of the
	 * designator is the sub-list excluding the last element.
	 */
	protected List<Part> m_designators;
	
	/**
	 * Creates a flat composed designator out of a list of designators.
	 * The head of the designator is the last of the list; that is, a list of
	 * parts &pi;<sub>1</sub>, &pi;<sub>2</sub>, &pi;<sub>3</sub> is to be
	 * interpreted as "part &pi;<sub>1</sub> of part &pi;<sub>2</sub> of
	 * part &pi;<sub>3</sub>"; the "largest" part is &pi;<sub>3</sub>, and
	 * &pi;<sub>1</sub> &compfn; &pi;<sub>2</sub> are further refinements of
	 * that part. 
	 * @param designators The list of designators
	 * @return A new designator
	 */
	public static Part compose(Part... designators)
	{
		if (designators.length == 0)
		{
			return Part.nothing;
		}
		List<Part> l_designators = new ArrayList<Part>();
		for (int i = 0; i < designators.length; i++)
		{
			if (designators[i] == null)
			{
				continue;
			}
			if (designators[i] instanceof ComposedPart)
			{
				l_designators.addAll(((ComposedPart) designators[i]).m_designators);
			}
			else
			{
				l_designators.add(designators[i]);
			}
		}
		if (l_designators.isEmpty())
		{
			return Part.nothing;
		}
		if (l_designators.size() == 1)
		{
			return l_designators.get(0);
		}
		if (l_designators.contains(Part.nothing))
		{
			// Composing "nothing" with anything else is "nothing"
			return Part.nothing;
		}
		return new ComposedPart(l_designators);
	}
	
	/**
	 * Creates a flat composed designator out of a list of designators.
	 * @param designators The list of designators
	 * @return A new designator
	 */
	public static Part compose(List<Part> designators)
	{
		Part[] parts = new Part[designators.size()];
		for (int i = 0; i < parts.length; i++)
		{
			parts[i] = designators.get(i);
		}
		return compose(parts);
	}

	/**
	 * Creates a new composed designator
	 * 
	 * @param designators
	 *          The list of designators representing the composition
	 */
	public ComposedPart(Part... designators)
	{
		super();
		m_designators = new ArrayList<Part>(designators.length);
		for (Part d : designators)
		{
			add(d);
		}
	}
	
	/**
	 * Creates a new composed designator
	 * 
	 * @param designators
	 *          The list of designators representing the composition
	 */
	public ComposedPart(List<Part> designators)
	{
		super();
		m_designators = new ArrayList<Part>(designators.size());
		for (Part d : designators)
		{
			add(d);
		}
	}

	/**
	 * Adds a new designator to the composition
	 * 
	 * @param d
	 *          The designator to add. If null, the operation is simply ignored.
	 * @return This composed designator
	 */
	public ComposedPart add(/* @ null @ */ Part d)
	{
		if (d == null)
		{
			return this;
		}
		if (d instanceof ComposedPart)
		{
			// Don't unnecessarily nest composed designators
			m_designators.addAll(((ComposedPart) d).m_designators);
		}
		else
		{
			m_designators.add(d);
		}
		return this;
	}

	/**
	 * Creates a new designator by removing the first element of the composition
	 * 
	 * @return A new designator with the first element of the composition removed
	 */
	public Part tail()
	{
		switch (m_designators.size())
		{
		case 1:
			return Part.all;
		case 2:
			return m_designators.get(0);
		default:
			ComposedPart cd = new ComposedPart();
			for (int i = 0; i < m_designators.size() - 1; i++)
			{
				cd.add(m_designators.get(i));
			}
			return cd;
		}
	}

	/**
	 * Gets the first designator of the composition, without removing it
	 * 
	 * @return The first designator, or <tt>null</tt> if the composition is empty
	 */
	/* @ pure null @ */ public Part head()
	{
		if (m_designators.isEmpty())
		{
			return null;
		}
		return m_designators.get(m_designators.size() - 1);
	}
	
	/**
	 * Determines if a chain of designators contains a particular designator.
	 * @param d The designator to look for
	 * @return {@code true} if the designator is present, {@code false} otherwise
	 */
	public boolean contains(/*@ non_null @*/ Part d)
	{
		for (Part in_d : m_designators)
		{
			if (in_d.equals(d))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Extracts a part from a sub-list of the composed part.
	 * @param start_index The start index
	 * @param end_index The end index
	 * @return The part corresponding to this interval
	 */
	public Part subPart(int start_index, int end_index)
	{
		List<Part> sub_parts = m_designators.subList(start_index, end_index);
		return ComposedPart.compose(sub_parts);
	}
	
	/**
	 * Gets the designator at a given position in the composition.
	 * @param index The index
	 * @return The designator
	 */
	/*@ pure @*/  public Part get(int index)
	{
		return m_designators.get(index);
	}
	
	/**
	 * Gets the size of the composed designator.
	 * @return The size
	 */
	/*@ pure @*/ public int size()
	{
		return m_designators.size();
	}

	@Override
	/*@ pure @*/ public String toString()
	{
		StringBuilder out = new StringBuilder();
		Part previous = null;
		for (int i = 0; i < m_designators.size(); i++)
		{
			if (i > 0 && (previous != null && !(previous instanceof All)))
			{
				out.append(" ∘ ");
			}
			previous = m_designators.get(i);
			out.append(previous);
		}
		return out.toString();
	}

	@Override
	public int hashCode()
	{
		int h = 0;
		for (Part d : m_designators)
		{
			h += d.hashCode();
		}
		return h;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ComposedPart))
		{
			return false;
		}
		ComposedPart cd = (ComposedPart) o;
		if (cd.m_designators.size() != m_designators.size())
		{
			return false;
		}
		for (int i = 0; i < m_designators.size(); i++)
		{
			if (!m_designators.get(i).equals(cd.m_designators.get(i)))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean appliesTo(Object o)
	{
		if (m_designators.isEmpty())
		{
			return false;
		}
		Part d = m_designators.get(m_designators.size() - 1);
		return d.appliesTo(o);
	}
}
