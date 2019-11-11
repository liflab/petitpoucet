package ca.uqac.lif.petitpoucet.functions.lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

@SuppressWarnings("rawtypes")
public class GetSize extends UnaryFunction<List,Object>
{
	protected static final transient Map<Integer,GetSizeQueryable> s_queryablePool = new LinkedHashMap<Integer,GetSizeQueryable>();

	public GetSize(int index)
	{
		super(List.class, Object.class);
	}

	@Override
	public GetSizeQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		List<?> list = (List<?>) inputs[0];
		int size = list.size();
		outputs[0] = size;
		if (track)
		{
			GetSizeQueryable geq = s_queryablePool.get(size);
			if (geq == null)
			{
				geq = new GetSizeQueryable(toString(), size);
				s_queryablePool.put(size, geq);
			}
			return geq;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "Size of";
	}

	public static class GetSizeQueryable extends FunctionQueryable
	{
		protected int m_listSize;

		public GetSizeQueryable(String reference, int index) 
		{
			super(reference, 1, 1);
			m_listSize = index;
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(m_listSize);
			TraceabilityNode and = factory.getAndNode();
			root.addChild(and, Quality.EXACT);
			for (int i = 0; i < m_listSize; i++)
			{
				ComposedDesignator cd = new ComposedDesignator(d, NthElement.get(m_listSize), NthInput.get(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				and.addChild(child, Quality.EXACT);
				leaves.add(child);
			}
			return leaves;
		}

		@Override
		protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			Designator head = d.peek();
			if (!(head instanceof NthElement))
			{
				return factory.unknownLink(root);
			}
			int index = ((NthElement) head).getIndex();
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			if (index >= 0 && index < m_listSize)
			{
				ComposedDesignator cd = new ComposedDesignator(d, NthOutput.get(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				root.addChild(child, Quality.EXACT);
				leaves.add(child);
			}
			else
			{
				TraceabilityNode node = factory.getObjectNode(Designator.nothing, this);
				root.addChild(node, Quality.NONE);
				leaves.add(node);
			}
			return leaves;
		}

		@Override
		public GetSizeQueryable duplicate(boolean with_state)
		{
			return this;
		}
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function duplicate(boolean with_state) 
	{
		return this;
	}
}
