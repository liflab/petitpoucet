package ca.uqac.lif.petitpoucet.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;

public class UnknownQueryable extends FunctionQueryable 
{
	public UnknownQueryable(String reference, int in_arity, int out_arity) 
	{
		super(reference, in_arity, out_arity);
	}

	@Override
	protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory)
	{
		return factory.unknownLink(root);
	}
	
	@Override
	protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int input_nb, Designator d,
			TraceabilityNode root, Tracer factory)
	{
		return factory.unknownLink(root);
	}
	
	@Override
	public UnknownQueryable duplicate(boolean with_state)
	{
		return new UnknownQueryable(m_reference, getInputArity(), getOutputArity());
	}
}
