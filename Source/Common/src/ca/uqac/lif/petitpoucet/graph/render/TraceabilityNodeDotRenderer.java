package ca.uqac.lif.petitpoucet.graph.render;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.graph.AndNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge;

public class TraceabilityNodeDotRenderer implements TraceabilityNodeRenderer<String>
{

  @Override
  public String render(ConcreteTraceabilityNode root)
  {
    Set<ConcreteTraceabilityNode> visited = new HashSet<ConcreteTraceabilityNode>();
    Queue<ConcreteTraceabilityNode> to_visit = new ArrayDeque<ConcreteTraceabilityNode>();
    to_visit.add(root);
    StringBuilder out = new StringBuilder();
    out.append("digraph G {\n");
    out.append("node [style=\"filled\"];\n");
    visit(out, to_visit, visited);
    out.append("}");
    return out.toString();
  }

  protected void visit(StringBuilder out, Queue<ConcreteTraceabilityNode> to_visit, Set<ConcreteTraceabilityNode> visited)
  {
    while (!to_visit.isEmpty())
    {
      ConcreteTraceabilityNode n = to_visit.remove();
      if (visited.contains(n))
      {
        continue;
      }
      visited.add(n);
      int s_id = n.getId();
      if (n instanceof ConcreteObjectNode)
      {
        DesignatedObject dob = ((ConcreteObjectNode) n).getDesignatedObject();
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
        if (ql.getQuality() == Quality.OVER)
        {
          link_color = "red";
        }
        if (ql.getQuality() == Quality.UNDER)
        {
          link_color = "blue";
        }
        ConcreteTraceabilityNode child_node = (ConcreteTraceabilityNode) ql.getNode();
        out.append(s_id).append(" -> ").append(child_node.getId()).append(" [color=\"").append(link_color).append("\"];\n");
        if (!visited.contains(child_node))
        {
          to_visit.add(child_node);
        }
      }
    }
  }
}
