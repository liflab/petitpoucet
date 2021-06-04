package ca.uqac.lif.dag;

import java.util.Collection;

public interface Connectable
{
	/**
	 * Gets the input arity of the node. The input arity is the number of
	 * input pins of this node.
	 * @return The input arity
	 */
	/*@ pure @*/ public int getInputArity();
	
	/**
	 * Gets the output arity of the node. The input arity is the number of
	 * output pins of this node.
	 * @return The input arity
	 */
	/*@ pure @*/ public int getOutputArity();
	
	/**
	 * Gets the output pins of other nodes that are connected to a given
	 * input pin of this node.
	 * @param index The index of the current node's input pin
	 * @return The collection of output pins of other nodes
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's input arity
	 */
	/*@ pure non_null @*/ public Collection<Pin<? extends Node>> getInputLinks(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the input pins of other nodes that are connected to a given
	 * output pin of this node.
	 * @param index The index of the current node's output pin
	 * @return The collection of input pins of other nodes
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's output arity
	 */
	/*@ pure non_null @*/ public Collection<Pin<? extends Node>> getOutputLinks(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Adds a node's output pin to the set of connections of this node's
	 * input pin. If other pins are already associated to this input pin,
	 * they are kept. 
	 * @param index The index of the current node's input pin
	 * @param pin A node's output pin to add to the connections
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's input arity
	 */
	public void addToInput(int index, /*@ non_null @*/ Pin<? extends Node> pin) throws IndexOutOfBoundsException;
	
	/**
	 * Sets a node's output pin as the connection of this node's input
	 * pin. If other pins are already associated to this input pin, they
	 * are deleted. 
	 * @param index The index of the current node's input pin
	 * @param pin A node's output pin to add to the connections
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's input arity
	 */
	public void setToInput(int index, /*@ non_null @*/ Pin<? extends Node> pin) throws IndexOutOfBoundsException;
	
	/**
	 * Adds a node's input pin to the set of connections of this node's
	 * output pin. If other pins are already associated to this output pin,
	 * they are kept. 
	 * @param index The index of the current node's output pin
	 * @param pin A node's input pin to add to the connections
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's output arity
	 */
	public void addToOutput(int index, /*@ non_null @*/ Pin<? extends Node> pin) throws IndexOutOfBoundsException;
	
	/**
	 * Sets a node's input pin as the connection of this node's output
	 * pin. If other pins are already associated to this output pin, they
	 * are deleted. 
	 * @param index The index of the current node's output pin
	 * @param pin A node's input pin to add to the connections
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's output arity
	 */
	/*@ non_null @*/ public void setToOutput(int index, /*@ non_null @*/ Pin<? extends Node> pin) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the node's input pin for a given index.
	 * @param index The index
	 * @return The pin
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's input arity
	 */
	/*@ non_null @*/ public Pin<? extends Node> getInputPin(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the node's input pin for a given index.
	 * @param index The index
	 * @return The pin
	 * @throws IndexOutOfBoundsException If the index is incompatible with
	 * the node's output arity
	 */
	/*@ non_null @*/ public Pin<? extends Node> getOutputPin(int index) throws IndexOutOfBoundsException;
}
