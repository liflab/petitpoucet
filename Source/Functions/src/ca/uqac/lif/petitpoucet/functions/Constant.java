package ca.uqac.lif.petitpoucet.functions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;

public class Constant implements Function
{
	protected Object m_value;
	
	public Constant(Object value)
	{
		super();
		m_value = value;
	}
	
	@Override
	public int size()
	{
		return 1;
	}

	@Override
	public Constant duplicate(boolean with_state) 
	{
		return this;
	}
	
	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track) 
	{
		outputs[0] = m_value;
		if (track)
		{
			return ConstantQueryable.get(toString());
		}
		return null;
	}
	
	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
	{
		return evaluate(inputs, outputs, c, true);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		return null;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		return m_value.getClass();
	}
	
	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		return printer.print(m_value);
	}

	@Override
	public Constant read(ObjectReader<?> reader, Object o) throws ReadException
	{
		Object r_o = reader.read(o);
		return new Constant(r_o);
	}

	@Override
	public Constant duplicate() 
	{
		return duplicate(false);
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, boolean track)
	{
		return evaluate(inputs, outputs, null, track);
	}
	
	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs)
	{
		return evaluate(inputs, outputs, null, true);
	}

	@Override
	public int getInputArity()
	{
		return 0;
	}

	@Override
	public int getOutputArity()
	{
		return 1;
	}
	
	@Override
	public String toString()
	{
		if (m_value == null)
		{
			return "null";
		}
		return m_value.toString();
	}
	
	@Override
	public void reset() 
	{
		// Nothing to do
	}
	
	public static class HardValue implements Designator
	{
		public static final transient HardValue instance = new HardValue();
		
		private HardValue()
		{
			super();
		}
		
		@Override
		public boolean appliesTo(Object o)
		{
			return o instanceof Constant;
		}

		@Override
		public Designator peek() 
		{
			return this;
		}

		@Override
		public Designator tail() 
		{
			return Designator.identity;
		}
		
		@Override
		public String toString()
		{
			return "Hard value";
		}
	}
	
	static class ConstantQueryable extends FunctionQueryable
	{
		protected static final transient Map<String,ConstantQueryable> s_pool = new LinkedHashMap<String,ConstantQueryable>();
		
		public static ConstantQueryable get(String reference)
		{
			ConstantQueryable cq = s_pool.get(reference);
			if (cq == null)
			{
				cq = new ConstantQueryable(reference);
				s_pool.put(reference, cq);
			}
			return cq;
		}
		
		private ConstantQueryable(String reference)
		{
			super(reference, 0, 1);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			TraceabilityNode node = factory.getObjectNode(HardValue.instance, this);
			root.addChild(node, Quality.EXACT);
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			leaves.add(node);
			return leaves;
		}
		
		@Override
		public ConstantQueryable duplicate(boolean with_state)
		{
			return this;
		}
	}
}
