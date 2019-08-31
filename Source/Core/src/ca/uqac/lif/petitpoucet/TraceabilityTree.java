package ca.uqac.lif.petitpoucet;

import java.util.List;

public interface TraceabilityTree
{
	public TraceabilityNode getRoot();

	public List<TraceabilityNode> getLeaves();
}
