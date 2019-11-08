package ca.uqac.lif.petitpoucet.graph;

import java.util.List;

import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;

public class AndNode extends ConcreteTraceabilityNode
{
	public AndNode()
	{
		super();
	}
	
	@Override
	public String toString()
	{
		return toString("");
	}
	
	@Override
	protected String toString(String indent)
	{
		StringBuilder out = new StringBuilder();
		out.append(indent).append("∧").append("\n");
		for (LabeledEdge le : m_children)
		{
			out.append(indent).append(le.getQuality());
			out.append(((ConcreteTraceabilityNode) le.getNode()).toString(indent + " "));
		}
		return out.toString();
	}
	
	@Override
	public void addChild(TraceabilityNode n, Quality q)
	{
		if (n instanceof AndNode)
		{
			List<LabeledEdge> children = n.getChildren();
			for (LabeledEdge le : children)
			{
				addChild(le);
			}
		}
		else
		{
			super.addChild(n, q);
		}
	}

	@Override
	public void addChild(LabeledEdge e)
	{
		TraceabilityNode tn = e.getNode();
		if (tn instanceof AndNode)
		{
			List<LabeledEdge> children = tn.getChildren();
			for (LabeledEdge le : children)
			{
				addChild(le);
			}
		}
		else
		{
			super.addChild(e);
		}
		m_children.add(e);
	}
}
