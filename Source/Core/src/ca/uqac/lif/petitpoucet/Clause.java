/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ca.uqac.lif.dag.MathSet;
import ca.uqac.lif.petitpoucet.NodeFactory.ObjectPart;

/**
 * A collection of part nodes designating parts of one or more objects. A
 * clause can be seen as the "conjunction" of all the object part it contains,
 * i.e. the parts taken together. In turn, a collection of clauses can be
 * seen as different alternatives.
 * 
 * @author Sylvain Hallé
 */
public class Clause implements Set<ObjectPart>
{
	/**
	 * The parts.
	 */
	protected final Set<ObjectPart> m_parts;
	
	/**
	 * Distributes the content of two lists of clauses.
	 * @param list1 The first list of clauses
	 * @param list2 The second list of clauses
	 * @return The distributed list of clauses
	 */
	/*@ non_null @*/ protected static Set<Clause> distributePair(/*@ non_null @*/ Set<Clause> list1, /*@ non_null @*/ Set<Clause> list2)
	{
		Set<Clause> distributed = new MathSet<Clause>();
		for (Clause c1 : list1)
		{
			for (Clause c2 : list2)
			{
				distributed.add(c1.mergeWith(c2));
			}
		}
		return distributed;
	}
	
	/*@ non_null @*/ public static Set<Clause> distribute(List<Set<Clause>> lists)
	{
		if (lists.isEmpty())
		{
			return new MathSet<Clause>();
		}
		if (lists.size() == 1)
		{
			return lists.get(0);
		}
		Set<Clause> old_list = lists.get(0);
		for (int i = 1; i < lists.size(); i++)
		{
			old_list = distributePair(old_list, lists.get(i));
		}
		return old_list;
	}
	
	@SafeVarargs
	/*@ non_null @*/ public static Set<Clause> distribute(Set<Clause> ... lists)
	{
		return distribute(Arrays.asList(lists));
	}
	
	/**
	 * Creates a new clause by providing the parts it contains.
	 * @param nodes The parts
	 */
	public Clause(ObjectPart ... nodes)
	{
		super();
		m_parts = new MathSet<ObjectPart>();
		for (ObjectPart pn : nodes)
		{
			m_parts.add(pn);
		}
	}
	
	/**
	 * Creates a new clause by merging the contents of the current clause with
	 * that of another clause.
	 * @param c The clause to merge with the current clause
	 * @return The new clause
	 */
	/*@ pure non_null @*/ public Clause mergeWith(Clause c)
	{
		Clause merged = new Clause();
		merged.addAll(this);
		merged.addAll(c);
		return merged;
	}
	
	/**
	 * Returns the set of all objects mentioned in the part nodes contained in
	 * this clause.
	 * @return The set of objects
	 */
	/*@ pure non_null @*/ public Set<Object> mentionedObjects()
	{
		Set<Object> objects = new MathSet<Object>();
		for (ObjectPart pn : m_parts)
		{
			objects.add(pn.m_subject);
		}
		return objects;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Clause))
		{
			return false;
		}
		Clause c = (Clause) o;
		if (c.size() != size())
		{
			return false;
		}
		for (ObjectPart pn : m_parts)
		{
			if (!c.contains(pn))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int h = 0;
		for (ObjectPart pn : m_parts)
		{
			h += pn.hashCode();
		}
		return h;
	}

	@Override
	public void forEach(Consumer<? super ObjectPart> action)
	{
		m_parts.forEach(action);
	}

	@Override
	public int size()
	{
		return m_parts.size();
	}

	@Override
	public boolean isEmpty()
	{
		return m_parts.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return m_parts.contains(o);
	}

	@Override
	public Iterator<ObjectPart> iterator()
	{
		return m_parts.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return m_parts.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return m_parts.toArray(a);
	}

	@Override
	public boolean add(ObjectPart e)
	{
		return m_parts.add(e);
	}

	@Override
	public boolean remove(Object o)
	{
		return m_parts.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return m_parts.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends ObjectPart> c)
	{
		return m_parts.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return m_parts.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return m_parts.retainAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super ObjectPart> filter)
	{
		return m_parts.removeIf(filter);
	}

	@Override
	public void clear()
	{
		m_parts.clear();
	}

	@Override
	public Spliterator<ObjectPart> spliterator()
	{
		return m_parts.spliterator();
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("{");
		boolean first = true;
		for (ObjectPart pn : m_parts)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				out.append(",");
			}
			out.append(pn);
		}
		out.append("}");
		return out.toString();
	}
}
