/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
import java.util.Iterator;
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

/**
 * An object encasing a graph of connected functions and acting externally as
 * a single function.
 * 
 * @author Sylvain Hallé
 */
public class Circuit extends NestedNode implements Function, Duplicable, ExplanationQueryable
{
	/**
	 * An array of input pins for the function. The size of the array
	 * corresponds to the function's input arity.
	 */
	/*@ non_null @*/ protected CircuitInputPin[] m_inputPins;
	
	/**
	 * An array of output pins for the function. The size of the array
	 * corresponds to the function's output arity.
	 */
	/*@ non_null @*/ protected CircuitOutputPin[] m_outputPins;
	
	/**
	 * A context that can be assigned to a circuit.
	 */
	protected Map<String,Object> m_context;
	
	/**
	 * A name that can be given to the circuit.
	 */
	/*@ null @*/ protected String m_name;

	/**
	 * Creates a new empty circuit instance.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 * @param name A name given to the circuit. This will be used in lineage
	 * trees instead of displaying the meaningless default string produced by
	 * Java.
	 */
	public Circuit(int in_arity, int out_arity, String name)
	{
		super(in_arity, out_arity);
		m_name = name;
		m_inputPins = new CircuitInputPin[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_inputPins[i] = new CircuitInputPin(i);
		}
		m_outputPins = new CircuitOutputPin[out_arity];
		for (int i = 0; i < out_arity; i++)
		{
			m_outputPins[i] = new CircuitOutputPin(i);
		}
	}
	
