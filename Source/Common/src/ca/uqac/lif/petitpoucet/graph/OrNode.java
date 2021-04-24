package ca.uqac.lif.petitpoucet.graph;

import java.util.List;

import ca.uqac.lif.petitpoucet.Elaboration;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.TraceabilityNode;

public class OrNode extends ConcreteTraceabilityNode
{
	protected ConstantElaboration m_shortElaboration;
	
	public OrNode()
	{
		super();
	}
	
	public void setShortElaboration(Elaboration e)
	{
		m_shortElaboration = (ConstantElaboration) e;
	}
	
	@Override
	public ConstantElaboration getShort()
	{
		return m_shortElaboration;
	}
	
	@Override
	public ComposedElaboration getLong()
	{
		OrElaboration ce = new OrElaboration(m_shortElaboration);
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
		out.append(indent).append("∨").append("\n");
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
		if (n instanceof OrNode)
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
		if (tn instanceof OrNode)
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
	
	public static class OrElaboration extends ComposedElaboration
	{
		public OrElaboration(ConstantElaboration short_e, Object ... parts)
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
					out.append(" or ");
				}
				out.append(m_parts.get(i).getShort());
			}
			return out.toString();
		}
	}
}
