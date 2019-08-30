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
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

public class Multiply extends NaryFunction
{
  public Multiply()
  {
    this(2);
  }

  public Multiply(int in_arity)
  {
    super(in_arity);
  }

  @Override
  public void getValue(Object[] inputs, Object[] outputs)
  {
    float out = 1;
    for (Object o : inputs)
    {
      if (o instanceof Number)
      {
        out *= ((Number) o).floatValue();
      }
    }
    outputs[0] = out;
  }
  
  @Override
  public String toString()
  {
    return "×";
  }

  @Override
  protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
      List<List<DesignatorLink>> links)
  {
    if (!(q instanceof CausalityQuery))
    {
      super.answerQuery(q, output_nb, d, links);
      return;
    }
    if (((Number) m_returnedValue[0]).floatValue() != 0f)
    {
      super.answerQuery(q, output_nb, d, links);
      return;
    }
    // Multiplication has a special definition of causality if the output is 0
    for (int i = 0; i < m_inArity; i++)
    {
      // Cause for output = 0 is any occurrence of 0 in inputs
      if (((Number) m_inputs[i]).floatValue() == 0f)
      {
        ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(new CircuitDesignator.NthInput(i), this);
        ConcreteDesignatorLink cdl = new ConcreteDesignatorLink(Quality.EXACT, cdo);
        links.add(putIntoList(cdl));
      }
    }
  }
}
