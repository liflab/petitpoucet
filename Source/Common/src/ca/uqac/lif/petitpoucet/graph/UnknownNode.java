package ca.uqac.lif.petitpoucet.graph;

public class UnknownNode extends ConcreteTraceabilityNode
{
	public UnknownNode()
	{
		super();
	}

	@Override
	public String toString()
	{
		return "?";
	}
}
