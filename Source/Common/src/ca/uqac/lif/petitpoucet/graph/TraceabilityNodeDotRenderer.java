package ca.uqac.lif.petitpoucet.graph;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.graph.TraceabilityNode.LabeledEdge;

public class TraceabilityNodeDotRenderer implements TraceabilityNodeRenderer<String>
{

  @Override
  public String render(ObjectNode root)
  {
    Set<TraceabilityNode> visited = new HashSet<TraceabilityNode>();
    Queue<TraceabilityNode> to_visit = new ArrayDeque<TraceabilityNode>();
    to_visit.add(root);
    StringBuilder out = new StringBuilder();
    out.append("digraph G {\n");
    out.append("node [style=\"filled\"];\n");
    visit(out, to_visit, visited);
    out.append("}");
    return out.toString();
  }

  protected void visit(StringBuilder out, Queue<TraceabilityNode> to_visit, Set<TraceabilityNode> visited)
  {
    while (!to_visit.isEmpty())
    {
      TraceabilityNode n = to_visit.remove();
      if (visited.contains(n))
      {
        continue;
      }
      visited.add(n);
      int s_id = n.getId();
      if (n instanceof ObjectNode)
      {
        DesignatedObject dob = ((ObjectNode) n).getDesignatedObject();
        out.append(s_id).append(" [label=\"").append(dob).append("\",shape=\"rectangle\",fillcolor=\"AliceBlue\"];\n"); 
      }
      else
      {
        // "or" node or "and" node
        String fill_color = "yellow";
        if (n instanceof AndNode)
        {
          fill_color = "green";
        }
        out.append(s_id).append(" [label=\"").append(n).append("\",shape=\"circle\",color=\"orange\",fillcolor=\"").append(fill_color).append("\",width=.3,fixedsize=\"true\"];\n");
      }
      String link_color = "black";
      for (LabeledEdge ql : n.getChildren())
      {
        if (ql.m_quality == Quality.OVER)
        {
          link_color = "red";
        }
        if (ql.m_quality == Quality.UNDER)
        {
          link_color = "blue";
        }
        out.append(s_id).append(" -> ").append(ql.m_node.getId()).append(" [color=\"").append(link_color).append("\"];\n");
        if (!visited.contains(ql.m_node))
        {
          to_visit.add(ql.m_node);
        }
      }
    }
  }
}
