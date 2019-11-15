package ca.uqac.lif.petitpoucet.functions.trees;

import java.util.LinkedHashMap;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

public class GetAttribute extends UnaryFunction<TreeNode,Object>
{
	protected String m_attribute;
	
	protected Map<String,FunctionQueryable> s_pool = new LinkedHashMap<String,FunctionQueryable>();
	
	protected GetAttribute(String name)
	{
		super(TreeNode.class, Object.class);
		m_attribute = name;
	}
	
	public static GetAttribute get(String name)
	{
		return new GetAttribute(name);
	}
	
	@Override
	public String toString()
	{
		return "Get " + m_attribute;
	}
	
	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetAttribute duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		TreeNode node = (TreeNode) inputs[0];
		outputs[0] = node.get(m_attribute);
		FunctionQueryable q = null;
		if (track)
		{
			q = s_pool.get(m_attribute);
			if (q == null)
			{
				q = new FunctionQueryable(m_attribute, 1, 1);
				s_pool.put(m_attribute, q);
			}
		}
		return q;
	}
	
	public static class AttributeDesignator implements Designator
	{
		protected String m_attribute;
		
		protected AttributeDesignator(String name)
		{
			super();
			m_attribute = name;
		}
		
		public static AttributeDesignator get(String name)
		{
			return new AttributeDesignator(name);
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
		
		@Override
		public String toString()
		{
			return "Value of " + m_attribute;
		}
		
	}

}
