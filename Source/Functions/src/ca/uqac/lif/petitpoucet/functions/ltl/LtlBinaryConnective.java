package ca.uqac.lif.petitpoucet.functions.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction.BinaryFunctionQueryable;

public class LtlBinaryConnective
{
	public static class LtlBinaryConnectiveQueryable extends FunctionQueryable
	{
		protected List<BinaryFunctionQueryable.Inputs> m_queryables;
		
		public LtlBinaryConnectiveQueryable(String reference, List<BinaryFunctionQueryable.Inputs> queryables)
		{
			super(reference, 2, 1);
			m_queryables = queryables;
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			Designator d_head = tail.peek();
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			if (q instanceof ProvenanceQuery || !(d_head instanceof NthElement))
			{
				return super.queryOutput(q, out_index, tail, root, factory);
			}
			int position = ((NthElement) d_head).getIndex();
			if (position < 0 || position > m_queryables.size())
			{
				TraceabilityNode node = factory.getUnknownNode();
				root.addChild(node, Quality.NONE);
				leaves.add(node);
				return leaves;
			}
			BinaryFunctionQueryable.Inputs inp = m_queryables.get(position);
			switch (inp)
			{
			case LEFT:
			{
				TraceabilityNode left = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(0)), this);
				root.addChild(left, Quality.EXACT);
				leaves.add(left);
				break;
			}
			case RIGHT:
			{
				TraceabilityNode right = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(1)), this);
				root.addChild(right, Quality.EXACT);
				leaves.add(right);
				break;
			}
			case BOTH:
			{
				TraceabilityNode and = factory.getAndNode();
				root.addChild(and, Quality.EXACT);
				TraceabilityNode left = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(0)), this);
				and.addChild(left, Quality.EXACT);
				leaves.add(left);
				TraceabilityNode right = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(1)), this);
				and.addChild(right, Quality.EXACT);
				leaves.add(right);
				break;
			}
			case ANY:
			{
				TraceabilityNode or = factory.getOrNode();
				root.addChild(or, Quality.EXACT);
				TraceabilityNode left = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(0)), this);
				or.addChild(left, Quality.EXACT);
				leaves.add(left);
				TraceabilityNode right = factory.getObjectNode(new ComposedDesignator(tail.tail(), NthElement.get(position), NthInput.get(1)), this);
				or.addChild(right, Quality.EXACT);
				leaves.add(right);
				break;
			}
			}
			return leaves;
		}
	}
}
