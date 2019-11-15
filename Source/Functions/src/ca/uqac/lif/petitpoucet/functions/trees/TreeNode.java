package ca.uqac.lif.petitpoucet.functions.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode 
{
	protected String m_name;

	protected List<TreeNode> m_children;

	protected Map<String,Object> m_attributes;

	public TreeNode(String name)
	{
		super();
		m_name = name;
		m_children = new ArrayList<TreeNode>();
		m_attributes = new HashMap<String,Object>();
	}

	public TreeNode addChild(TreeNode child)
	{
		m_children.add(child);
		return this;
	}

	public List<TreeNode> getChildren()
	{
		return m_children;
	}

	public String getName()
	{
		return m_name;
	}

	public Object get(String att_name)
	{
		return m_attributes.get(att_name);
	}

	public TreeNode set(String name, Object value)
	{
		m_attributes.put(name, value);
		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		toString(out, "");
		return out.toString();
	}

	protected void toString(StringBuilder out, String indent)
	{
		out.append(indent).append(m_name);
		if (!m_attributes.isEmpty())
		{
			out.append("(").append(m_attributes).append(")\n");
		}
		indent += " ";
		for (TreeNode tn : m_children)
		{
			tn.toString(out, indent);
		}
	}
}
