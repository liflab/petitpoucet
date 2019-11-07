package ca.uqac.lif.petitpoucet.functions.numbers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction;
import ca.uqac.lif.petitpoucet.functions.CumulableFunction;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.SlidableFunction;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction.BinaryFunctionQueryable.Inputs;

public class Numbers 
{
	public static final transient Addition addition = new Addition();

	public static final transient Subtraction subtraction = new Subtraction();

	public static final transient Multiplication multiplication = new Multiplication();

	public static final transient Division division = new Division();

	public static final transient AbsoluteValue abs = new AbsoluteValue();

	public static final transient Signum signum = new Signum();

	public static final transient NumberCast cast = new NumberCast();
	
	public static final transient AverageList avg = new AverageList();
	
	public static final transient IsGreaterThan isGreaterThan = new IsGreaterThan();
	
	public static final transient IsEven isEven = new IsEven();

	private Numbers()
	{
		super();
	}

	protected static class AbsoluteValue extends UnaryFunction<Number,Number>
	{
		protected static final transient FunctionQueryable s_queryable = new FunctionQueryable("Numbers.AbsoluteValue", 1, 1);

		protected AbsoluteValue()
		{
			super(Number.class, Number.class);
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public AbsoluteValue read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public AbsoluteValue duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = Math.abs(((Number) inputs[0]).floatValue());
			return s_queryable;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class AverageList extends UnaryFunction<List,Number>
	{
		protected static final transient Map<Integer,AverageListQueryable> s_queryablePool = new LinkedHashMap<Integer,AverageListQueryable>();
		
		protected AverageList()
		{
			super(List.class, Number.class);
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public AverageList read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public AverageList duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public AverageListQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			List<?> list = (List<?>) inputs[0];
			float total = 0f, num_values = 0f;
			for (Object o : list)
			{
				total += ((Number) o).floatValue();
				num_values++;
			}
			if (num_values == 0)
			{
				outputs[0] = 0f;
			}
			else
			{
				outputs[0] = total / num_values;
			}
			AverageListQueryable geq = s_queryablePool.get((int) num_values);
			if (geq == null)
			{
				geq = new AverageListQueryable(toString(), (int) num_values);
				s_queryablePool.put((int) num_values, geq);
			}
			return geq;
		}
		
		public static class AverageListQueryable extends FunctionQueryable
		{
			protected int m_length;
			
			public AverageListQueryable(String reference, int length)
			{
				super(reference, 1, 1);
				m_length = length;
			}
			
			@Override
			protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
					Designator tail, TraceabilityNode root, Tracer factory)
			{
				List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(m_length);
				TraceabilityNode and = factory.getAndNode();
				for (int i = 0; i < m_length; i++)
				{
					ComposedDesignator cd = new ComposedDesignator(tail, NthElement.get(i), NthInput.get(0));
					TraceabilityNode node = factory.getObjectNode(cd, this);
					and.addChild(node, Quality.EXACT);
					leaves.add(node);
				}
				root.addChild(and, Quality.EXACT);
				return leaves;
			}
			
			@Override
			public AverageListQueryable duplicate(boolean with_state)
			{
				return new AverageListQueryable(m_reference, m_length);
			}
			
			@Override
			public String toString()
			{
				return "AVG";
			}
		}
	}

	protected static class NumberCast extends UnaryFunction<Object,Number>
	{
		protected static final transient FunctionQueryable s_queryable = new FunctionQueryable("Numbers.NumberCast", 1, 1);

		protected NumberCast()
		{
			super(Object.class, Number.class);
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public NumberCast read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public NumberCast duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			Number num;
			if (inputs[0] instanceof Number)
			{
				num = (Number) inputs[0];
			}
			else
			{
				num = Float.parseFloat(inputs[0].toString());
			}
			outputs[0] = num;
			return s_queryable;
		}
	}

	protected static class Signum extends UnaryFunction<Number,Number>
	{
		protected static final transient FunctionQueryable s_queryable = new FunctionQueryable("Numbers.Signum", 1, 1);

		protected Signum()
		{
			super(Number.class, Number.class);
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public Signum read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public Signum duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			float f = ((Number) inputs[0]).floatValue();
			if (f == 0)
			{
				outputs[0] = 0;
			}
			else if (f > 0)
			{
				outputs[0] = 1;
			}
			else
			{
				outputs[0] = -1;
			}
			return s_queryable;
		}
	}
	
