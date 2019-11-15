package ca.uqac.lif.petitpoucet.functions.logic;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction;
import ca.uqac.lif.petitpoucet.functions.CumulableFunction;
import ca.uqac.lif.petitpoucet.functions.LazyFunction;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction.BinaryFunctionQueryable.Inputs;

public class LazyBooleans 
{
	public static final transient And and = new And();
	
	public static final transient Or or = new Or();
	
	public static final transient Booleans.Not not = Booleans.not;
	
	private LazyBooleans()
	{
		super();
	}
	
	protected static class And extends BinaryFunction<Boolean,Boolean,Boolean> implements CumulableFunction<Boolean>, LazyFunction
	{
		protected static final transient BinaryFunctionQueryable s_queryableBoth = new BinaryFunctionQueryable("LazyBooleans.And", Inputs.BOTH);

		protected static final transient BinaryFunctionQueryable s_queryableAny = new BinaryFunctionQueryable("LazyBooleans.And", Inputs.ANY);

		protected static final transient BinaryFunctionQueryable s_queryableLeft = new BinaryFunctionQueryable("LazyBooleans.And", Inputs.LEFT);

		protected static final transient BinaryFunctionQueryable s_queryableRight = new BinaryFunctionQueryable("LazyBooleans.And", Inputs.RIGHT);

		protected And()
		{
			super(Boolean.class, Boolean.class, Boolean.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
		{
			Boolean left = (Boolean) inputs[0];
			if (left == false)
			{
				outputs[0] = false;
				if (track)
				{
					return s_queryableLeft;
				}
				return null;
			}
			Boolean right = (Boolean) inputs[1];
			if (right == null)
			{
				return null;
			}
			outputs[0] = left && right;
			return getDependency(left, right, track);
		}

		@Override
		public And duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Boolean getInitialValue() 
		{
			return true;
		}

		protected static BinaryFunctionQueryable getDependency(boolean x, boolean y, boolean track)
		{
			if (!track)
			{
				return null;
			}
			if (x == false)
			{
				if (y == false)
				{
					return s_queryableAny;
				}
				return s_queryableLeft;
			}
			if (y == false)
			{
				return s_queryableRight;
			}
			return s_queryableBoth;
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public Object read(ObjectReader<?> reader, Object o) throws ReadException
		{
			return this;
		}
		
		@Override
		public String toString()
		{
			return "LazyAnd";
		}
	}
	
	protected static class Or extends BinaryFunction<Boolean,Boolean,Boolean> implements CumulableFunction<Boolean>
	{
		protected static final transient BinaryFunctionQueryable s_queryableBoth = new BinaryFunctionQueryable("Booleans.Or", Inputs.BOTH);

		protected static final transient BinaryFunctionQueryable s_queryableAny = new BinaryFunctionQueryable("Booleans.Or", Inputs.ANY);

		protected static final transient BinaryFunctionQueryable s_queryableLeft = new BinaryFunctionQueryable("Booleans.Or", Inputs.LEFT);

		protected static final transient BinaryFunctionQueryable s_queryableRight = new BinaryFunctionQueryable("Booleans.Or", Inputs.RIGHT);

		protected Or()
		{
			super(Boolean.class, Boolean.class, Boolean.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track) 
		{
			Boolean left = (Boolean) inputs[0];
			if (left == true)
			{
				outputs[0] = true;
				if (track)
				{
					return s_queryableLeft;
				}
				return null;
			}
			Boolean right = (Boolean) inputs[1];
			if (right == null)
			{
				return null;
			}
			outputs[0] = left || right;
			return getDependency(left, right, track);
		}

		@Override
		public Or duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Boolean getInitialValue() 
		{
			return true;
		}

		protected static BinaryFunctionQueryable getDependency(boolean x, boolean y, boolean track)
		{
			if (!track)
			{
				return null;
			}
			if (x == true)
			{
				if (y == true)
				{
					return s_queryableAny;
				}
				return s_queryableLeft;
			}
			if (y == true)
			{
				return s_queryableRight;
			}
			return s_queryableBoth;
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public Object read(ObjectReader<?> reader, Object o) throws ReadException
		{
			return this;
		}
		
		@Override
		public String toString()
		{
			return "LazyOr";
		}
	}

}
