package ca.uqac.lif.petitpoucet.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.petitpoucet.Designator;

public abstract class CollectionDesignator
{
	/**
	 * Designator representing the n-th element of an ordered collection.
	 */
	public static class NthElement extends NthOf
	{
		protected static final transient Map<Integer,NthElement> s_pool = new LinkedHashMap<Integer,NthElement>();
		
		public static NthElement get(int index)
		{
			NthElement ne = s_pool.get(index);
			if (ne == null)
			{
				ne = new NthElement(index);
				s_pool.put(index, ne);
			}
			return ne;
		}
		
		/**
		 * Creates a new instance of the designator
		 * 
		 * @param index
		 *          The number of the line to designate
		 */
		protected NthElement(int index)
		{
			super(index);
		}

		@Override
		public String toString()
		{
			return "Element " + super.toString();
		}

		@Override
		public boolean appliesTo(Object o)
		{
			return o != null && o instanceof List;
		}

		@Override
		public int hashCode()
		{
			return 3 * m_index;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof NthElement))
			{
				return false;
			}
			NthElement cd = (NthElement) o;
			return cd.m_index == m_index;
		}

		@Override
		public Designator peek()
		{
			return this;
		}

		@Override
		public Designator tail()
		{
			return null;
		}
	}
}
