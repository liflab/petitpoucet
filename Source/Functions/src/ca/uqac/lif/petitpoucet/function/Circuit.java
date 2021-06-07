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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.util.Duplicable;

public class Circuit extends NestedNode implements Function, Duplicable, ExplanationQueryable
{
	protected Map<String,Object> m_context;
	
	/**
	 * A name that can be given to the circuit.
	 */
	/*@ null @*/ protected String m_name;

	public Circuit(int in_arity, int out_arity, String name)
	{
		super(in_arity, out_arity);
		m_name = name;
	}
	
	public Circuit(int in_arity, int out_arity)
	{
		this(in_arity, out_arity, null);
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
		for (Node n : m_internalNodes)
		{
			if (n instanceof Contextualizable)
			{
				((Contextualizable) n).setContext(key, value);
			}
		}
	}

	@Override
	public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int output_nb = NthOutput.mentionedOutput(part);
		if (output_nb < 0 || output_nb >= getOutputArity())
		{
			// Nothing to do
			return root;
		}
		Pin<? extends Node> start_pin = m_outputAssociations.get(output_nb);
		Part start_part = NthOutput.replaceOutBy(part, new NthOutput(start_pin.getIndex()));
		NodeFactory in_factory = new NodeFactory(factory);
		NestedNode sub_node = develop(start_part, start_pin.getNode(), in_factory);
		if (sub_node == null)
		{
			// No explanation for the inner circuit
			return root;
		}
		root.addChild(sub_node);
		// Tie input leaves to the inputs of the circuit 
		for (int i = 0; i < sub_node.getOutputArity(); i++)
		{
			Pin<? extends Node> pin = sub_node.getAssociatedOutput(i);
			if (!(pin.getNode() instanceof PartNode))
			{
				// Not a part node
				continue;
			}
			PartNode pn = (PartNode) pin.getNode();
			int mentioned_input = NthInput.mentionedInput(pn.getPart());
			if (mentioned_input < 0)
			{
				// No input mentioned
				continue;
			}
			int circuit_input = getNestedInput((Node) pn.getSubject(), mentioned_input);
			if (circuit_input >= 0)
			{
				PartNode leaf = factory.getPartNode(NthInput.replaceInBy(pn.getPart(), new NthInput(circuit_input)), this);
				NodeConnector.connect(sub_node, i, leaf, 0);
			}
		}
		return root;
	}

	/**
	 * From an output pin of an inner function, calculates the explanation and
	 * repeats the process if any input pins that are mentioned are connected to
	 * output pins of other functions. This has for effect of generating the
	 * explanation graph of the whole circuit.
	 * <p>
	 * The method encapsulates this graph into a nested node. This node has a
	 * single input pin (the output used as the starting point) and has as many
	 * output pins as the number of leaves in the resulting graph. Each of these
	 * pins is linked to the input pin of one of the functions in the circuit
	 * that are associated to an input pin of the circuit itself.
	 * 
	 * @param start The <em>output</em> part to explain, used as a starting point
	 * @param subject The inner function that must provide the explanation
	 * @param factory A factory used to obtain part nodes. This factory must be
	 * distinct from the one given to the circuit in a call to
	 * {@link #getExplanation(Part, NodeFactory)}
	 * @return The nested node containing the explanation graph of the entire
	 * circuit. 
	 */
	protected NestedNode develop(Part start, Node subject, NodeFactory factory)
	{
		Queue<PartNode> to_explore = new ArrayDeque<PartNode>();
		Set<PartNode> explored = new HashSet<PartNode>();
		PartNode root = null;
		if (subject instanceof ExplanationQueryable)
		{
			root = ((ExplanationQueryable) subject).getExplanation(start, factory);
			to_explore.add(root);
		}
		while (!to_explore.isEmpty())
		{
			PartNode current = to_explore.remove();
			explored.add(current);
			NestedNodeCrawler c = new NestedNodeCrawler(current);
			c.crawl();
			for (Node n : c.getLeaves())
			{
				if (!(n instanceof PartNode) || explored.contains(n))
				{
					continue;
				}
				PartNode pn = (PartNode) n;
				Part current_part = pn.getPart();					
				int num_input = NthInput.mentionedInput(current_part);
				if (num_input < 0)
				{
					continue;
				}
				// This node mentions the input of a function; what is this input connected to?
				Node current_subject = (Node) pn.getSubject();
				Pin<? extends Node> pin = getPin(current_subject.getInputLinks(num_input));
				if (pin == null)
				{
					continue;
				}
				Node upstream_subject = pin.getNode();
				if (!(upstream_subject instanceof ExplanationQueryable))
				{
					continue;
				}
				// Get explanation for this function's output
				Part upstream_part = NthInput.replaceInByOut(current_part, pin.getIndex());
				PartNode upstream_node = factory.getPartNode(upstream_part, upstream_subject);
				if (to_explore.contains(upstream_node) || explored.contains(upstream_node))
				{
					continue;
				}
				PartNode upstream_root = ((ExplanationQueryable) upstream_subject).getExplanation(upstream_part, factory);
				pn.addChild(upstream_root);
				to_explore.add(upstream_root);
			}
		}
		if (root == null)
		{
			return null;	
		}
		// All nodes explored; create nested node
		return NestedNode.createFromTree(root);
	}

	/**
	 * Extracts the first pin obtained from a collection. The main purpose of
	 * this method is to obtain the only element of a collection which we know
	 * is a singleton.
	 * @param pins The collection of pins
	 * @return The pin
	 */
	protected static Pin<? extends Node> getPin(Collection<Pin<? extends Node>> pins)
	{
		Pin<? extends Node> pin = null;
		for (Pin<? extends Node> p : pins)
		{
			pin = p;
			break;
		}
		return pin;
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
	
	@Override
	public Circuit duplicate()
	{
		return duplicate(false);
	}
	
	@Override
	public Circuit duplicate(boolean with_state)
	{
		Circuit c = new Circuit(getInputArity(), getOutputArity());
		copyInto(c, with_state);
		return c;
	}
	
	protected void copyInto(Circuit c, boolean with_state)
	{
		super.copyInto(c, with_state);
		c.m_name = m_name;
	}

	@Override
	public PartNode getExplanation(Part part)
	{
		return getExplanation(part, new NodeFactory());
	}
	
	@Override
	public String toString()
	{
		if (m_name == null)
		{
			return super.toString();
		}
		return m_name;
	}

}
