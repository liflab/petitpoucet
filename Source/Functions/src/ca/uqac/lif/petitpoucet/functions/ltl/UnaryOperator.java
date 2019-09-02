package ca.uqac.lif.petitpoucet.functions.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

public abstract class UnaryOperator extends NaryFunction
{
	protected List<Integer> m_positions;
	
	protected boolean m_startValue;
	
	public UnaryOperator(boolean start_value)
	{
		super(1);
		m_positions = new ArrayList<Integer>();
		m_startValue = start_value;
	}
	
	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		m_inputs = inputs;
		m_positions.clear();
		List<?> list = (List<?>) inputs[0];
		boolean value = m_startValue;
		int pos = 0;
		for (Object o : list)
		{
			boolean b = false;
			if (o instanceof Boolean)
			{
				b = (Boolean) o;
			}
			if (b == !m_startValue)
			{
				value = !m_startValue;
				m_positions.add(pos);
			}
			pos++;
		}
		outputs[0] = value;
		m_returnedValue[0] = value;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		if (!(q instanceof CausalityQuery))
		{
			answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.EXACT);
		}
		if (m_positions.isEmpty())
		{
			answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.EXACT);
		}
		TraceabilityNode or = factory.getOrNode();
		for (int p : m_positions)
		{
			ComposedDesignator cd = new ComposedDesignator(new NthElement(p), new NthInput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			or.addChild(child, Quality.EXACT);
			leaves.add(child);
		}
		root.addChild(or, Quality.EXACT);
	}
}
