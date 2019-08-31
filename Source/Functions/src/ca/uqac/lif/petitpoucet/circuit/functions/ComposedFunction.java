package ca.uqac.lif.petitpoucet.circuit.functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;

public class ComposedFunction extends Function
{
	/**
	 * The set of functions contained in this composed function
	 */
	protected Set<Function> m_innerFunctions;

	protected InputPlaceholder[] m_inputPlaceholders;

	protected OutputPlaceholder[] m_outputPlaceholders;

	/**
	 * An optional textual name for the function
	 */
	protected String m_name = null;

	public ComposedFunction(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
		m_innerFunctions = new HashSet<Function>();
		m_inputPlaceholders = new InputPlaceholder[m_inArity];
		for (int i = 0; i < in_arity; i++)
		{
			m_inputPlaceholders[i] = new InputPlaceholder(i);
		}
		m_outputPlaceholders = new OutputPlaceholder[m_outArity];
		for (int i = 0; i < in_arity; i++)
		{
			m_outputPlaceholders[i] = new OutputPlaceholder(i);
		}
	}

	/**
	 * Set a textual name for the function
	 * @param name The name
	 * @return This function
	 */
	public ComposedFunction setName(String name)
	{
		m_name = name;
		return this;
	}

	public void add(Function ... functions)
	{
		for (Function f : functions)
		{
			m_innerFunctions.add(f);
		}
	}

	public void associateInput(int i, Function f, int j)
	{
		m_inputPlaceholders[i].setToOutput(0, new FunctionConnection(j, f));
		f.setToInput(j, new FunctionConnection(0, m_inputPlaceholders[i]));
	}

	public void associateOutput(int i, Function f, int j)
	{
		m_outputPlaceholders[i].setToInput(0, new FunctionConnection(j, f));
		f.setToOutput(j, new FunctionConnection(0, m_outputPlaceholders[i]));
	}

	@Override
	public List<TraceabilityNode> query(TraceabilityQuery q, Designator d,
			TraceabilityNode root, NodeFactory factory) 
	{
		List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
		Designator top = d.peek();
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		if (!(top instanceof NthInput) && !(top instanceof NthOutput))
		{
			// Can't answer queries that are not about inputs or outputs
			leaves.add(factory.getUnknownNode());
		}
		if (top instanceof NthInput)
		{
			NthInput ni = (NthInput) top;
			FunctionConnection conn = m_inputConnections[ni.getIndex()];
			if (conn != null)
			{
				ComposedDesignator new_cd = new ComposedDesignator(tail, new NthOutput(conn.getIndex()));
				ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new_cd, conn.getObject());
				TraceabilityNode tn = factory.getObjectNode(dob);
				leaves.add(tn);
				root.addChild(tn, Quality.EXACT);
			}
		}
		else if (top instanceof NthOutput)
		{
			NthOutput no = (NthOutput) top;
			ComposedDesignator new_cd = new ComposedDesignator(tail, new NthOutput(0));
			ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new_cd, m_outputPlaceholders[no.getIndex()]);
			TraceabilityNode tn = factory.getObjectNode(dob);
			leaves.add(tn);
			root.addChild(tn, Quality.EXACT);
		}
		return leaves;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		// Do nothing
	}

	@Override
	public String toString()
	{
		if (m_name == null)
		{
			return super.toString();
		}
		return m_name;
	}

	public abstract class Placeholder extends Function
	{
		protected int m_index;

		public Placeholder(int index)
		{
			super(1, 1);
			m_index = index;
		}

		@Override
		public void getValue(Object[] inputs, Object[] outputs)
		{
			outputs[0] = inputs[0];
		}
	}

	public class InputPlaceholder extends Placeholder
	{
		public InputPlaceholder(int index)
		{
			super(index);
		}

		@Override
		public List<TraceabilityNode> query(TraceabilityQuery q, Designator d,
				TraceabilityNode root, NodeFactory factory) 
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			Designator top = d.peek();
			Designator tail = d.tail();
			if (tail == null)
			{
				tail = Designator.identity;
			}
			if (top instanceof NthInput)
			{
				ComposedDesignator cd = new ComposedDesignator(tail, new NthInput(m_index));
				TraceabilityNode child = factory.getObjectNode(cd, ComposedFunction.this);
				leaves.add(child);
				root.addChild(child, Quality.EXACT);
			}
			else if (top instanceof NthOutput)
			{
				ComposedDesignator cd = new ComposedDesignator(tail, new NthInput(m_index));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				leaves.add(child);
				root.addChild(child, Quality.EXACT);
			}
			return leaves;
		}

		@Override
		public Object[] evaluate()
		{
			if (m_evaluated)
			{
				return m_returnedValue;
			}
			FunctionConnection conn = ComposedFunction.this.m_inputConnections[m_index];
			m_inputs[0] = conn.pullValue();
			getValue(m_inputs, m_returnedValue);
			return m_returnedValue;
		}

		@Override
		public String toString()
		{
			return "Input placeholder " + m_index + " of " + ComposedFunction.this.toString();
		}
	}

	public class OutputPlaceholder extends Placeholder
	{
		public OutputPlaceholder(int index)
		{
			super(index);
		}

		@Override
		public List<TraceabilityNode> query(TraceabilityQuery q, Designator d,
				TraceabilityNode root, NodeFactory factory) 
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			Designator top = d.peek();
			Designator tail = d.tail();
			if (tail == null)
			{
				tail = Designator.identity;
			}
			if (top instanceof NthInput)
			{
				FunctionConnection fc = m_inputConnections[0];
				Function f = fc.getObject();
				ComposedDesignator cd = new ComposedDesignator(tail, new NthOutput(fc.getIndex()));
				TraceabilityNode child = factory.getObjectNode(cd, f);
				leaves.add(child);
				root.addChild(child, Quality.EXACT);
			}
			else if (top instanceof NthOutput)
			{
				ComposedDesignator cd = new ComposedDesignator(tail, new NthInput(m_index));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				leaves.add(child);
				root.addChild(child, Quality.EXACT);
			}
			return leaves;
		}

		@Override
		public Object[] evaluate()
		{
			if (m_evaluated)
			{
				return m_returnedValue;
			}
			FunctionConnection conn = m_inputConnections[0];
			m_inputs[0] = conn.pullValue();
			getValue(m_inputs, m_returnedValue);
			return m_returnedValue;
		}

		@Override
		public String toString()
		{
			return "Output placeholder " + m_index + " of " + ComposedFunction.this.toString();
		}
	}

	@Override
	public Object[] evaluate()
	{
		Object[] outputs = new Object[m_outArity];
		if (m_evaluated)
		{
			return m_returnedValue;
		}
		for (int j = 0; j < m_outArity; j++)
		{
			Object[] obs = m_outputPlaceholders[j].evaluate(); 
			outputs[j] = obs[0];
		}
		return outputs;
	}
}
