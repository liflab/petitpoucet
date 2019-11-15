package ca.uqac.lif.petitpoucet.functions.lists;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;

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
		List<List<Object>> out_list = new ArrayList<List<Object>>();
		int[] sizes = new int[inputs.length];
		List<?>[] lists = new List[inputs.length]; 
		for (int i = 0; i < inputs.length; i++)
		{
			lists[i] = (List<?>) inputs[i];
			sizes[i] = lists[i].size();
		}
		MultiIterator mit = new MultiIterator(sizes);
		while (mit.hasNext())
		{
			List<Integer> indices = mit.next();
			List<Object> out_elem = new ArrayList<Object>(inputs.length);
			for (int i = 0; i < indices.size(); i++)
			{
				out_elem.add(lists[i].get(indices.get(i)));
			}
			out_list.add(out_elem);
		}
		outputs[0] = out_list;
		if (track)
		{
			return new ProductQueryable(m_inArity, sizes);
		}
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
	
	protected static class ProductQueryable extends FunctionQueryable
	{
		protected int[] m_sizes;
		
		public ProductQueryable(int in_arity, int ... sizes)
		{
			super("Product", in_arity, 1);
			m_sizes = sizes;
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			Designator t_head = tail.peek();
			Designator t_tail = tail.tail();
			int total_elems = 1;
			for (int s : m_sizes)
			{
				total_elems *= s;
			}
			if (!(t_head instanceof NthElement))
			{
				return super.queryOutput(q, out_index, t_tail, root, factory);
			}
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			int elem_index = ((NthElement) t_head).getIndex();
			if (elem_index < 0 || elem_index > total_elems)
			{
				TraceabilityNode n = factory.getUnknownNode();
				root.addChild(n, Quality.NONE);
				leaves.add(n);
			}
			else
			{
				List<Integer> indices = getIndices(elem_index, m_sizes);
				Designator t_t_head = t_tail.peek();
				Designator t_t_tail = t_tail.tail();
				if (t_t_head instanceof NthElement)
				{
					int in_elem_index = ((NthElement) t_t_head).getIndex();
					if (in_elem_index < 0 || in_elem_index > indices.size())
					{
						TraceabilityNode n = factory.getUnknownNode();
						root.addChild(n, Quality.NONE);
						leaves.add(n);
					}
					else
					{
						int index = indices.get(in_elem_index);
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(t_t_tail, NthElement.get(index), NthInput.get(in_elem_index)), this);
						root.addChild(n, Quality.EXACT);
						leaves.add(n);
					}
				}
				else
				{
					TraceabilityNode and = factory.getAndNode();
					root.addChild(and, Quality.EXACT);
					for (int in_elem_index = 0; in_elem_index < indices.size(); in_elem_index++)
					{
						int index = indices.get(in_elem_index);
						TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(t_t_tail, NthElement.get(index), NthInput.get(in_elem_index)), this);
						and.addChild(n, Quality.EXACT);
						leaves.add(n);
					}
				}
			}
			return leaves;
		}
	}
	
	protected static List<Integer> getIndices(int elem_index, int ... sizes)
	{
		// TODO: there is a better way than replaying the iterator
		// up to the desired position
		MultiIterator mit = new MultiIterator(sizes);
		List<Integer> indices = null;
		for (int i = 0; i < elem_index; i++)
		{
			indices = mit.next();
		}
		return indices;
	}
	
	public static class MultiIterator implements Iterator<List<Integer>>
	{
		protected int[] m_sizes;
		
		protected int[] m_indices;
		
		protected boolean m_done;
		
		protected boolean m_hasNextCalled;
		
		public MultiIterator(int ... sizes)
		{
			super();
			m_sizes = sizes;
			m_indices = new int[sizes.length];
			for (int i = 0; i < sizes.length; i++)
			{
				m_indices[i] = 0;
			}
			m_done = false;
			m_hasNextCalled = false;
		}
		
		@Override
		public boolean hasNext()
		{
			if (m_done)
			{
				return false;
			}
			if (m_hasNextCalled)
			{
				return true;
			}
			m_hasNextCalled = true;
			boolean one_more = false;
			for (int i = 0; i < m_indices.length; i++)
			{
				m_indices[i]++;
				if (m_indices[i] == m_sizes[i])
				{
					m_indices[i] = 0;
				}
				else
				{
					one_more = true;
				}
			}
			if (!one_more)
			{
				m_done = true;
				return false;
			}
			return true;
		}

		@Override
		public List<Integer> next()
		{
			if (!m_hasNextCalled)
			{
				if (!hasNext())
				{
					throw new NoSuchElementException();
				}
			}
			List<Integer> out = new ArrayList<Integer>(m_indices.length);
			for (int i = 0; i < m_indices.length; i++)
			{
				out.add(m_indices[i]);
			}
			m_hasNextCalled = false;
			return out;
		}		
	}
}
