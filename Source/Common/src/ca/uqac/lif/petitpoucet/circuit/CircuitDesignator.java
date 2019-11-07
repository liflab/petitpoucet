package ca.uqac.lif.petitpoucet.circuit;

import java.util.LinkedHashMap;
import java.util.Map;

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
		protected static final transient Map<Integer,NthInput> s_pool = new LinkedHashMap<Integer,NthInput>();
		
		/**
		 * Gets an instance of the NthInput designator
		 * @param index The index of the input
		 * @return The instance
		 */
		public static NthInput get(int index)
		{
			NthInput ni = s_pool.get(index);
			if (ni == null)
			{
				ni = NthInput.get(index);
				s_pool.put(index, ni);
			}
			return ni;
		}
		
		/**
		 * Creates a new instance of the designator
		 * 
		 * @param index
		 *          The number of the line to designate
		 */
		private NthInput(int index)
		{
			super(index);
		}

		@Override
		public String toString()
		{
			return "Input " + super.toString();
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

	/**
	 * Designates the n-th output of a circuit element
	 */
	public static class NthOutput extends NthOf
	{
		protected static final transient Map<Integer,NthOutput> s_pool = new LinkedHashMap<Integer,NthOutput>();
		
		/**
		 * Gets an instance of the NthOutput designator
		 * @param index The index of the output
		 * @return The instance
		 */
		public static NthOutput get(int index)
		{
			NthOutput ni = s_pool.get(index);
			if (ni == null)
			{
				ni = NthOutput.get(index);
				s_pool.put(index, ni);
			}
			return ni;
		}
		
		/**
		 * Creates a new instance of the designator
		 * 
		 * @param index
		 *          The number of the line to designate
		 */
		private NthOutput(int index)
		{
			super(index);
		}

		@Override
		public String toString()
		{
			return "Output " + super.toString();
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
