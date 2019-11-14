package ca.uqac.lif.petitpoucet.functions.reflect;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

public class InstanceOf extends UnaryFunction<Object,Boolean>
{
	/*@ non_null @*/ protected Class<?> m_class;
	
	protected static final transient FunctionQueryable s_queryable = new FunctionQueryable("InstanceOf", 1, 1);
	
	public InstanceOf(/*@ non_null @*/ Class<?> c)
	{
		super(Object.class, Boolean.class);
		m_class = c;
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
	public InstanceOf duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		Object o = inputs[0];
		if (o == null)
		{
			outputs[0] = false;
		}
		else
		{
			outputs[0] = m_class.isInstance(o);
		}
		
		if (track)
		{
			return s_queryable;
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return "InstanceOf " + m_class.getSimpleName();
	}
}
