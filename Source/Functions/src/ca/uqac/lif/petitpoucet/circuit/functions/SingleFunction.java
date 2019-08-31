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

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

public abstract class SingleFunction extends Function
{
  public SingleFunction(int in_arity, int out_arity)
  {
    super(in_arity, out_arity);
  }

  /**
   * Evaluates the function with concrete values. Note that this implementation is
   * not very efficient, as a function that is involved as the input of 
   * @return outputs An array of output arguments
   */
  @Override
  public final Object[] evaluate()
  {
    if (m_evaluated)
    {
      return m_returnedValue;
    }
    for (int i = 0; i < m_inArity; i++)
    {
      FunctionConnection fc = m_inputConnections[i];
      if (fc == null)
      {
        m_inputs[i] = null;
      }
      else
      {
        m_inputs[i] = fc.pullValue();
      }
    }
    getValue(m_inputs, m_returnedValue);
    m_evaluated = true;
    return m_returnedValue;
  }

  @Override
  public List<List<DesignatorLink>> query(TraceabilityQuery q, Designator d)
  {
    List<List<DesignatorLink>> list = new ArrayList<List<DesignatorLink>>();
    Designator top = d.peek();
    if (!(top instanceof NthInput) && !(top instanceof NthOutput))
    {
      // Can't answer queries that are not about inputs or outputs
      list.add(putIntoList(DesignatorLink.UnknownLink.instance));
    }
    if (top instanceof NthInput)
    {
      // Ask for an input: find the output to which it is connected
      NthInput ni = (NthInput) top;
      FunctionConnection conn = m_inputConnections[ni.getIndex()];
      Designator tail = d.tail();
      if (tail == null)
      {
        tail = Designator.identity;
      }
      ComposedDesignator new_cd = new ComposedDesignator(tail, new NthOutput(conn.getIndex()));
      ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new_cd, conn.getObject());
      ConcreteDesignatorLink dl = new ConcreteDesignatorLink(Quality.EXACT, dob);
      list.add(putIntoList(dl));
    }
    else if (top instanceof NthOutput)
    {
      // Ask for an output: delegate whether it is a provenance or causality query
      int output_nb = ((NthOutput) top).getIndex();
      Designator tail = d.tail();
      if (tail != null)
      {
        answerQuery(q, output_nb, tail, list);
      }
      else
      {
        answerQuery(q, output_nb, Designator.identity, list);
      }
    }
    return list;
  }
  
  /*@ pure non_null @*/ protected static List<DesignatorLink> putIntoList(DesignatorLink ... links)
  {
    List<DesignatorLink> new_list = new ArrayList<DesignatorLink>(links.length);
    for (DesignatorLink dl : links)
    {
      new_list.add(dl);
    }
    return new_list;
  }

  protected abstract void answerQuery(TraceabilityQuery q, int output_nb, Designator d, List<List<DesignatorLink>> links);
}
