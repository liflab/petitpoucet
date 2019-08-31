package ca.uqac.lif.petitpoucet.circuit.functions;

public class IsEven extends NaryFunction
{
	public IsEven()
	{
		super(1);
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_inputs[0] = inputs[0];
		if (m_inputs[0] instanceof Number)
		{
			m_returnedValue[0] = ((Number) m_inputs[0]).intValue() % 2 == 0;
			outputs[0] = m_returnedValue[0];
		}
	}

	@Override
	public String toString()
	{
		return "Is Even";
	}
}
