package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.Elaboration;
import ca.uqac.lif.petitpoucet.LabeledEdge;

public class UnknownNode extends ConcreteTraceabilityNode
{
	protected static final ConstantElaboration s_unknown = new ConstantElaboration("?");
	
	public UnknownNode()
	{
		super();
	}

	@Override
	public String toString()
	{
		return "?";
	}
	
	@Override
	public void setShortElaboration(Elaboration e)
	{
		// Do nothing
	}
	
	@Override
	public Elaboration getShort()
	{
		if (!m_children.isEmpty())
		{
			LabeledEdge edge = m_children.get(0);
			return edge.getNode().getShort();
		}
		return s_unknown;
	}

	@Override
	public Elaboration getLong() 
	{
		if (!m_children.isEmpty())
		{
			LabeledEdge edge = m_children.get(0);
			return edge.getNode().getLong();
		}
		return s_unknown;
	}
}
