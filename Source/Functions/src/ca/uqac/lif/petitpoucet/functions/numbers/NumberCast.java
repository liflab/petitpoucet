package ca.uqac.lif.petitpoucet.functions.numbers;

import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

public class NumberCast extends NaryFunction
{
	public NumberCast()
	{
		super(1);
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.EXACT);
	}
	
	@Override
	public String toString()
	{
		return "Number cast";
	}
	
	@Override
	public void getValue(Object[] inputs, Object[] outputs)
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
		m_returnedValue[0] = num;
	}
}
