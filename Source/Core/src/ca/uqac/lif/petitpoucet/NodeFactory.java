package ca.uqac.lif.petitpoucet;

public interface NodeFactory
{
  public TraceabilityNode getObjectNode(DesignatedObject dob);
  
  public TraceabilityNode getObjectNode(Designator d, Object o);
  
  public TraceabilityNode getAndNode();
  
  public TraceabilityNode getOrNode();
  
  public TraceabilityNode getUnknownNode();
}
