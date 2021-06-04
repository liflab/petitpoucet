/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.function;

import java.util.Map;

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.ExplanationQueryable;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.util.Duplicable;

public class Circuit extends NestedNode implements Function, Duplicable, ExplanationQueryable
{
	protected Map<String,Object> m_context;
	
	public Circuit(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}

	@Override
	public Object getContext(String key)
	{
		return m_context.get(key);
	}

	@Override
	public void setContext(String key, Object value)
	{
		m_context.put(key, value);
	}

	@Override
	public PartNode getExplanation(Part part)
	{
		PartNode root = new PartNode(part, this);
		int output_nb = NthOutput.mentionedOutput(part);
		if (output_nb < 0 || output_nb >= getOutputArity())
		{
			// Nothing to do
			return root;
		}
		Pin<? extends Node> start_pin = m_outputAssociations.get(output_nb);
		Part start_part = NthOutput.replaceOutBy(part, new NthOutput(start_pin.getIndex()));
		
	}

	@Override
	public Object[] evaluate(Object ... inputs)
	{
		if (inputs.length != getInputArity())
		{
			throw new InvalidNumberOfArgumentsException();
		}
		for (int i = 0; i < inputs.length; i++)
		{
			Pin<? extends Node> pin = m_inputAssociations.get(i);
			if (!(pin instanceof FunctionPin))
			{
				throw new FunctionException("Invalid circuit");
			}
			((FunctionPin<?>) pin).setValue(inputs[i]);
		}
		Object[] out = new Object[getOutputArity()];
		for (int i = 0; i < out.length; i++)
		{
			Pin<? extends Node> pin = m_outputAssociations.get(i);
			if (!(pin instanceof FunctionPin))
			{
				throw new FunctionException("Invalid circuit");
			}
			out[i] = ((FunctionPin<?>) pin).getValue();
		}
		return out;
	}

	@Override
	public void reset()
	{
		for (Node n : m_internalNodes)
		{
			if (n instanceof Function)
			{
				((Function) n).reset();
			}
		}
	}

}
