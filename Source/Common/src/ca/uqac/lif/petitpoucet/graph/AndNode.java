package ca.uqac.lif.petitpoucet.graph;

import java.util.List;

import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;

public class AndNode extends ConcreteTraceabilityNode
{
	protected ConstantElaboration m_shortElaboration;
	
	public AndNode()
	{
		super();
	}
	
	@Override
	public void setShortElaboration(ConstantElaboration e)
	{
		m_shortElaboration = e;
	}
	
	@Override
	public ConstantElaboration getShort()
	{
		return m_shortElaboration;
	}
	
	@Override
	public ComposedElaboration getLong()
	{
		AndElaboration ce = new AndElaboration(m_shortElaboration);
		for (LabeledEdge edge : m_children)
		{
			TraceabilityNode child = edge.getNode();
			ce.add(child.getLong());
		}
		return ce;
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
	
	public static class AndElaboration extends ComposedElaboration
	{
		public AndElaboration(ConstantElaboration short_e, Object ... parts)
		{
			super(short_e, parts);
		}
		
		@Override
		public String toString()
		{
			if (m_parts.isEmpty())
			{
				return m_short.toString();
			}
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < m_parts.size(); i++)
			{
				if (i > 0)
				{
					out.append(" and ");
				}
				out.append(m_parts.get(i).getShort());
			}
			return out.toString();
		}
	}
}
