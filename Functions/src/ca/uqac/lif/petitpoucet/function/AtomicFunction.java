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

import ca.uqac.lif.petitpoucet.circuit.AtomicConnectable;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;
import ca.uqac.lif.petitpoucet.function.Constant.Argument;
import ca.uqac.lif.petitpoucet.circuit.Connectable;


/**
 * A node in a circuit. A node has a fixed number of inputs and outputs, and
 * computes its output values from its input values. The output values are
 * cached until the node is reset.
 * @see Connectable
 * @see Computable
 * @author Sylvain Hallé
 */
public abstract class AtomicFunction extends AtomicConnectable implements Computable
{	
	/**
	 * A delegate taking care of the functionalities common to all
	 * Computable objects.
	 */
	/*@ non_null @*/ protected final FunctionDelegate m_delegate;

	/**
	 * Creates a new node with the given input and output arities.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 */
	public AtomicFunction(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
		m_delegate = new FunctionDelegate(this);
	}

	@Override
	public Object compute(int index)
	{
		return m_delegate.compute(index);
	}
	
	@Override
	public FunctionDelegate delegate()
	{
		return m_delegate;
	}

	public Object evaluate(Object ... inputs)
	{
		for (int i = 0; i < inputs.length; i++)
		{
			getConnector().connectElements(new Argument(inputs[i], i), 0, this, i);
		}
		return compute();
	}
	
	@Override
	public ComputableConnector getConnector()
	{
		return ComputableConnector.instance;
	}

	@Override
	public AtomicFunction duplicate()
	{
		return duplicate(false);
	}

	@Override
	public abstract AtomicFunction duplicate(boolean with_state);

	/**
	 * Copies the state of the current atomic function into another function.
	 * @param f The other function
	 * @param with_state Whether the state should be copied or not
	 */
	protected void duplicate(AtomicFunction f, boolean with_state)
	{
		super.duplicate(f, with_state);
		m_delegate.duplicate(f, with_state);
	}

	@Override
	public void reset()
	{
		m_delegate.reset();
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
	public ComputableDownstreamConnection getInputEndpoint(int index)
	{
		return m_delegate.getInputEndpoint(index);
	}

	@Override
	public ComputableUpstreamConnection getOutputEndpoint(int index)
	{
		return m_delegate.getOutputEndpoint(index);
	}	
}
