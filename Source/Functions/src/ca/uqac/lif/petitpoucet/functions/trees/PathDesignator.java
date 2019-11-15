package ca.uqac.lif.petitpoucet.functions.trees;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;

public class PathDesignator implements Designator
{
	protected List<String> m_elements;
	
	public PathDesignator(String ... elements)
	{
		super();
		m_elements = new ArrayList<String>(elements.length);
		for (String e : elements)
		{
			m_elements.add(e);
		}
	}
	
	public PathDesignator(List<String> elements)
	{
		super();
		m_elements = new ArrayList<String>(elements.size());
		m_elements.addAll(elements);
	}
	
	protected void addTo(String e)
	{
		m_elements.add(e);
	}
	
	public PathDesignator append(String e)
	{
		PathDesignator pd = new PathDesignator(m_elements);
		pd.addTo(e);
		return pd;
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < m_elements.size(); i++)
		{
			if (i > 0)
			{
				out.append("/");
			}
			out.append(m_elements.get(i));
		}
		return out.toString();
	}
	
	@Override
	public boolean appliesTo(Object o) 
	{
		return o instanceof TreeNode;
	}

	@Override
	public Designator peek()
	{
		return this;
	}

	@Override
	public Designator tail()
	{
		return null;
	}
}
