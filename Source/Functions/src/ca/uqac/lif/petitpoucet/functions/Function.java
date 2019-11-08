package ca.uqac.lif.petitpoucet.functions;

import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.Readable;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.StateDuplicable;

public interface Function extends Printable, Readable, StateDuplicable<Function>
{
	public Queryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track);
	
	public Queryable evaluate(Object[] inputs, Object[] outputs, Context c);
	
	public Queryable evaluate(Object[] inputs, Object[] outputs, boolean track);
	
	public Queryable evaluate(Object[] inputs, Object[] outputs);
	
	public Class<?> getInputType(int index);
	
	public Class<?> getOutputType(int index);
	
	public int getInputArity();
	
	public int getOutputArity();
	
	public void reset();
	
	public class FunctionException extends RuntimeException
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 2L;
		
		public FunctionException(Throwable t)
		{
			super(t);
		}
		
		public FunctionException(String message)
		{
			super(message);
		}
	}
}
