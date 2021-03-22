package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.Elaboration;

public class ConstantElaboration implements Elaboration
{
	protected Object m_object;

	public ConstantElaboration(Object o)
	{
		super();
		m_object = o;
	}

	@Override
	public ConstantElaboration getShort()
	{
		return this;
	}

	@Override
	public ConstantElaboration getLong()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return m_object.toString();
	}
}