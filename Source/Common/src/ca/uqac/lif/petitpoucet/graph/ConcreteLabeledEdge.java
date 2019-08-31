package ca.uqac.lif.petitpoucet.graph;

import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.TraceabilityNode;

public class ConcreteLabeledEdge implements LabeledEdge
{
  protected Quality m_quality;
  
  protected TraceabilityNode m_node;
  
  public ConcreteLabeledEdge(TraceabilityNode node, Quality q)
  {
    super();
    m_node = node;
    m_quality = q;
  }
  
  @Override
  public Quality getQuality()
  {
    return m_quality;
  }

  @Override
  public TraceabilityNode getNode()
  {
    return m_node;
  }

  @Override
  public String toString()
  {
    return m_node + ":" + m_quality;
  }
}
