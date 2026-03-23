package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.circuit.ParameterizedConnectable;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;

public abstract class ParameterizedFunction extends ParameterizedConnectable<CompositeFunction> implements Computable
{
	/**
	 * A delegate taking care of the functionalities common to all
	 * Computable objects.
	 */
	/*@ non_null @*/ protected final FunctionDelegate m_delegate;
	
	/**
	 * Creates a new instance of the node.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 * @param parameter The parameter {@link AtomicFunction}
	 */
	public ParameterizedFunction(int in_arity, int out_arity, CompositeFunction parameter)
	{
		super(in_arity, out_arity, parameter);
		m_delegate = new FunctionDelegate(this);
	}
	
	public void duplicate(ParameterizedFunction f, boolean with_state)
	{
		super.duplicate(f, with_state);
		m_delegate.duplicate(f, with_state);
	}
	
	@Override
	public Object compute(int index)
	{
		return m_delegate.compute(index);
	}
	
	@Override
	public void reset()
	{
		m_delegate.reset();
		m_f.reset();
	}
	
	@Override
	public FunctionDelegate delegate()
	{
		return m_delegate;
	}
	
	@Override
	public ComputableConnector getConnector()
	{
		return ComputableConnector.instance;
	}
	
	@Override
	public ComputableDownstreamConnection getInputEndpoint(int index)
	{
		return m_delegate.getInputEndpoint(index);
	}

	@Override
	public ComputableUpstreamConnection getOutputEndpoint(int index)
	{
		return m_delegate.getOutputEndpoint(index);
	}	
	
	protected void register(Object[] outputs, Object[] inputs)
	{
		m_f.reset();
		for (int i = 0; i < inputs.length; i++)
		{
			getConnector().connectElements(new Constant(inputs[i]), 0, m_f, i);
		}
		m_f.evaluate(inputs, outputs);
		try
		{
			m_explanations.add(m_f.explain(OutputPart.FIRST));
		}
		catch (ExplanationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected abstract class ParameterizedFunctionVertex extends ParameterLazyVertex
	{
		public ParameterizedFunctionVertex(VertexFactory f, Part p)
		{
			super(f, p);
		}
		
		@Override
		public abstract ParameterizedFunction getInstance();
		
	}
}
