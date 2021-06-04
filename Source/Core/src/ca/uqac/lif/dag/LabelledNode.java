package ca.uqac.lif.dag;

public class LabelledNode extends Node
{
	/**
	 * An object associated to a node.
	 */
	/*@ null @*/ protected Object m_label;
	
	public LabelledNode(/*@ null @*/ Object label)
	{
		super(1, 1);
		m_label = label;
	}
	
	@Override
	public String toString()
	{
		if (m_label != null)
		{
			return m_label.toString();
		}
		return "null";
	}
	
	
	/**
	 * Connects the first output pin of the current node to the first input pin
	 * of another node.
	 * @param n The other node
	 */
	public void addChild(Node n)
	{
		NodeConnector.connect(this, 0, n, 0);
	}
}
