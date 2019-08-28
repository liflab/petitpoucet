package examples.functions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.Trackable;
import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;
import ca.uqac.lif.petitpoucet.circuit.CircuitElement;

public abstract class CircuitFunction implements CircuitElement, Trackable
{
	/**
	 * The function's input arity
	 */
	protected int m_inArity;

	/**
	 * The function's output arity
	 */
	protected int m_outArity;

	/**
	 * The input connections of this function
	 */
	protected FunctionConnection[] m_inputConnections;

	/**
	 * The input connections of this function
	 */
	protected FunctionConnection[] m_outputConnections;
	
	/**
	 * The value returned by the function the last time it was called
	 */
	protected Object[] m_returnedValue;
	
	/**
	 * A flag indicating whether the function has been evaluated once
	 */
	protected boolean m_evaluated = false;

	/**
	 * Creates a new circuit function
	 * @param in_arity The function's input arity
	 * @param out_arity The function's output arity
	 */
	public CircuitFunction(int in_arity, int out_arity)
	{
		super();
		m_inArity = in_arity;
		m_outArity = out_arity;
		m_inputConnections = new FunctionConnection[in_arity];
		m_outputConnections = new FunctionConnection[out_arity];
		m_returnedValue = new Object[out_arity];
	}

	/**
	 * Evaluates the function with concrete values. Note that this implementation is
	 * not very efficient, as a function that is involved as the input of 
	 * @return outputs An array of output arguments
	 */
	public final Object[] evaluate()
	{
		if (m_evaluated)
		{
			return m_returnedValue;
		}
		Object[] inputs = new Object[m_inArity];
		for (int i = 0; i < m_inArity; i++)
		{
			FunctionConnection fc = m_inputConnections[i];
			if (fc == null)
			{
				inputs[i] = null;
			}
			else
			{
				inputs[i] = fc.pullValue();
			}
		}
		getValue(inputs, m_returnedValue);
		return m_returnedValue;
	}

	@Override
	public void setToInput(int index, CircuitConnection connection)
	{
		m_inputConnections[index] = (FunctionConnection) connection;
	}

	@Override
	public void setToOutput(int index, CircuitConnection connection)
	{
		m_outputConnections[index] = (FunctionConnection) connection;
	}
	
	@Override
	public int getInputArity() 
	{
		return m_inArity;
	}

	@Override
	public int getOutputArity() 
	{
		return m_outArity;
	}

	@Override
	public Trackable getTrackable() 
	{
		return this;
	}
	
	@Override
	public List<DesignatorLink> query(TraceabilityQuery q, Designator d)
	{
		List<DesignatorLink> list = new ArrayList<DesignatorLink>();
		if (q instanceof ProvenanceQuery)
		{
			provenanceQuery(d, list);
		}
		if (q instanceof CausalityQuery)
		{
			causalityQuery(d, list);
		}
		return list;
	}

	/**
	 * Gets the value of this function for concrete inputs
	 * @param inputs An array of input arguments
	 * @param outputs An array of output arguments
	 */
	public abstract void getValue(Object[] inputs, Object[] outputs);
	
	public abstract void provenanceQuery(Designator d, List<DesignatorLink> links);
	
	public abstract void causalityQuery(Designator d, List<DesignatorLink> links);;
}
