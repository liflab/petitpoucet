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
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

@SuppressWarnings("rawtypes")
public class Wrap extends UnaryFunction<Object,List>
{
	public static final transient Wrap instance = new Wrap();
	
	protected static final transient WrapQueryable s_queryable = new WrapQueryable();
	
	protected Wrap()
	{
		super(Object.class, List.class);
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
	public Wrap duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		List<Object> list = new ArrayList<Object>(1);
		list.add(inputs[0]);
		outputs[0] = list;
		if (track)
		{
			return s_queryable;
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return "Wrap";
	}

	protected static class WrapQueryable extends FunctionQueryable
	{
		public WrapQueryable()
		{
			super("Wrap", 1, 1);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			Designator t_head = tail.peek();
			Designator t_tail = tail.tail();
			if (!(t_head instanceof NthElement))
			{
				return super.queryOutput(q, out_index, t_tail, root, factory);
			}
			int elem_index = ((NthElement) t_head).getIndex();
			if (elem_index != 0)
			{
				TraceabilityNode n = factory.getUnknownNode();
				root.addChild(n, Quality.NONE);
				leaves.add(n);
			}
			else
			{
				TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(t_tail, NthInput.get(0)), this);
				root.addChild(n, Quality.EXACT);
				leaves.add(n);
			}
			return leaves;
		}
	}
}
