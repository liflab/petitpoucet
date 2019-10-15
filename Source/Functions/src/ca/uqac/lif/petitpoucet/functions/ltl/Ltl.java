package ca.uqac.lif.petitpoucet.functions.ltl;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.functions.Function;

public class Ltl 
{
	public static final transient Globally globally = new Globally();
	
	private Ltl()
	{
		super();
	}
	
	public static class Globally extends UnaryOperator
	{
		protected Globally()
		{
			super(true);
		}
		
		@Override
		public String toString()
		{
			return "Globally";
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException
		{
			return null;
		}

		@Override
		public Globally read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public Function duplicate(boolean with_state) 
		{
			return this;
		}
	}
}
