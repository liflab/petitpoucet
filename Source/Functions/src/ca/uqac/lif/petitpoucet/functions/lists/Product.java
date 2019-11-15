package ca.uqac.lif.petitpoucet.functions.lists;

import java.awt.List;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;

public class Product implements Function
{
	protected int m_inArity;
	
	protected static final transient Map<Integer,Product> s_pool = new LinkedHashMap<Integer,Product>();
	
	public static Product get(int arity)
	{
		Product p = s_pool.get(arity);
		if (p == null)
		{
			p = new Product(arity);
			s_pool.put(arity, p);
		}
		return p;
	}
	
	protected Product(int arity)
	{
		super();
		m_inArity = arity;
	}
	
	@Override
	public String toString()
	{
		return "x";
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
	public Product duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public Function duplicate() 
	{
		return duplicate(false);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		return evaluate(inputs, outputs, c, true);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs, boolean track)
	{
		return evaluate(inputs, outputs, null, track);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return evaluate(inputs, outputs, null, true);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		if (index >= 0 && index < m_inArity)
		{
			return List.class;
		}
		return null;
	}

	@Override
	public Class<?> getOutputType(int index)
	{
		if (index == 0)
		{
			return List.class;
		}
		return null;
	}

	@Override
	public int getInputArity()
	{
		return m_inArity;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}

	@Override
	public void reset()
	{
		// Nothing to do
	}
}
