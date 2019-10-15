package ca.uqac.lif.petitpoucet;

import java.util.List;

public interface Tracer
{
	public TraceabilityNode getObjectNode(Designator d, Object o);

	public TraceabilityNode getAndNode();

	public TraceabilityNode getOrNode();

	public TraceabilityNode getUnknownNode();
	
	public List<TraceabilityNode> unknownLink(TraceabilityNode root);

	public Tracer getSubTracer(Object context);

	public TraceabilityTree trace(TraceabilityQuery q, Designator d, Object o);
}
