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
package ca.uqac.lif.dag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.util.Duplicable;

/**
 * Abstract entity having input and output pins that can be linked to pins of
 * of other nodes.
 * 
 * @author Sylvain Hallé
 */
public class Node implements Connectable, Duplicable
{
	/**
	 * A map associating input pin indices with a list of output pins of other
	 * nodes.
	 */
	protected Map<Integer,List<Pin<? extends Node>>> m_inputs;
	
	/**
	 * A map associating output pin indices with a list of input pins of other
	 * nodes.
	 */
	protected Map<Integer,List<Pin<? extends Node>>> m_outputs;
	
	/**
	 * Creates a new node.
	 * @param in_arity The number of input pins on this node
	 * @param out_arity The number of output pins on this node
	 */
	public Node(int in_arity, int out_arity)
	{
		super();
		m_inputs = new HashMap<Integer,List<Pin<? extends Node>>>(in_arity);
		for (int i = 0; i < in_arity; i++)
		{
			m_inputs.put(i, new ArrayList<Pin<? extends Node>>());
		}
		m_outputs = new HashMap<Integer,List<Pin<? extends Node>>>(out_arity);
		for (int i = 0; i < out_arity; i++)
		{
			m_outputs.put(i, new ArrayList<Pin<? extends Node>>());
		}
	}
	
	@Override
	/*@ pure @*/ public int getInputArity()
	{
		return m_inputs.size();
	}
	
	@Override
	/*@ pure @*/ public int getOutputArity()
	{
		return m_outputs.size();
	}
	
	@Override
	/*@ pure non_null @*/ public Collection<Pin<? extends Node>> getInputLinks(int index)
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_inputs.get(index);
	}
	
	@Override
	/*@ pure non_null @*/ public List<Pin<? extends Node>> getOutputLinks(int index)
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_outputs.get(index);
	}
	
	@Override
	public void addToInput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_inputs.get(index).add(pin);
	}
	
	@Override
	public void setToInput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_inputs.get(index).clear();
		m_inputs.get(index).add(pin);
	}
	
	@Override
	public void removeFromInput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_inputs.get(index).remove(pin);
	}
	
	@Override
	public void addToOutput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_outputs.get(index).add(pin);
	}
	
	@Override
	/*@ non_null @*/ public void setToOutput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_outputs.get(index).clear();
		m_outputs.get(index).add(pin);
	}
	
	@Override
	public void removeFromOutput(int index, /*@ non_null @*/ Pin<? extends Node> pin)
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		m_outputs.get(index).remove(pin);
	}
	
	@Override
	/*@ non_null @*/ public Pin<? extends Node> getInputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return new Pin<Node>(this, index);
	}
	
	@Override
	/*@ non_null @*/ public Pin<? extends Node> getOutputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return new Pin<Node>(this, index);
	}
	
	@Override
	public Node duplicate()
	{
		return duplicate(false);
	}
	
	@Override
	public Node duplicate(boolean with_state)
	{
		Node n = new Node(getInputArity(), getOutputArity());
		copyInto(n, with_state);
		return n;
	}
	
	/**
	 * Copies the contents of the current node into another node instance.
	 * @param n The other node
	 * @param with_state Set to {@code true} for a stateful copy, {@link false}
	 * otherwise
	 */
	protected void copyInto(Node n, boolean with_state)
	{
		// Nothing to do
	}
}
