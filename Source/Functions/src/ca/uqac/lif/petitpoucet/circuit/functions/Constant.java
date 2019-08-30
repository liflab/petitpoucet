/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit.functions;

import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
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
	public String toString()
	{
	  if (m_value != null)
	  {
	    return "Constant " +  m_value.toString();
	  }
	  return "null";
	}

	@Override
	public void answerQuery(TraceabilityQuery q, int output_nb, Designator d, List<List<DesignatorLink>> links) 
	{
		ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new Parameter("Parameter 'value' in constructor", d), this);
		ConcreteDesignatorLink dl = new ConcreteDesignatorLink(Quality.EXACT, dob);
		links.add(putIntoList(dl));
	}
}
