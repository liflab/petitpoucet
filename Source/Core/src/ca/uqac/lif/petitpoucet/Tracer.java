package ca.uqac.lif.petitpoucet;

public interface Tracer
{
	public TraceabilityNode getObjectNode(Designator d, Object o);

	public TraceabilityNode getAndNode();

	public TraceabilityNode getOrNode();

	public TraceabilityNode getUnknownNode();

	public Tracer getSubTracer(Object context);

	public TraceabilityTree trace(TraceabilityQuery q, Designator d, Object o);
}
