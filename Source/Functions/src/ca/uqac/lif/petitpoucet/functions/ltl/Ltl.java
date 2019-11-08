package ca.uqac.lif.petitpoucet.functions.ltl;

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
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;
import ca.uqac.lif.petitpoucet.functions.BinaryFunction.BinaryFunctionQueryable.Inputs;

public class Ltl 
{
	public static final transient Globally globally = new Globally();

	public static final transient Eventually eventually = new Eventually();

	public static final transient Next next = new Next();

	public static final transient And and = new And();

	private Ltl()
	{
		super();
	}

	@SuppressWarnings("rawtypes")
	public static class And extends BinaryFunction<List,List,List>
	{
		protected static BinaryFunctionQueryable s_queryableBoth = new BinaryFunctionQueryable("Ltl.And", Inputs.BOTH);

		protected static BinaryFunctionQueryable s_queryableAny = new BinaryFunctionQueryable("Ltl.And", Inputs.ANY);

		protected static BinaryFunctionQueryable s_queryableLeft = new BinaryFunctionQueryable("Ltl.And", Inputs.LEFT);

		protected static BinaryFunctionQueryable s_queryableRight = new BinaryFunctionQueryable("Ltl.And", Inputs.RIGHT);

		protected And()
		{
			super(List.class, List.class, List.class);
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
		public And duplicate(boolean with_state)
		{
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
		{
			List<Boolean> left = (List<Boolean>) inputs[0];
			List<Boolean> right = (List<Boolean>) inputs[1];
			int len = Math.max(left.size(), right.size());
			List<Boolean> out = new ArrayList<Boolean>(len);
			List<BinaryFunctionQueryable.Inputs> out_queryables = new ArrayList<BinaryFunctionQueryable.Inputs>(len);
			for (int i = 0; i < len; i++)
			{
				boolean b_left = left.get(i);
				boolean b_right = right.get(i);
				if (track)
				{
					if (b_left == false)
					{
						if (b_right == false)
						{
							out_queryables.add(BinaryFunctionQueryable.Inputs.ANY);
						}
						else
						{
							out_queryables.add(BinaryFunctionQueryable.Inputs.LEFT);
						}
					}
					else
					{
						if (b_right == false)
						{
							out_queryables.add(BinaryFunctionQueryable.Inputs.RIGHT);
						}
						else
						{
							out_queryables.add(BinaryFunctionQueryable.Inputs.BOTH);
						}
					}
				}
				out.add(b_left && b_right);
			}
			outputs[0] = out;
			if (track)
			{
				return new LtlBinaryConnective.LtlBinaryConnectiveQueryable("Ltl.And", out_queryables);
			}
			return null;
		}
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
		public Globally duplicate(boolean with_state) 
		{
			return this;
		}
	}

	public static class Eventually extends UnaryOperator
	{
		protected Eventually()
		{
			super(false);
		}

		@Override
		public String toString()
		{
			return "Eventually";
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException
		{
			return null;
		}

		@Override
		public Eventually read(ObjectReader<?> reader, Object o) throws ReadException 
		{
			return this;
		}

		@Override
		public Eventually duplicate(boolean with_state) 
		{
			return this;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Next extends UnaryFunction<List,List>
	{
		protected static final transient Map<Integer,NextQueryable> s_queryablePool = new LinkedHashMap<Integer,NextQueryable>();

		protected Next()
		{
			super(List.class, List.class);
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
		public Next duplicate(boolean with_state)
		{
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public NextQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
		{
			List<Boolean> in_list = (List<Boolean>) inputs[0];
			int size = in_list.size();
			List<Boolean> out_list = new ArrayList<Boolean>(size - 1);
			for (int i = 1; i < size; i++)
			{
				out_list.add(in_list.get(i));
			}
			outputs[0] = out_list;
			if (track)
			{
				NextQueryable nq = s_queryablePool.get(size);
				if (nq == null)
				{
					nq = new NextQueryable(size);
					s_queryablePool.put(size, nq);
				}
				return nq;
			}
			return null;
		}

		protected static class NextQueryable extends FunctionQueryable
		{
			protected int m_length;

			public NextQueryable(int length)
			{
				super("Next", 1, 1);
				m_length = length;
			}

			@Override
			protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
					TraceabilityNode root, Tracer factory)
			{
				List<TraceabilityNode> list = new ArrayList<TraceabilityNode>(1);
				Designator d_head = d.peek();
				if (!(d_head instanceof NthElement))
				{
					TraceabilityNode n = factory.getUnknownNode();
					root.addChild(n, Quality.NONE);
					list.add(n);
				}
				else
				{
					NthElement ne = (NthElement) d_head;
					int index = ne.getIndex();
					if (index < 0 || index >= m_length - 1)
					{
						TraceabilityNode n = factory.getUnknownNode();
						root.addChild(n, Quality.NONE);
						list.add(n);
					}
					else
					{
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(NthElement.get(index + 1), NthInput.get(0)), this);
						root.addChild(n, Quality.EXACT);
						list.add(n);
					}
				}
				return list;
			}

			@Override
			protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int output_nb, Designator d,
					TraceabilityNode root, Tracer factory)
			{
				List<TraceabilityNode> list = new ArrayList<TraceabilityNode>(1);
				Designator d_head = d.peek();
				if (!(d_head instanceof NthElement))
				{
					TraceabilityNode n = factory.getUnknownNode();
					root.addChild(n, Quality.NONE);
					list.add(n);
				}
				else
				{
					NthElement ne = (NthElement) d_head;
					int index = ne.getIndex();
					if (index <= 0 || index >= m_length)
					{
						TraceabilityNode n = factory.getUnknownNode();
						root.addChild(n, Quality.NONE);
						list.add(n);
					}
					else
					{
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(NthElement.get(index - 1), NthOutput.get(0)), this);
						root.addChild(n, Quality.EXACT);
						list.add(n);
					}
				}
				return list;
			}
		}
	}
}