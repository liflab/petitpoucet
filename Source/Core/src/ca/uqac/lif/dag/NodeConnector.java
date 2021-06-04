package ca.uqac.lif.dag;

public class NodeConnector
{
	/**
	 * Connects an output pin of a node to the input pin of another node.
	 * @param n1 The first node
	 * @param i1 The index of the output pin on {@code n1}
	 * @param n2 The second node
	 * @param i2 The index of the output pin on {@code n2}
	 */
	public static void connect(/*@ non_null @*/ Node n1, int i1, /*@ non_null @*/ Node n2, int i2)
	{
		n1.addToOutput(i1, n2.getInputPin(i2));
		n2.addToInput(i2, n1.getOutputPin(i1));
	}
}
