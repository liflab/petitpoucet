package ca.uqac.lif.petitpoucet.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.common.Contextualizable;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.DownstreamQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.UpstreamQuery;
import ca.uqac.lif.petitpoucet.ObjectNode;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.StateDuplicable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityTree;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.Trackable;
import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.circuit.CircuitElement;
import ca.uqac.lif.petitpoucet.circuit.CircuitQueryable;
import ca.uqac.lif.petitpoucet.circuit.CircuitQueryable.QueryableCircuitConnection;
import ca.uqac.lif.petitpoucet.functions.CircuitFunction.CircuitFunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.FunctionConnector.FunctionConnection;

public class GroupFunction implements Contextualizable, Function, Trackable 
{
	/**
	 * The set of functions contained in this composed function
	 */
	/*@ non_null @*/ protected Set<CircuitFunction> m_innerFunctions;

	/*@ non_null @*/ protected CircuitFunctionPlaceholder[] m_inputPlaceholders;

	/*@ non_null @*/ protected CircuitFunctionPlaceholder[] m_outputPlaceholders;

	/*@ non_null @*/ protected Context m_context;

	protected GroupFunctionQueryable m_queryable;
	
	protected String m_name;

	/**
	 * Creates a new composed function
	 * @param in_arity The function's input arity
	 * @param out_arity The function's output arity
	 */
	public GroupFunction(int in_arity, int out_arity)
	{
		super();
		m_innerFunctions = new HashSet<CircuitFunction>();
		m_inputPlaceholders = new CircuitFunctionPlaceholder[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_inputPlaceholders[i] = new CircuitFunctionPlaceholder(i);
		}
		m_outputPlaceholders = new CircuitFunctionPlaceholder[out_arity];
		for (int i = 0; i < out_arity; i++)
		{
			m_outputPlaceholders[i] = new CircuitFunctionPlaceholder(i);
		}
		m_context = new Context();
		m_queryable = new GroupFunctionQueryable(toString(), in_arity, out_arity);
		m_name = super.toString();
	}
	
	public GroupFunction setName(String name)
	{
		m_name = name;
		m_queryable.m_reference = name;
		return this;
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}

	/**
	 * Adds functions to the group
	 * @param functions The functions to add
	 */
	public void add(CircuitFunction... functions)
	{
		for (CircuitFunction f : functions)
		{
			m_innerFunctions.add(f);
			m_queryable.add(f.getQueryable());
		}
	}

	public void connect(CircuitFunction f, int i, CircuitFunction g, int j)
	{
		add(f, g);
		FunctionConnector.connect(f, i, g, j);
		m_queryable.connect(f.getQueryable(), i, g.getQueryable(), j);
	}

	public void associateInput(int i, CircuitFunction cf, int j)
	{
		m_inputPlaceholders[i].setToOutput(0, new FunctionConnection(cf, j));
		cf.setToInput(j, new GroupConnection(m_inputPlaceholders[i], i));
		m_queryable.associateInput(i, (CircuitQueryable) cf.getQueryable(), j);
	}

	public void associateOutput(int i, CircuitFunction cf, int j)
	{
		m_outputPlaceholders[i].setToInput(0, new FunctionConnection(cf, j));
		cf.setToOutput(j, new GroupConnection(m_outputPlaceholders[i], i));
		m_queryable.associateOutput(i, (CircuitQueryable) cf.getQueryable(), j);
	}


	static class GroupConnection implements CircuitConnection
	{
		protected int m_index;

		protected CircuitElement m_function;

		public GroupConnection(CircuitElement f, int index)
		{
			super();
			m_index = index;
			m_function = f;
		}

		@Override
		public int getIndex()
		{
			return m_index;
		}

		@Override
		public CircuitElement getObject() 
		{
			return m_function;
		}
	}

