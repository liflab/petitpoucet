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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A collection of part nodes designating parts of one or more objects.
 * 
 * @author Sylvain Hallé
 */
public class Clause implements List<PartNode>
{
	/**
	 * The parts.
	 */
	protected final List<PartNode> m_parts;
	
	/**
	 * Creates a new clause by providing the parts it contains.
	 * @param nodes The parts
	 */
	public Clause(PartNode ... nodes)
	{
		super();
		m_parts = new ArrayList<PartNode>();
		for (PartNode pn : nodes)
		{
			m_parts.add(pn);
		}
	}

	@Override
	public void forEach(Consumer<? super PartNode> action)
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
	public Iterator<PartNode> iterator()
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
	public boolean add(PartNode e)
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
	public boolean addAll(Collection<? extends PartNode> c)
	{
		return m_parts.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends PartNode> c)
	{
		return m_parts.addAll(index, c);
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
	public void replaceAll(UnaryOperator<PartNode> operator)
	{
		m_parts.replaceAll(operator);
	}

	@Override
	public boolean removeIf(Predicate<? super PartNode> filter)
	{
		return m_parts.removeIf(filter);
	}

	@Override
	public void sort(Comparator<? super PartNode> c)
	{
		m_parts.sort(c);
	}

	@Override
	public void clear()
	{
		m_parts.clear();
	}

	@Override
	public boolean equals(Object o)
	{
		return m_parts.equals(o);
	}

	@Override
	public int hashCode()
	{
		return m_parts.hashCode();
	}

	@Override
	public PartNode get(int index)
	{
		return m_parts.get(index);
	}

	@Override
	public PartNode set(int index, PartNode element)
	{
		return m_parts.set(index, element);
	}
	
	@Override
	public void add(int index, PartNode element)
	{
		m_parts.add(index, element);
	}

	@Override
	public Stream<PartNode> stream()
	{
		return m_parts.stream();
	}

	@Override
	public PartNode remove(int index)
	{
		return m_parts.remove(index);
	}

	@Override
	public Stream<PartNode> parallelStream()
	{
		return m_parts.parallelStream();
	}

	@Override
	public int indexOf(Object o)
	{
		return m_parts.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return m_parts.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<PartNode> listIterator()
	{
		return m_parts.listIterator();
	}

	@Override
	public ListIterator<PartNode> listIterator(int index)
	{
		return m_parts.listIterator(index);
	}

	@Override
	public List<PartNode> subList(int fromIndex, int toIndex)
	{
		return m_parts.subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<PartNode> spliterator()
	{
		return m_parts.spliterator();
	}
}
