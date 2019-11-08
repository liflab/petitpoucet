package ca.uqac.lif.petitpoucet.graph.render;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.graph.AndNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteLabeledEdge;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge;

public class TraceabilityNodeDotRenderer implements TraceabilityNodeRenderer<String>
{
	boolean m_flatten = true;
	
	CaptionStyle m_showCaptions = CaptionStyle.FULL;
	
	@Override
	public void setFlatten(boolean b)
	{
		m_flatten = b;
	}
	
	@Override
	public void setShowCaptions(CaptionStyle s)
	{
		m_showCaptions = s;
	}
	
	@Override
	public String render(ConcreteTraceabilityNode root)
	{
		Set<ConcreteTraceabilityNode> visited = new HashSet<ConcreteTraceabilityNode>();
		Queue<ConcreteTraceabilityNode> to_visit = new ArrayDeque<ConcreteTraceabilityNode>();
		to_visit.add(root);
		StringBuilder out = new StringBuilder();
		out.append("digraph G {\n");
		out.append(" node [style=\"filled\"];\n");
		visit(out, to_visit, visited);
		out.append("}");
		return out.toString();
	}
	
	protected String formatCaption(DesignatedObject dob, CaptionStyle style)
	{
		if (style == CaptionStyle.NONE)
		{
			return "";
		}
		if (style == CaptionStyle.FULL)
		{
			return dob.toString();
		}
		// Short
		Designator d = dob.getDesignator().peek();
		if (d instanceof NthInput || d instanceof NthOutput)
		{
			return dob.getObject().toString();
		}
		return dob.toString();
	}
	
	protected String formatColor(DesignatedObject dob)
	{
		String color = "AliceBlue";
		Designator d = dob.getDesignator().peek();
		if (d instanceof NthInput)
		{
			color = "Tomato";
		}
		if (d instanceof NthOutput)
		{
			color = "GreenYellow";
		}
		return color;
	}

	protected void visit(StringBuilder out, Queue<ConcreteTraceabilityNode> to_visit,
			Set<ConcreteTraceabilityNode> visited)
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
				String caption = formatCaption(dob, m_showCaptions);
				String fillcolor = formatColor(dob);
				String shape = "shape=\"rectangle\"";
				if (m_showCaptions == CaptionStyle.NONE)
				{
					shape = "shape=\"circle\",width=.3,fixedsize=\"true\"";
				}
				out.append(" ").append(s_id).append(" [label=\"").append(caption)
						.append("\",").append(shape).append(",fillcolor=\"").append(fillcolor).append("\"];\n");
			}
			else
			{
				// "or" node or "and" node
				String fill_color = "yellow";
				String symbol = "∨";
				if (n instanceof AndNode)
				{
					fill_color = "green";
					symbol = "∧";
				}
				out.append(" ").append(s_id).append(" [label=\"").append(symbol)
						.append("\",shape=\"circle\",color=\"orange\",fillcolor=\"").append(fill_color)
						.append("\",width=.3,fixedsize=\"true\"];\n");
			}
			String link_color = "black";
			for (LabeledEdge ql : n.getChildren())
			{
				Quality qual = ql.getQuality();
				ConcreteTraceabilityNode child_node = (ConcreteTraceabilityNode) ql.getNode();
				if (m_flatten)
				{
					LabeledEdge le = getNextSpecialChild(child_node);
					qual = merge(qual, le.getQuality());
					child_node = (ConcreteTraceabilityNode) le.getNode();
				}
				if (ql.getQuality() == Quality.OVER)
				{
					link_color = "red";
				}
				if (ql.getQuality() == Quality.UNDER)
				{
					link_color = "blue";
				}
				
				out.append(" ").append(s_id).append(" -> ").append(child_node.getId()).append(" [color=\"")
						.append(link_color).append("\"];\n");
				if (!visited.contains(child_node))
				{
					to_visit.add(child_node);
				}
			}
		}
	}
	
	protected static ConcreteLabeledEdge getNextSpecialChild(ConcreteTraceabilityNode n)
	{
		Quality q = Quality.EXACT;
		while (n.getChildren().size() == 1)
		{
			LabeledEdge le = n.getChildren().get(0);
			q = merge(q, le.getQuality());
			n = (ConcreteTraceabilityNode) le.getNode();
		}
		return new ConcreteLabeledEdge(n, q);
	}
	
	protected static Quality merge(Quality q1, Quality q2)
	{
		if (q1 == Quality.EXACT)
		{
			return q2;
		}
		if (q2 == Quality.EXACT)
		{
			return q1;
		}
		if (q1 == q2)
		{
			return q1;
		}
		return Quality.NONE;
	}
}
