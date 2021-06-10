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

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Designator related to the n-th element of a vector.
 */
public class NthElement implements Part
{
	/**
	 * The index of the element inside the vector.
	 */
	private final int m_index;
	
	/**
	 * Creates a new designator instance.
	 * @param index The index of the element inside the vector
	 */
	public NthElement(int index)
	{
		super();
		m_index = index;
	}
	
	/**
	 * Gets the input index.
	 * @return The index
	 */
	public int getIndex()
	{
		return m_index;
	}
	
	@Override
	public boolean appliesTo(Object o)
	{
		return o instanceof List;
	}

	@Override
	public Part head()
	{
		return this;
	}

	@Override
	public Part tail()
	{
		return Part.nothing;
	}
	
	@Override
	public String toString()
	{
		return "[" + m_index + "]";
	}
	
	@Override
	public int hashCode()
	{
		return m_index;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof NthElement)
		{
			return ((NthElement) o).m_index == m_index;
		}
		return false;
	}
	
	/**
	 * Creates a new designator by replacing the sequence "m-th element of output 0"
	 * by the sequence "n-th element of input 0" inside a composed designator. 
	 * @param d The designator
	 * @param index The index corresponding to n in the description above
	 * @return The new designator
	 */
	public static Part replaceNthOutputByNthInput(Part d, int index)
	{
		if (!(d instanceof ComposedPart))
		{
			return d; // Nothing to do
		}
		ComposedPart cd = (ComposedPart) d;
		List<Part> desigs = new ArrayList<Part>();
		for (int i = 0; i < cd.size(); i++)
		{
			desigs.add(cd.get(i));
		}
		for (int i = desigs.size() - 1; i >= 1; i--)
		{
			if (desigs.get(i) instanceof NthOutput && desigs.get(i - 1) instanceof NthElement)
			{
				desigs.set(i, NthInput.FIRST);
				desigs.set(i - 1, new NthElement(index));
			}
		}
		return new ComposedPart(desigs);
	}
	
	/**
	 * Retrieves the element of the output vector mentioned in a designator.
	 * If multiple {@link NthElement} are present, the one closest to the
	 * designator mentioning the function's output is kept.
	 * @param d The designator
	 * @return The element index, or -1 if no specific element is mentioned
	 */
	public static int mentionedElement(Part d)
	{
		int index = -1;
		if (d instanceof ComposedPart)
		{
			ComposedPart cd = (ComposedPart) d;
			for (int i = cd.size() - 1; i >= 0; i--)
			{
				Part in_d = cd.get(i);
				if (in_d instanceof NthElement)
				{
					index = ((NthElement) in_d).getIndex();
				}
			}
		}
		return index;
	}
}