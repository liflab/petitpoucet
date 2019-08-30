package ca.uqac.lif.petitpoucet.graph;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;

public abstract class TraceabilityNode
{
  /**
   * A unique ID for the node
   */
  protected int m_id;
  
  /**
   * A counter for unique IDs
   */
  private static int s_idCounter = 0;
  
  /**
   * The links between this object and others
   */
  protected List<LabeledEdge> m_links;
  
  /**
   * Creates a new traceability node with no children
   * @param dob The designated object represented by this node
   */
  public TraceabilityNode()
  {
    super();
    m_id = s_idCounter++;
    m_links = new ArrayList<LabeledEdge>();
  }
  
  /**
   * Gets the node's unique ID
   * @return The ID
   */
  public int getId()
  {
    return m_id;
  }
  
  /**
   * Gets the children of this node
   * @return The list of children
   */
  public List<LabeledEdge> getChildren()
  {
    return m_links;
  }
  
  /**
   * Adds children to this node
   * @param links The list of children
   */
  public void addChildren(List<LabeledEdge> links)
  {
    m_links.addAll(links);
  }
  
  /**
   * Adds children to this node
   * @param links The list of children
   */
  public void addChildren(LabeledEdge ... links)
  {
    for (LabeledEdge node : links)
    {
      m_links.add(node);
    }
  }
  
  @Override
  public int hashCode()
  {
    return m_id;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == null || !(o instanceof TraceabilityNode))
    {
      return false;
    }
    return m_id == ((TraceabilityNode) o).getId();
  }
  
  public static class LabeledEdge
  {
    protected TraceabilityNode m_node;
    
    protected Quality m_quality;
    
    public LabeledEdge(TraceabilityNode node, Quality q)
    {
      super();
      m_node = node;
      m_quality = q;
    }
    
    @Override
    public String toString()
    {
      return "(" + m_node + ")" + m_quality;
    }
  }
}
