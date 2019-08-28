package examples.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.Parameter;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

public class Constant extends CircuitFunction
{
	/**
	 * The value of this constant object
	 */
	protected Object m_value;
	
	/**
	 * Creates a new constant
	 * @param value The value of this constant object
	 */
	public Constant(Object value)
	{
		super(0, 1);
		m_value = value;
	}
	
	/**
	 * Sets the value of this constant object
	 * @param value The value
	 */
	public void setValue(Object value)
	{
		m_value = value;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs) 
	{
		outputs[0] = m_value;
	}

	@Override
	public void provenanceQuery(NthOutput d, List<DesignatorLink> links) 
	{
		ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new Parameter("value", d), this);
		ConcreteDesignatorLink dl = new ConcreteDesignatorLink(dob, Quality.EXACT);
		links.add(dl);
	}

	@Override
	public void causalityQuery(NthOutput d, List<DesignatorLink> links) 
	{
		// Same as provenance
		provenanceQuery(d, links);
	}
}
