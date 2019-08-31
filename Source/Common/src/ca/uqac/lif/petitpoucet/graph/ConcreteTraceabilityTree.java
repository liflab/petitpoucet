package ca.uqac.lif.petitpoucet.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.petitpoucet.LabeledEdge;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityTree;

public class ConcreteTraceabilityTree implements TraceabilityTree
{
	/**
	 * The tree's root
	 */
	protected TraceabilityNode m_root;

	public ConcreteTraceabilityTree(TraceabilityNode root)
	{
		super();
		m_root = root;
	}

	@Override
	public TraceabilityNode getRoot()
	{
		return m_root;
	}

	@Override
	public List<TraceabilityNode> getLeaves()
	{
		Queue<TraceabilityNode> to_visit = new ArrayDeque<TraceabilityNode>();
		List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
		Set<TraceabilityNode> visited = new HashSet<TraceabilityNode>();
		to_visit.add(m_root);
		while (!to_visit.isEmpty())
		{
			TraceabilityNode n = to_visit.remove();
			visited.add(n);
			if (n.getChildren().size() == 0)
			{
				leaves.add(n);
			}
			else
			{
				for (LabeledEdge le : n.getChildren())
				{
					TraceabilityNode tn = le.getNode();
					if (!visited.contains(tn) && !to_visit.contains(tn))
					{
						to_visit.add(tn);
					}
				}
			}
		}
		return leaves;
	}

}
