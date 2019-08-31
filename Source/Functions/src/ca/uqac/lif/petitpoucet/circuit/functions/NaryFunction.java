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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;

public abstract class NaryFunction extends SingleFunction
{
  public NaryFunction(int in_arity)
  {
    super(in_arity, 1);
  }
  
  @Override
  protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
      List<List<DesignatorLink>> links)
  {
    // Default behaviour: a function's (single) output is linked to all its inputs
    List<DesignatorLink> in_links = new ArrayList<DesignatorLink>(m_inArity);
    for (int i = 0; i < m_inArity; i++)
    {
      ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(new CircuitDesignator.NthInput(i), this);
      ConcreteDesignatorLink cdl = new ConcreteDesignatorLink(Quality.EXACT, cdo);
      in_links.add(cdl);
    }
    links.add(in_links);
  }
}
