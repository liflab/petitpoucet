/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.circuit.CompositeConnectable;
import ca.uqac.lif.petitpoucet.circuit.Connectable;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;
import ca.uqac.lif.petitpoucet.function.Constant.Argument;

public class CompositeFunction extends CompositeConnectable<Computable> implements Computable
{
	/**
	 * A delegate taking care of the functionalities common to all
	 * Computable objects.
	 */
	/*@ non_null @*/ protected final FunctionDelegate m_delegate;

	/**
	 * Creates a new circuit with the given input and output arities, and a
	 * default name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 */
	public CompositeFunction(int in_arity, int out_arity)
	{
		this(in_arity, out_arity, null);
	}

	/**
	 * Creates a new circuit with the given input and output arities, and an
	 * optional name.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 * @param name An optional name for the circuit
	 */
	public CompositeFunction(int in_arity, int out_arity, String name)
	{
		super(in_arity, out_arity, name);
		m_delegate = new FunctionDelegate(this);
	}

	@Override
	public void reset()
	{
		m_delegate.reset();
		for (Connectable n : m_nodes)
		{
			if (!(n instanceof Computable))
			{
				throw new IllegalArgumentException("Expected a Computable");
			}
			((Computable) n).reset();
		}
	}
	
	@Override
	public FunctionDelegate delegate()
	{
		return m_delegate;
	}

	@Override
	public CompositeFunction duplicate(boolean with_state)
	{
		CompositeFunction c = new CompositeFunction(getInputArity(), getOutputArity());
		duplicate(c, with_state);
		return c;
	}

	@Override
	public void evaluate(Object[] input, Object[] output)
	{
		// Step 1: connect inputs with constant
		for (int i = 0; i < input.length; i++)
		{
			UpstreamConnection c = (UpstreamConnection) m_inputAssociations.get(i);
			Connectable n = c.getObject();
			getConnector().connectElements(new Argument(input[i], i), 0, n, c.getIndex());
		}
		// Step 2: trigger evaluation and fetch outputs
		for (int i = 0; i < output.length; i++)
		{
			DownstreamConnection c = (DownstreamConnection) m_outputAssociations.get(i); 
			Connectable n = c.getObject();
			if (n instanceof Computable)
			{
				output[i] = ((Computable) n).compute(c.getIndex());
			}
		}
	}
	
	@Override
	public Object compute(int index)
	{
		return m_delegate.compute(index);
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
	
	@Override
	public ComputableDownstreamConnection getAssignedOutput(int index)
	{
		return (ComputableDownstreamConnection) super.getAssignedOutput(index);
	}
	
	@Override
	public ComputableUpstreamConnection getAssignedInput(int index)
	{
		return (ComputableUpstreamConnection) super.getAssignedInput(index);
	}
	
	@Override
	protected ComputableUpstreamConnection newInputAssociation(Computable n, int i)
	{
		return new ComputableUpstreamConnection(n, i);
	}

	@Override
	protected ComputableDownstreamConnection newOutputAssociation(Computable n, int i)
	{
		return new ComputableDownstreamConnection(n, i);
	}
	
	@Override
	public ComputableConnector getConnector()
	{
		return ComputableConnector.instance;
	}
	
	public Object evaluate(Object ... arguments)
	{
		Object[] outs = new Object[getOutputArity()];
		evaluate(arguments, outs);
		return outs[0];
	}
}
