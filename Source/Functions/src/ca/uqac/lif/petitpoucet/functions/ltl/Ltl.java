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
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

public class Ltl 
{
	public static final transient Globally globally = new Globally();
	
	public static final transient Eventually eventually = new Eventually();
	
	public static final transient Next next = new Next();
	
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
		public NextQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
		{
			List<Boolean> in_list = (List<Boolean>) inputs[0];
			int size = in_list.size();
			List<Boolean> out_list = new ArrayList<Boolean>(size - 1);
			for (int i = 1; i < size; i++)
			{
				out_list.add(in_list.get(i));
			}
			outputs[0] = out_list;
			NextQueryable nq = s_queryablePool.get(size);
			if (nq == null)
			{
				nq = new NextQueryable(size);
				s_queryablePool.put(size, nq);
			}
			return nq;
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
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(new NthElement(index + 1), NthInput.get(0)), this);
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
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(new NthElement(index - 1), NthOutput.get(0)), this);
						root.addChild(n, Quality.EXACT);
						list.add(n);
					}
				}
				return list;
			}
		}
	}
}