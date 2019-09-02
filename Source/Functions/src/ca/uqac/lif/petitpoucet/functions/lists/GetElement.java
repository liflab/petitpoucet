package ca.uqac.lif.petitpoucet.functions.lists;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

public class GetElement extends NaryFunction
{
	protected int m_index;

	public GetElement(int index)
	{
		super(1);
		m_index = index;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		m_evaluated = true;
		List<?> list = (List<?>) inputs[0];
		Object elem = list.get(m_index);
		m_returnedValue[0] = elem;
		outputs[0] = elem;
	}
	
	@Override
	public String toString()
	{
		return "Get #" + m_index;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		ComposedDesignator cd = new ComposedDesignator(tail, new NthElement(m_index), new NthInput(0));
		TraceabilityNode child = factory.getObjectNode(cd, this);
		root.addChild(child, Quality.EXACT);
		leaves.add(child);
	}
	
}
