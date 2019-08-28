package examples.functions;

import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;

public class FunctionConnection implements CircuitConnection
{
	protected int m_index;
	
	protected CircuitFunction m_element;
	
	protected Object m_value;
	
	public FunctionConnection(int index, CircuitFunction element)
	{
		super();
		m_index = index;
		m_element = element;
		m_value = null;
	}
	
	@Override
	public int getIndex() 
	{
		return m_index;
	}

	@Override
	public CircuitFunction getObject() 
	{
		return m_element;
	}
	
	public Object pullValue()
	{
		if (m_value != null)
		{
			return m_value;
		}
		Object[] outs = m_element.evaluate();
		m_value = outs[m_index];
		return m_value;
	}
}