	@Override
	public GroupFunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		for (int i = 0; i < m_inputPlaceholders.length; i++)
		{
			m_inputPlaceholders[i].setValue(inputs[i]);
		}
		for (int i = 0; i < m_outputPlaceholders.length; i++)
		{
			outputs[i] = m_outputPlaceholders[i].getOutput(c);
		}
		return m_queryable;
	}

	@Override
	public void reset()
	{
		for (CircuitFunctionPlaceholder p : m_inputPlaceholders)
		{
			p.reset();
		}
		for (CircuitFunctionPlaceholder p : m_outputPlaceholders)
		{
			p.reset();
		}
		for (Function f : m_innerFunctions)
		{
			f.reset();
		}
		m_queryable.reset();
	}

	protected class CircuitFunctionPlaceholder implements CircuitElement, Outputable
	{
		protected Object m_value;

		protected int m_index;

		protected FunctionConnection m_upstreamConnection;

		protected FunctionConnection m_downstreamConnection;

		protected boolean m_computed; 

		public CircuitFunctionPlaceholder(int index)
		{
			super();
			m_index = index;
			m_computed = false;
		}

		public void reset()
		{
			m_computed = false;
		}

		@Override
		public void setToInput(int index, CircuitConnection connection) 
		{
			m_upstreamConnection = (FunctionConnection) connection;
		}

		@Override
		public void setToOutput(int index, CircuitConnection connection) 
		{
			m_downstreamConnection = (FunctionConnection) connection;
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
		public FunctionConnection getInputConnection(int index)
		{
			return m_upstreamConnection;
		}

		@Override
		public FunctionConnection getOutputConnection(int index) 
		{
			return m_downstreamConnection;
		}

		public void setValue(Object o)
		{
			m_value = o;
			m_computed = true;
		}

		@Override
		public Object getOutput(Context c)
		{
			if (!m_computed)
			{
				if (m_upstreamConnection != null)
				{
					CircuitFunction cf = (CircuitFunction) m_upstreamConnection.getObject();
					m_value = cf.getOutput(m_upstreamConnection.getIndex(), c);
				}
			}
			return m_value;
		}

		@Override
		public Object getOutput(int index, Context c)
		{
			return getOutput(c);
		}
	}

	@Override
	public Map<String,Object> print(ObjectPrinter<?> printer) throws PrintException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupFunction read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupFunction duplicate(boolean with_state) 
	{
		GroupFunction gf = new GroupFunction(m_inputPlaceholders.length, m_outputPlaceholders.length);
		if (with_state)
		{
			gf.m_context.putAll(m_context);
		}
		Map<CircuitFunction,CircuitFunction> mapping = new HashMap<CircuitFunction,CircuitFunction>(m_innerFunctions.size());
		for (CircuitFunction cf : m_innerFunctions)
		{
			CircuitFunction cf_dup = cf.duplicate(with_state);
			mapping.put(cf, cf_dup);
			gf.add(cf_dup);
		}
		for (Map.Entry<CircuitFunction,CircuitFunction> e : mapping.entrySet())
		{
			CircuitFunction cf_src = e.getKey();
			CircuitFunction cf_dst = e.getValue();
			for (int i = 0; i < cf_src.m_inputConnections.length; i++)
			{
				CircuitConnection cc = cf_src.m_inputConnections[i];
				CircuitElement ce = cc.getObject();
				if (ce instanceof CircuitFunctionPlaceholder)
				{
					gf.associateInput(cc.getIndex(), cf_dst, i);
				}
				else
				{
					CircuitFunction cf_up = (CircuitFunction) ce;
					CircuitFunction cf_dst_up = mapping.get(cf_up);
					gf.connect(cf_dst_up, cc.getIndex(), cf_dst, i);
					//cf_dst.setToInput(i, new GroupConnection(cf_dst_up, cc.getIndex()));
					//cf_dst_up.setToOutput(cc.getIndex(), new GroupConnection(cf_dst, i));
				}
			}
		}
		for (int i = 0; i < m_outputPlaceholders.length; i++)
		{
			CircuitFunctionPlaceholder p = m_outputPlaceholders[i];
			CircuitFunction cf = mapping.get(p.m_upstreamConnection.getObject());
			gf.associateOutput(i, cf, p.m_index);
		}
		//gf.m_queryable = m_queryable.duplicate(with_state);
		return gf;
	}

	@Override
	public GroupFunction duplicate() 
	{
		return duplicate(false);
	}

	@Override
	public GroupFunctionQueryable getQueryable() 
	{
		return m_queryable;
	}

	@Override
	public GroupFunctionQueryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return evaluate(inputs, outputs, m_context);
	}

	@Override
	public Class<?> getInputType(int index)
	{
		try
		{
			CircuitFunctionPlaceholder ip = m_inputPlaceholders[index];
			if (ip.m_downstreamConnection == null)
			{
				return null;
			}
			CircuitFunction cf = (CircuitFunction) ip.m_downstreamConnection.getObject();
			if (cf == null)
			{
				return null;
			}
			return cf.getInputType(ip.m_downstreamConnection.getIndex());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new FunctionException(e);
		}
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		try
		{
			CircuitFunctionPlaceholder ip = m_outputPlaceholders[index];
			if (ip.m_upstreamConnection == null)
			{
				return null;
			}
			CircuitFunction cf = (CircuitFunction) ip.m_upstreamConnection.getObject();
			if (cf == null)
			{
				return null;
			}
			return cf.getOutputType(ip.m_upstreamConnection.getIndex());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new FunctionException(e);
		}
	}

	@Override
	public Object getContext(String key)
	{
		return m_context.get(key);
	}

	@Override
	public void setContext(String key, Object value) 
	{
		m_context.put(key, value);
		for (CircuitFunction cf : m_innerFunctions)
		{
			cf.setContext(key, value);
		}
	}

	@Override
	public int getInputArity() 
	{
		return m_inputPlaceholders.length;
	}

	@Override
	public int getOutputArity() 
	{
		return m_outputPlaceholders.length;
	}

	public static class GroupFunctionQueryable implements Queryable, Printable, Readable, StateDuplicable<GroupFunctionQueryable>
	{
		/**
		 * The set of functions contained in this composed function
		 */
		/*@ non_null @*/ protected Set<CircuitFunctionQueryable> m_innerQueryables;

		/*@ non_null @*/ protected QueryablePlaceholder[] m_inputConnections;

		/*@ non_null @*/ protected QueryablePlaceholder[] m_outputConnections;
		
		protected String m_reference;

		public GroupFunctionQueryable(String reference, int in_arity, int out_arity)
		{
			super();
			m_reference = reference;
			m_innerQueryables = new HashSet<CircuitFunctionQueryable>();
			m_inputConnections = new QueryablePlaceholder[in_arity];
			for (int i = 0; i < in_arity; i++)
			{
				m_inputConnections[i] = new QueryablePlaceholder(i);
			}
			m_outputConnections = new QueryablePlaceholder[out_arity];
			for (int i = 0; i < out_arity; i++)
			{
				m_outputConnections[i] = new QueryablePlaceholder(i);
			}
			m_reference = reference;
		}
		
		@Override
		public String toString()
		{
			return m_reference;
		}
		
		public void reset()
		{
			for (CircuitFunctionQueryable cq : m_innerQueryables)
			{
				cq.reset();
			}
		}
		
		public void add(CircuitFunctionQueryable q)
		{
			m_innerQueryables.add(q);
		}

		public void connect(CircuitFunctionQueryable q1, int i, CircuitFunctionQueryable q2, int j)
		{
			m_innerQueryables.add(q1);
			m_innerQueryables.add(q2);
			q1.setToOutput(i, new QueryableCircuitConnection(j, q2));
			q2.setToInput(j, new QueryableCircuitConnection(i, q1));
		}
		
		public void associateInput(int i, CircuitQueryable cq, int j)
		{
			m_inputConnections[i].m_downstreamConnection = new QueryableCircuitConnection(j, cq);
			cq.setToInput(j, m_inputConnections[i]);
		}
		
		public void associateOutput(int i, CircuitQueryable cq, int j)
		{
			m_outputConnections[i].m_upstreamConnection = new QueryableCircuitConnection(j, cq);
			cq.setToOutput(j, m_outputConnections[i]);
		}

		@Override
		public GroupFunctionQueryable duplicate() 
		{
			return duplicate(false);
		}

		@Override
		public GroupFunctionQueryable duplicate(boolean with_state)
		{
			GroupFunctionQueryable gfq = new GroupFunctionQueryable(m_reference, m_inputConnections.length, m_outputConnections.length);
			Map<CircuitFunctionQueryable,CircuitFunctionQueryable> mapping = new HashMap<CircuitFunctionQueryable,CircuitFunctionQueryable>(m_innerQueryables.size());
			for (CircuitFunctionQueryable q : m_innerQueryables)
			{
				CircuitFunctionQueryable q_dup = (CircuitFunctionQueryable) ((StateDuplicable<CircuitFunctionQueryable>) q).duplicate(with_state);
				mapping.put(q, q_dup);
				gfq.m_innerQueryables.add(q_dup);
			}
			for (Map.Entry<CircuitFunctionQueryable,CircuitFunctionQueryable> e : mapping.entrySet())
			{
				CircuitFunctionQueryable cf_src = e.getKey();
				CircuitFunctionQueryable cf_dst = e.getValue();
				for (int i = 0; i < cf_src.getInputArity(); i++)
				{
					CircuitConnection cc = cf_src.getInputConnection(i);
					if (cc instanceof QueryablePlaceholder)
					{
						gfq.associateInput(cc.getIndex(), cf_dst, i);
					}
					else
					{
						CircuitElement ce = cc.getObject();
						CircuitFunctionQueryable cf_up = (CircuitFunctionQueryable) ce;
						CircuitFunctionQueryable cf_dst_up = mapping.get(cf_up);
						gfq.connect(cf_dst_up, cc.getIndex(), cf_dst, i);
						//cf_dst.setToInput(i, new QueryableCircuitConnection(cc.getIndex(), cf_dst_up));
						//cf_dst_up.setToOutput(cc.getIndex(), new QueryableCircuitConnection(i, cf_dst));
					}
				}
			}
			for (int i = 0; i < m_outputConnections.length; i++)
			{
				QueryablePlaceholder p = m_outputConnections[i];
				CircuitQueryable cf = mapping.get(p.m_upstreamConnection.getObject());
				gfq.associateOutput(i, cf, p.m_index);
			}
			return gfq;
		}

		@Override
		public Object print(ObjectPrinter<?> printer) throws PrintException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GroupFunctionQueryable read(ObjectReader<?> reader, Object o) throws ReadException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public List<TraceabilityNode> query(TraceabilityQuery q, 
				Designator d, TraceabilityNode root, Tracer factory)
		{
			if (q instanceof UpstreamQuery && d.peek() instanceof NthOutput)
			{
				return queryOutput(q, ((NthOutput) d.peek()).getIndex(), d.tail(), root, factory);
			}
			if (q instanceof DownstreamQuery && d.peek() instanceof NthInput)
			{
				return queryInput(q, ((NthInput) d.peek()).getIndex(), d.tail(), root, factory);
			}
			return new ArrayList<TraceabilityNode>(0);//return factory.unknownLink(root);
		}

		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			// Send traceability sub-query to inner queryables
			Tracer sub_tracer = factory.getSubTracer(toString());
			CircuitConnection cc = m_outputConnections[out_index].m_upstreamConnection;
			ComposedDesignator cd = new ComposedDesignator(tail, NthOutput.get(cc.getIndex()));
			Queryable fq = (Queryable) cc.getObject();
			TraceabilityTree tree = sub_tracer.trace(q, cd, fq);
			root.addChild(tree.getRoot(), Quality.EXACT);
			List<TraceabilityNode> leaves = tree.getLeaves();
			// Once done, reconnect "input" leaves to the input of the group they are
			// connected to
			List<TraceabilityNode> new_leaves = new ArrayList<TraceabilityNode>();
			for (TraceabilityNode tn : leaves)
			{
				if (tn instanceof ObjectNode)
				{
					ObjectNode o_tn = (ObjectNode) tn;
					DesignatedObject dob = o_tn.getDesignatedObject();
					Designator d_head = dob.getDesignator().peek();
					if (d_head instanceof NthInput)
					{
						int input_nb = ((NthInput) d_head).getIndex();
						CircuitQueryable cq = (CircuitQueryable) dob.getObject();
						CircuitConnection c_con = cq.getInputConnection(input_nb);
						ComposedDesignator leaf_cd = new ComposedDesignator(dob.getDesignator().tail(), NthInput.get(c_con.getIndex()));
						TraceabilityNode new_leaf = factory.getObjectNode(leaf_cd, this);
						tn.addChild(new_leaf, Quality.EXACT);
						new_leaves.add(new_leaf);
					}
				}
			}
			return new_leaves;
		}
		
		protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int in_index, Designator tail, TraceabilityNode root, Tracer factory)
		{
			// Send traceability sub-query to inner queryables
			Tracer sub_tracer = factory.getSubTracer(toString());
			CircuitConnection cc = m_inputConnections[in_index].m_downstreamConnection;
			ComposedDesignator cd = new ComposedDesignator(tail, NthInput.get(cc.getIndex()));
			Queryable fq = (Queryable) cc.getObject();
			TraceabilityTree tree = sub_tracer.trace(q, cd, fq);
			root.addChild(tree.getRoot(), Quality.EXACT);
			List<TraceabilityNode> leaves = tree.getLeaves();
			// Once done, reconnect "output" leaves to the output of the group they are
			// connected to
			List<TraceabilityNode> new_leaves = new ArrayList<TraceabilityNode>();
			for (TraceabilityNode tn : leaves)
			{
				if (tn instanceof ObjectNode)
				{
					ObjectNode o_tn = (ObjectNode) tn;
					DesignatedObject dob = o_tn.getDesignatedObject();
					Designator d_head = dob.getDesignator().peek();
					if (d_head instanceof NthOutput)
					{
						int input_nb = ((NthOutput) d_head).getIndex();
						CircuitQueryable cq = (CircuitQueryable) dob.getObject();
						CircuitConnection c_con = cq.getOutputConnection(input_nb);
						ComposedDesignator leaf_cd = new ComposedDesignator(dob.getDesignator().tail(), NthOutput.get(c_con.getIndex()));
						TraceabilityNode new_leaf = factory.getObjectNode(leaf_cd, this);
						tn.addChild(new_leaf, Quality.EXACT);
						new_leaves.add(new_leaf);
					}
				}
			}
			return new_leaves;
		}
		
		protected static class QueryablePlaceholder extends QueryableCircuitConnection
		{
			protected CircuitConnection m_upstreamConnection;

			protected CircuitConnection m_downstreamConnection;

			protected int m_index;

			public QueryablePlaceholder(int index)
			{
				super(index, null);
				m_index = index;
			}

			public void setToInput(int index, CircuitConnection connection) 
			{
				m_upstreamConnection = connection;
			}

			//@Override
			public void setToOutput(int index, CircuitConnection connection) 
			{
				m_downstreamConnection = connection;
			}

			public CircuitConnection getInputConnection(int index) 
			{
				return m_upstreamConnection;
			}

			public CircuitConnection getOutputConnection(int index) 
			{
				return m_downstreamConnection;
			}

		}
	}
}