	protected static class IsEven extends UnaryFunction<Number,Boolean>
	{
		protected static final transient FunctionQueryable s_queryable = new FunctionQueryable("Numbers.IsEven", 1, 1);

		protected IsEven()
		{
			super(Number.class, Boolean.class);
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException 
		{
			return null;
		}

		@Override
		public IsEven read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public IsEven duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			float f = ((Number) inputs[0]).floatValue();
			outputs[0] = f % 2 == 0;
			return s_queryable;
		}
	}

	protected static class Addition extends BinaryFunction<Number,Number,Number> implements CumulableFunction<Number>
	{
		protected static BinaryFunctionQueryable s_queryable = new BinaryFunctionQueryable("Numbers.Addition", Inputs.BOTH);

		protected Addition()
		{
			super(Number.class, Number.class, Number.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = ((Number) inputs[0]).floatValue() + ((Number) inputs[1]).floatValue();
			return s_queryable;
		}

		@Override
		public Addition duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Number getInitialValue() 
		{
			return 0;
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
	}

	protected static class Subtraction extends BinaryFunction<Number,Number,Number> implements CumulableFunction<Number>
	{
		protected static BinaryFunctionQueryable s_queryable = new BinaryFunctionQueryable("Numbers.Subtraction", Inputs.BOTH);

		protected Subtraction()
		{
			super(Number.class, Number.class, Number.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = ((Number) inputs[0]).floatValue() - ((Number) inputs[1]).floatValue();
			return s_queryable;
		}

		@Override
		public Subtraction duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Number getInitialValue() 
		{
			return 0;
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
	}

	protected static class Multiplication extends BinaryFunction<Number,Number,Number> implements CumulableFunction<Number>
	{
		protected static BinaryFunctionQueryable s_queryableBoth = new BinaryFunctionQueryable("Numbers.Multiplication", Inputs.BOTH);

		protected static BinaryFunctionQueryable s_queryableAny = new BinaryFunctionQueryable("Numbers.Multiplication", Inputs.ANY);

		protected static BinaryFunctionQueryable s_queryableLeft = new BinaryFunctionQueryable("Numbers.Multiplication", Inputs.LEFT);

		protected static BinaryFunctionQueryable s_queryableRight = new BinaryFunctionQueryable("Numbers.Multiplication", Inputs.RIGHT);

		protected Multiplication()
		{
			super(Number.class, Number.class, Number.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = ((Number) inputs[0]).floatValue() * ((Number) inputs[1]).floatValue();
			return getDependency(((Number) inputs[0]).floatValue(), ((Number) inputs[1]).floatValue());
		}

		@Override
		public Multiplication duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Number getInitialValue() 
		{
			return 1;
		}

		protected static BinaryFunctionQueryable getDependency(float x, float y)
		{
			if (x == 0)
			{
				if (y == 0)
				{
					return s_queryableAny;
				}
				return s_queryableLeft;
			}
			if (y == 0)
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
	}

	protected static class Division extends BinaryFunction<Number,Number,Number> implements CumulableFunction<Number>
	{
		protected static BinaryFunctionQueryable s_queryableBoth = new BinaryFunctionQueryable("Numbers.Division", Inputs.BOTH);

		protected static BinaryFunctionQueryable s_queryableAny = new BinaryFunctionQueryable("Numbers.Division", Inputs.ANY);

		protected static BinaryFunctionQueryable s_queryableLeft = new BinaryFunctionQueryable("Numbers.Division", Inputs.LEFT);

		protected static BinaryFunctionQueryable s_queryableRight = new BinaryFunctionQueryable("Numbers.Division", Inputs.RIGHT);

		protected Division()
		{
			super(Number.class, Number.class, Number.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = ((Number) inputs[0]).floatValue() / ((Number) inputs[1]).floatValue();
			return getDependency(((Number) inputs[0]).floatValue(), ((Number) inputs[1]).floatValue());
		}

		@Override
		public Division duplicate(boolean with_state) 
		{
			return this;
		}

		@Override
		public Number getInitialValue() 
		{
			return 1;
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

		protected static BinaryFunctionQueryable getDependency(float x, float y)
		{
			if (x == 0)
			{
				if (y == 0)
				{
					return s_queryableBoth;
				}
				return s_queryableLeft;
			}
			if (y == 0)
			{
				return s_queryableRight;
			}
			return s_queryableBoth;
		}
	}
	
	protected static class IsGreaterThan extends BinaryFunction<Number,Number,Boolean>
	{
		protected static BinaryFunctionQueryable s_queryable = new BinaryFunctionQueryable("Numbers.IsGreaterThan", Inputs.BOTH);

		protected IsGreaterThan()
		{
			super(Number.class, Number.class, Boolean.class);
		}

		@Override
		public BinaryFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			outputs[0] = ((Number) inputs[0]).floatValue() > ((Number) inputs[1]).floatValue();
			return s_queryable;
		}

		@Override
		public IsGreaterThan duplicate(boolean with_state) 
		{
			return this;
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
			return "GT";
		}
	}

	protected static abstract class SlidableArithmeticFunction implements SlidableFunction
	{
		protected float m_currentValue;

		protected SlidableFunctionQueryable m_queryable;

		public SlidableArithmeticFunction()
		{
			super();
			m_queryable = new SlidableFunctionQueryable(toString());
		}

		@Override
		public int getInputArity() 
		{
			return 1;
		}

		@Override
		public int getOutputArity() 
		{
			return 1;
		}

		@Override
		public Class<?> getInputType(int index)
		{
			if (index == 0) 
			{
				return Number.class;
			}
			return null;
		}

		@Override
		public Class<?> getOutputType(int index)
		{
			return Number.class;
		}

		@Override
		public SlidableFunctionQueryable evaluate(Object[] inputs, Object[] outputs) 
		{
			return evaluate(inputs, outputs,  null);
		}

		@Override
		public SlidableFunctionQueryable devaluate(Object[] inputs, Object[] outputs) 
		{
			return devaluate(inputs, outputs,  null);
		}

		@Override
		public Function duplicate()
		{
			return duplicate(false);
		}

		@Override
		public void reset()
		{
			m_currentValue = 0;
			m_queryable.reset();
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object read(ObjectReader<?> reader, Object o) throws ReadException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Sum extends SlidableArithmeticFunction
	{
		public Sum()
		{
			super();
		}

		@Override
		public SlidableFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context context) 
		{
			m_currentValue += ((Number) inputs[0]).floatValue();
			outputs[0] = m_currentValue;
			m_queryable.addCallToEvaluate();
			return m_queryable;
		}

		@Override
		public SlidableFunctionQueryable devaluate(Object[] inputs, Object[] outputs, Context context) 
		{
			m_currentValue -= ((Number) inputs[0]).floatValue();
			outputs[0] = m_currentValue;
			m_queryable.addCallToDevaluate();
			return m_queryable;
		}

		@Override
		public Sum duplicate(boolean with_state) 
		{
			Sum s = new Sum();
			if (with_state)
			{
				s.m_currentValue = m_currentValue;
			}
			return s;
		}
	}

	public static class Average extends SlidableArithmeticFunction
	{
		protected int m_numValues;

		public Average()
		{
			super();
			m_numValues = 0;
		}

		@Override
		public SlidableFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context context) 
		{
			m_currentValue += ((Number) inputs[0]).floatValue();
			m_numValues++;
			outputs[0] = m_currentValue / m_numValues;
			m_queryable.addCallToEvaluate();
			return m_queryable;

		}

		@Override
		public SlidableFunctionQueryable devaluate(Object[] inputs, Object[] outputs, Context context) 
		{
			m_currentValue -= ((Number) inputs[0]).floatValue();
			m_numValues--;
			if (m_numValues > 0)
			{
				outputs[0] = m_currentValue / m_numValues;
			}
			else
			{
				outputs[0] = 0;
			}
			m_queryable.addCallToDevaluate();
			return m_queryable;
		}

		@Override
		public Average duplicate(boolean with_state) 
		{
			Average s = new Average();
			if (with_state)
			{
				s.m_currentValue = m_currentValue;
				s.m_numValues = m_numValues;
			}
			return s;
		}

		@Override
		public void reset()
		{
			super.reset();
			m_currentValue = 0;
			m_numValues = 0;
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object read(ObjectReader<?> reader, Object o) throws ReadException {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
