package ca.uqac.lif.petitpoucet.common;

import ca.uqac.lif.petitpoucet.Designator;

/**
 * Designator pointing to the n-th element of some compound object.
 * @author Sylvain Hallé
 */
public abstract class NthOf implements Designator
{
	/**
	 * The line index
	 */
	protected int m_index;
	
	/**
	 * Creates a new instance of the designator
	 * @param index The number of the line to designate
	 */
	public NthOf(int index)
	{
		super();
		m_index = index;
	}
	
	/**
	 * Gets the line index
	 * @return The index
	 */
	public int getIndex()
	{
		return m_index;
	}
	
	@Override
	public String toString()
	{
		return m_index + "-th";
	}
}
