package ca.uqac.lif.petitpoucet;

import java.util.List;

import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;

public interface TraceabilityNode
{
	public void addChild(TraceabilityNode n, Quality q);

	public void addChild(LabeledEdge e);

	public List<LabeledEdge> getChildren();
}