	/**
	 * Creates a new empty circuit instance.
	 * @param in_arity The input arity of the circuit
	 * @param out_arity The output arity of the circuit
	 */
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
		int input_nb = NthInput.mentionedInput(part);
		if (input_nb >= 0 && input_nb < getInputArity())
		{
			return explainInput(input_nb, part, factory);
		}
		int output_nb = NthOutput.mentionedOutput(part);
		if (output_nb >= 0 && output_nb < getOutputArity())
		{
			return explainOutput(output_nb, part, factory);
		}
		// Nothing to do
		return factory.getPartNode(part, this);
	}
	
	/**
	 * Explains a part of the input of the circuit in terms of parts of its
	 * output.
	 * @param input_nb The number of the output pin
	 * @param part The part of the input to explain
	 * @param factory A factory to generate explainability nodes
	 * @return The root of the generated tree
	 */
	/*@ non_null @*/ protected PartNode explainInput(int input_nb, Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		Pin<? extends Node> start_pin = m_inputAssociations.get(input_nb);
		Part start_part = NthInput.replaceInBy(part, new NthInput(start_pin.getIndex()));
		NodeFactory in_factory = factory.getFactory(part, this);
		NestedNode sub_node = developToOutput(start_part, start_pin.getNode(), in_factory);
		if (sub_node == null)
		{
			// No explanation for the inner circuit
			return root;
		}
		root.addChild(sub_node);
		// Tie leaves to the outputs of the circuit 
		for (int i = 0; i < sub_node.getOutputArity(); i++)
		{
			Pin<? extends Node> pin = sub_node.getAssociatedOutput(i);
			if (!(pin.getNode() instanceof PartNode))
			{
				// Not a part node
				continue;
			}
			PartNode pn = (PartNode) pin.getNode();
			int mentioned_output = NthOutput.mentionedOutput(pn.getPart());
			if (mentioned_output < 0)
			{
				// No input mentioned
				continue;
			}
			int circuit_output = getNestedOutput((Node) pn.getSubject(), mentioned_output);
			if (circuit_output >= 0)
			{
				PartNode leaf = factory.getPartNode(NthOutput.replaceOutBy(pn.getPart(), new NthOutput(circuit_output)), this);
				NodeConnector.connect(sub_node, i, leaf, 0);
			}
		}
		return root;
	}
	
	/**
	 * Explains a part of the output of the circuit in terms of parts of its
	 * input.
	 * @param output_nb The number of the output pin
	 * @param part The part of the output to explain
	 * @param factory A factory to generate explainability nodes
	 * @return The root of the generated tree
	 */
	/*@ non_null @*/ protected PartNode explainOutput(int output_nb, Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		Pin<? extends Node> start_pin = m_outputAssociations.get(output_nb);
		Part start_part = NthOutput.replaceOutBy(part, new NthOutput(start_pin.getIndex()));
		NodeFactory in_factory = factory.getFactory(part, this);
		NestedNode sub_node = developToInput(start_part, start_pin.getNode(), in_factory);
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
			Object o_subject = pn.getSubject();
			if (o_subject instanceof Node)
			{
				int circuit_input = getNestedInput((Node) pn.getSubject(), mentioned_input);
				if (circuit_input >= 0)
				{
					PartNode leaf = factory.getPartNode(NthInput.replaceInBy(pn.getPart(), new NthInput(circuit_input)), this);
					NodeConnector.connect(sub_node, i, leaf, 0);
				}				
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
	protected static NestedNode developToInput(Part start, Node subject, NodeFactory factory)
	{
		Queue<PartNode> to_explore = new ArrayDeque<>();
		Set<PartNode> explored = new HashSet<>();
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
				Object o_subject = pn.getSubject();
				if (!(o_subject instanceof Node))
				{
					continue;
				}
				Node current_subject = (Node) o_subject;
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
	 * From an input pin of an inner function, calculates the explanation and
	 * repeats the process if any output pins that are mentioned are connected to
	 * input pins of other functions. This has for effect of generating the
	 * (input-to-output) explanation graph of the whole circuit.
	 * <p>
	 * The method encapsulates this graph into a nested node. This node has a
	 * single input pin (the output used as the starting point) and has as many
	 * output pins as the number of leaves in the resulting graph. Each of these
	 * pins is linked to the output pin of one of the functions in the circuit
	 * that are associated to an output pin of the circuit itself.
	 * 
	 * @param start The <em>input</em> part to explain, used as a starting point
	 * @param subject The inner function that must provide the explanation
	 * @param factory A factory used to obtain part nodes. This factory must be
	 * distinct from the one given to the circuit in a call to
	 * {@link #getExplanation(Part, NodeFactory)}
	 * @return The nested node containing the explanation graph of the entire
	 * circuit. 
	 */
	protected static NestedNode developToOutput(Part start, Node subject, NodeFactory factory)
	{
		Queue<PartNode> to_explore = new ArrayDeque<>();
		Set<PartNode> explored = new HashSet<>();
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
				int num_output = NthOutput.mentionedOutput(current_part);
				if (num_output < 0)
				{
					continue;
				}
				// This node mentions the input of a function; what is this output connected to?
				Node current_subject = (Node) pn.getSubject();
				Pin<? extends Node> pin = getPin(current_subject.getOutputLinks(num_output));
				if (pin == null)
				{
					continue;
				}
				Node downstream_subject = pin.getNode();
				if (!(downstream_subject instanceof ExplanationQueryable))
				{
					continue;
				}
				// Get explanation for this function's input
				Part downstream_part = NthOutput.replaceOutByIn(current_part, pin.getIndex());
				PartNode downstream_node = factory.getPartNode(downstream_part, downstream_subject);
				if (to_explore.contains(downstream_node) || explored.contains(downstream_node))
				{
					continue;
				}
				PartNode downstream_root = ((ExplanationQueryable) downstream_subject).getExplanation(downstream_part, factory);
				pn.addChild(downstream_root);
				to_explore.add(downstream_root);
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
		if (pins.isEmpty())
		{
			return null;
		}
		Iterator<Pin<? extends Node>> it = pins.iterator();
		return it.next();
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
			m_outputPins[i].setValue(out[i]);
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
		for (CircuitInputPin p : m_inputPins)
		{
			p.reset();
		}
		for (CircuitOutputPin p : m_outputPins)
		{
			p.reset();
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
	/*@ non_null @*/ public CircuitInputPin getInputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_inputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_inputPins[index];
	}

	@Override
	/*@ non_null @*/ public CircuitOutputPin getOutputPin(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= m_outputs.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return m_outputPins[index];
	}

	@Override
	public PartNode getExplanation(Part part)
	{
		return getExplanation(part, NodeFactory.getFactory());
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
	
	public class CircuitInputPin extends FunctionPin<Circuit>
	{
		/**
		 * Creates a new input pin.
		 * @param index The index of the input
		 */
		public CircuitInputPin(int index)
		{
			super(Circuit.this, index);
		}
		
		@Override
		public void setValue(Object o)
		{
			Pin<? extends Node> pin = m_inputAssociations.get(m_index);
			if (pin instanceof FunctionPin)
			{
				((FunctionPin<?>) pin).setValue(o);
			}
			m_value = o;
			m_evaluated = true;
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
			}
			if (!m_evaluated)
			{
				throw new FunctionException("Cannot get value");
			}
			return m_value;
		}
		
		@Override
		public CircuitInputPin duplicate(boolean with_state)
		{
			CircuitInputPin afip = new CircuitInputPin(m_index);
			this.copyInto(afip, false);
			return afip;
		}
	}
	
	public class CircuitOutputPin extends FunctionPin<Circuit>
	{
		/**
		 * Creates a new output pin.
		 * @param index The index of the input
		 */
		public CircuitOutputPin(int index)
		{
			super(Circuit.this, index);
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
				Pin<? extends Node> pin = m_inputAssociations.get(i);
				if (pin instanceof FunctionPin)
				{
					((FunctionPin<?>) pin).setValue(ins[i]);
				}
			}			
			m_evaluated = true;
			Object[] outs = new Object[getOutputArity()];
			for (int i = 0; i < outs.length; i++)
			{
				Pin<? extends Node> pin = m_outputAssociations.get(i);
				if (!(pin instanceof FunctionPin))
				{
					throw new FunctionException("Invalid circuit");
				}
				outs[i] = ((FunctionPin<?>) pin).getValue();
			}
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
		public CircuitOutputPin duplicate(boolean with_state)
		{
			CircuitOutputPin afop = new CircuitOutputPin(m_index);
			this.copyInto(afop, false);
			return afop;
		}
	}

}
