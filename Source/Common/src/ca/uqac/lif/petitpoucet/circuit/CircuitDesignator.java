package ca.uqac.lif.petitpoucet.circuit;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.common.NthOf;

public abstract class CircuitDesignator implements Designator 
{
	@Override
	public boolean appliesTo(Object o)
	{
		return o != null && o instanceof CircuitElement;
	}
	
	/**
	 * Designates the n-th input of a circuit element
	 */
	public static class NthInput extends NthOf
	{
		/**
		 * Creates a new instance of the designator
		 * @param index The number of the line to designate
		 */
		public NthInput(int index)
		{
			super(index);
		}
		
		@Override
		public String toString()
		{
			return super.toString() + " input";
		}

		@Override
		public boolean appliesTo(Object o) 
		{
			return o != null && o instanceof CircuitElement;
		}
		
		@Override
    public int hashCode()
    {
      return 13 * m_index;
    }
    
    @Override
    public boolean equals(Object o)
    {
      if (o == null || !(o instanceof NthInput))
      {
        return false;
      }
      NthInput cd = (NthInput) o;
      return cd.m_index == m_index;
    }
	}
	
	/**
	 * Designates the n-th output of a circuit element
	 */
	public static class NthOutput extends NthOf
	{
		/**
		 * Creates a new instance of the designator
		 * @param index The number of the line to designate
		 */
		public NthOutput(int index)
		{
			super(index);
		}
		
		@Override
		public String toString()
		{
			return super.toString() + " output";
		}

		@Override
		public boolean appliesTo(Object o) 
		{
			return o != null && o instanceof CircuitElement;
		}
		
		@Override
    public int hashCode()
    {
      return 17 * m_index;
    }
    
    @Override
    public boolean equals(Object o)
    {
      if (o == null || !(o instanceof NthOutput))
      {
        return false;
      }
      NthOutput cd = (NthOutput) o;
      return cd.m_index == m_index;
    }
	}
}
