package ca.uqac.lif.petitpoucet.functions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.common.Context;

public class ContextVariable implements Function
{
	protected String m_name;
	
	public ContextVariable(String name)
	{
		super();
		m_name = name;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextVariable duplicate(boolean with_state) 
	{
		return new ContextVariable(m_name);
	}

	@Override
	public ContextVariable duplicate()
	{
		return duplicate(false);
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
	{
		if (c == null || !c.containsKey(m_name))
		{
			return new UnknownQueryable(toString(), 0, 1);
		}
		outputs[0] = c.get(m_name);
		return new ContextVariableQueryable(toString(), m_name);
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return evaluate(inputs, outputs,  null);
	}

	@Override
	public Class<?> getInputType(int index)
	{
		return null;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		return Object.class;
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
	public void reset() 
	{
		// Nothing to do
	}
	
	public static class ContextVariableQueryable extends FunctionQueryable
	{
		protected String m_name;
		
		public ContextVariableQueryable(String reference, String name)
		{
			super(reference, 0, 1);
			m_name = name;
		}
		
		public String getName()
		{
			return m_name;
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			TraceabilityNode node = factory.getObjectNode(new ContextDesignator(), this);
			root.addChild(node, Quality.EXACT);
			return leaves;
		}
		
		@Override
		public ContextVariableQueryable duplicate(boolean with_state)
		{
			return new ContextVariableQueryable(m_reference, m_name);
		}
	}
	
	public static class ContextDesignator implements Designator
	{
		@Override
		public boolean appliesTo(Object o) 
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Designator peek()
		{
			return this;
		}

		@Override
		public Designator tail() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String toString()
		{
			return "Context";
		}
		
	}

}
