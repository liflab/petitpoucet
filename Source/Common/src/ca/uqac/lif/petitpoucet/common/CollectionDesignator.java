package ca.uqac.lif.petitpoucet.common;

import java.util.List;

public abstract class CollectionDesignator
{
	/**
	 * Designator representing the n-th element of an ordered collection.
	 */
	public static class NthElement extends NthOf
	{
		/**
		 * Creates a new instance of the designator
		 * @param index The number of the line to designate
		 */
		public NthElement(int index)
		{
			super(index);
		}
		
		@Override
		public String toString()
		{
			return super.toString() + " element";
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
	}
}
