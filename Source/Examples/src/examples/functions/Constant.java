package examples.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.common.Parameter;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

public class Constant extends CircuitFunction
{
	protected Object m_value;
	
	public Constant(Object value)
	{
		super(0, 1);
		m_value = value;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs) 
	{
		outputs[0] = m_value;
	}

	@Override
	public void provenanceQuery(Designator d, List<DesignatorLink> links) 
	{
		ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new Parameter("value", Designator.nothing), this);
		ConcreteDesignatorLink dl = new ConcreteDesignatorLink(dob, Quality.EXACT);
		links.add(dl);
	}

	@Override
	public void causalityQuery(Designator d, List<DesignatorLink> links) 
	{
		// Same as provenance
		provenanceQuery(d, links);
	}
}
