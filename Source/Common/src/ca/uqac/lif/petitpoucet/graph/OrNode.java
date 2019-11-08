package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.LabeledEdge;

public class OrNode extends ConcreteTraceabilityNode
{
	public OrNode()
	{
		super();
	}

	@Override
	public String toString()
	{
		return toString("");
	}
	
	@Override
	protected String toString(String indent)
	{
		StringBuilder out = new StringBuilder();
		out.append(indent).append("∨").append("\n");
		for (LabeledEdge le : m_children)
		{
			out.append(indent).append(le.getQuality());
			out.append(((ConcreteTraceabilityNode) le.getNode()).toString(indent + " "));
		}
		return out.toString();
	}
}
