package ca.uqac.lif.petitpoucet.functions.lists;

import java.util.ArrayList;
import java.util.List;

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
public class GetElement extends UnaryFunction<List,Object>
{
	protected int m_index;

	protected GetElementQueryable m_queryable;

	public GetElement(int index)
	{
		super(List.class, Object.class);
		m_index = index;
		m_queryable = new GetElementQueryable(toString(), index);
	}

	@Override
	public GetElementQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		List<?> list = (List<?>) inputs[0];
		Object elem = list.get(m_index);
		outputs[0] = elem;
		return m_queryable;
	}

	@Override
	public String toString()
	{
		return "Get #" + m_index;
	}

	public static class GetElementQueryable extends FunctionQueryable
	{
		protected int m_index;

		public GetElementQueryable(String reference, int index) 
		{
			super(reference, 1, 1);
			m_index = index;
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			ComposedDesignator cd = new ComposedDesignator(d, new NthElement(m_index), new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.EXACT);
			leaves.add(child);
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
			if (index == m_index)
			{
				ComposedDesignator cd = new ComposedDesignator(d, new NthOutput(m_index));
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
		public GetElementQueryable duplicate(boolean with_state)
		{
			return new GetElementQueryable(m_reference, m_index);
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
		return new GetElement(m_index);
	}
}
