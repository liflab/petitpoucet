package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.circuit.Connectable;
import ca.uqac.lif.petitpoucet.circuit.Connectable.UpstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;

class FunctionDelegate
{
	protected final Computable m_pupil;
	
	/**
	 * The cached output values. This is set to null when the node is reset, and
	 * recomputed when the node is computed again.
	 */
	/*@ null @*/ protected Object[] m_outputArguments;
	
	/**
	 * Creates a new node with the given input and output arities.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 */
	FunctionDelegate(Computable pupil)
	{
		super();
		m_pupil = pupil;
		m_outputArguments = null;
	}
	
	public Object compute(int index)
	{
		Object[] arguments = new Object[m_pupil.getInputArity()];
		if (m_outputArguments == null)
		{
			m_outputArguments = new Object[m_pupil.getOutputArity()];
			for (int i = 0; i < m_pupil.getInputArity(); i++)
			{
				UpstreamConnection c = m_pupil.getAssignedInput(i);
				Connectable o = c.getObject();
				if (!(o instanceof Computable))
				{
					throw new IllegalStateException("Expected node to be computable");
				}
				arguments[i] = ((Computable) o).compute(c.getIndex());
			}
			m_pupil.evaluate(arguments, m_outputArguments);
		}
		return m_outputArguments[index];
	}
	
	public void reset()
	{
		m_outputArguments = null;
	}
	
	public ComputableDownstreamConnection getInputEndpoint(int index)
	{
		return new ComputableDownstreamConnection(m_pupil, index);
	}

	public ComputableUpstreamConnection getOutputEndpoint(int index)
	{
		return new ComputableUpstreamConnection(m_pupil, index);
	}
	
	/**
	 * Copies the state of the current atomic function into another function.
	 * @param f The other function
	 * @param with_state Whether the state should be copied or not
	 */
	protected void duplicate(Computable f, boolean with_state)
	{
		if (with_state)
		{
			FunctionDelegate d = f.delegate();
			for (int i = 0; i < m_outputArguments.length; i++)
			{
				d.m_outputArguments[i] = m_outputArguments[i];
			}
		}
	}
	
	
}
