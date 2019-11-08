package ca.uqac.lif.petitpoucet.functions;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.common.Context;

public class Equals extends BinaryFunction<Object,Object,Boolean>
{
	public static final transient Equals instance = new Equals();
	
	protected Equals()
	{
		super(Object.class, Object.class, Boolean.class);
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
	public Function duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		Object left = inputs[0];
		Object right = inputs[1];
		if (left instanceof String && right instanceof String)
		{
			boolean b = ((String) left).compareTo((String) right) == 0;
			outputs[0] = b;
			if (b)
			{
				return BinaryFunctionQueryable.
			}
		}
	}
}
