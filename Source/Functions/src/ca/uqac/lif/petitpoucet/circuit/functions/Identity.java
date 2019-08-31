package ca.uqac.lif.petitpoucet.circuit.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;

public class Identity extends SingleFunction
{
	public Identity(int arity)
	{
		super(arity, arity);
	}

	public Identity()
	{
		this(1);
	}

	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		ComposedDesignator cd = new ComposedDesignator(d, new NthInput(output_nb));
		TraceabilityNode child = factory.getObjectNode(cd, this);
		root.addChild(child, Quality.EXACT);
		leaves.add(child);
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		outputs[0] = inputs[0];
	}
}
