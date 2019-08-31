package ca.uqac.lif.petitpoucet;

public interface Tracer
{
  public TraceabilityNode getObjectNode(DesignatedObject dob);
  
  public TraceabilityNode getObjectNode(Designator d, Object o);
  
  public TraceabilityNode getAndNode();
  
  public TraceabilityNode getOrNode();
  
  public TraceabilityNode getUnknownNode();
  
  public Tracer getSubTracer();
  
  public TraceabilityTree trace(TraceabilityQuery q, Designator d, Object o);
}
