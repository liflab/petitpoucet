package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;

public class ConcreteDesignatedObject implements DesignatedObject
{
	/**
	 * The object that is designated
	 */
	protected Object m_object;
	
	/**
	 * The part of the object that is designated
	 */
	protected Designator m_designator;
	
	/**
	 * Creates a new concrete designated object
	 * @param d The part of the object that is designated
	 * @param o The object that is designated
	 */
	public ConcreteDesignatedObject(Designator d, Object o)
	{
		super();
		m_object = o;
		m_designator = d;
	}
	
	@Override
	public Object getObject()
	{
		return m_object;
	}

	@Override
	public Designator getDesignator() 
	{
		return m_designator;
	}
	
	@Override
	public int hashCode()
	{
		if (m_object == null)
		{
			return m_designator.hashCode();
		}
		return m_object.hashCode() + m_designator.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ConcreteDesignatedObject))
		{
			return false;
		}
		ConcreteDesignatedObject cdo = (ConcreteDesignatedObject) o;
		return (m_object == null && cdo.m_object == null) || (m_object != null && m_object.equals(cdo.m_object))
				&& m_designator.equals(cdo.m_designator);
	}
}
