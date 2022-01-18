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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.util.Duplicable;

/**
 * A self-contained computation unit that produces an array of output values
 * from an array of input values. This class is abstract, and must minimally
 * implement method {@link #getValue(Object...) getValue()} to become concrete.
 * <p>
 * The class also implements the {@link ExplanationQueryable} interface by
 * providing a boilerplate explanation where all elements of the output are
 * related to all elements of the input. Only functions that explain their
 * output differently need to override this method.
 */
public abstract class AtomicFunction extends Node implements Function, Duplicable, ExplanationQueryable
{
	/**
	 * A context that can be assigned to a function.
	 */
	protected Map<String,Object> m_context;
	
	/**
	 * An array of input pins for the function. The size of the array
	 * corresponds to the function's input arity.
	 */
	protected AtomicFunctionInputPin[] m_inputPins;
	
	/**
	 * An array of output pins for the function. The size of the array
	 * corresponds to the function's output arity.
	 */
	protected AtomicFunctionOutputPin[] m_outputPins;

	/**
	 * Creates a new atomic function.
	 * @param in_arity The input arity of the function
	 * @param out_arity The output arity of the function
	 */
	public AtomicFunction(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
		m_context = new HashMap<>();
		m_inputPins = new AtomicFunctionInputPin[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_inputPins[i] = new AtomicFunctionInputPin(i);
		}
		m_outputPins = new AtomicFunctionOutputPin[out_arity];
		for (int i = 0; i < out_arity; i++)
		{
			m_outputPins[i] = new AtomicFunctionOutputPin(i);
		}
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

	/**
	 * Copies the contents of the current function into another function
	 * instance.
	 * @param f The other function
	 * @param with_state Set to {@code true} for a stateful copy, {@code false}
	 * otherwise
	 */
	protected void copyInto(AtomicFunction f, boolean with_state)
	{
		super.copyInto(f, with_state);
		if (with_state)
		{
			f.m_context.putAll(m_context);
		}
	}
	
	@Override
	public void reset()
	{
		for (int i = 0; i < m_inputPins.length; i++)
		{
			m_inputPins[i].reset();
		}
		for (int i = 0; i < m_outputPins.length; i++)
		{
			m_outputPins[i].reset();
		}
	}

	@Override
	/*@ non_null @*/ public AtomicFunctionInputPin getInputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_inputPins[index];
	}

	@Override
	/*@ non_null @*/ public AtomicFunctionOutputPin getOutputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_outputPins[index];
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
			m_inputPins[i].setValue(inputs[i]);
		}
		Object[] out = new Object[getOutputArity()];
		for (int i = 0; i < out.length; i++)
		{
			out[i] = m_outputPins[i].getValue();
		}
		return out;
	}
	
	@Override
	/*@ non_null @*/ public final PartNode getExplanation(Part part)
	{
		return getExplanation(part, NodeFactory.getFactory());
	}
	
	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index >= 0)
		{
			LabelledNode and = root;
			if (getInputArity() > 1)
			{
				and = factory.getAndNode();
				root.addChild(and);
			}
			for (int i = 0; i < getInputArity(); i++)
			{
				PartNode in = factory.getPartNode(NthOutput.replaceOutByIn(part, i), this);
				and.addChild(in);
			}
		}
		return root;
	}
	
	@Override
	public AtomicFunction duplicate()
	{
		return (AtomicFunction) duplicate(false);
	}

	/**
	 * Computes the output value of the function based on input arguments. 
	 * @param inputs The input arguments. 
	 * @return An array corresponding to the output values of the function.
	 * The size of this array is equal to the output arity of the function.
	 * @throws InvalidNumberOfArgumentsException If the number of arguments of
	 * the method does not equal the input arity of the function
	 */
	protected abstract Object[] getValue(Object ... inputs) throws InvalidNumberOfArgumentsException;

	public class AtomicFunctionInputPin extends FunctionPin<AtomicFunction>
	{
		/**
		 * Creates a new input pin.
		 * @param index The index of the input
		 */
		public AtomicFunctionInputPin(int index)
		{
			super(AtomicFunction.this, index);
		}

		/**
		 * Gets the value of this input pin.
		 * @return The value
		 */
		public Object getValue()
		{
			if (m_evaluated)
			{
				return m_value;
			}
			Collection<Pin<? extends Node>> pins = getInputLinks(m_index);
			if (getInputArity() == 0)
			{
				// Special case for functions with input arity of 0
				m_evaluated = true;
				return null;
			}
			for (Pin<?> p : pins)
			{
				if (p instanceof FunctionPin)
				{
					m_value = ((FunctionPin<?>) p).getValue();
					m_evaluated = true;
					break;
				}
				else
				{
					System.out.println("EILLE");
				}
			}
			if (!m_evaluated)
			{
				throw new FunctionException("Cannot get value");
			}
			return m_value;
		}
		
		@Override
		public AtomicFunctionInputPin duplicate(boolean with_state)
		{
			AtomicFunctionInputPin afip = new AtomicFunctionInputPin(m_index);
			copyInto(afip, false);
			return afip;
		}
	}

	public class AtomicFunctionOutputPin extends FunctionPin<AtomicFunction>
	{
		/**
		 * Creates a new output pin.
		 * @param index The index of the input
		 */
		public AtomicFunctionOutputPin(int index)
		{
			super(AtomicFunction.this, index);
		}

		@Override
		public Object getValue()
		{
			if (m_evaluated)
			{
				return m_value;
			}
			Object[] ins = new Object[getInputArity()];
			for (int i = 0; i < m_inputPins.length; i++)
			{
				ins[i] = m_inputPins[i].getValue();
			}
			Object[] outs = AtomicFunction.this.getValue(ins);
			for (int i = 0; i < m_outputPins.length; i++)
			{
				m_outputPins[i].setValue(outs[i]);
			}
			if (!m_evaluated)
			{
				throw new FunctionException("Cannot get value");
			}
			return m_value;
		}
		
		@Override
		public AtomicFunctionOutputPin duplicate(boolean with_state)
		{
			AtomicFunctionOutputPin afop = new AtomicFunctionOutputPin(m_index);
			copyInto(afop, false);
			return afop;
		}
	}
}
