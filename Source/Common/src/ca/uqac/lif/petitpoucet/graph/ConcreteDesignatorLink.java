package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.DesignatorLink;

public class ConcreteDesignatorLink implements DesignatorLink
{
	/**
	 * The quality of the link
	 */
	protected Quality m_quality;
	
	/**
	 * The object designated by this link
	 */
	protected DesignatedObject m_dob;
	
	public ConcreteDesignatorLink(DesignatedObject dob, Quality q)
	{
		super();
		m_quality = q;
		m_dob = dob;
	}
	
	@Override
	public Quality getQuality() 
	{
		return m_quality;
	}

	@Override
	public DesignatedObject getDesignatedObject()
	{
		return m_dob;
	}

}
