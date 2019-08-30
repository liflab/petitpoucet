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
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

/**
 * Applies a function to all the elements of input lists,
 * producing output lists.
 */
public class ApplyToAll extends CircuitFunction
{
  /**
   * The function to apply
   */
  protected CircuitFunction m_function;

  public ApplyToAll(CircuitFunction f)
  {
    super(f.getInputArity(), f.getOutputArity());
    m_function = f;
  }
  
  @Override
  public String toString()
  {
    return "ApplyToAll";
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void getValue(Object[] inputs, Object[] outputs)
  {
    List[] in_lists = new List[m_inArity];
    int num_el = Integer.MAX_VALUE;
    for (int i = 0; i < m_inArity; i++)
    {
      in_lists[i] = (List<?>) inputs[i];
      num_el = Math.min(num_el, in_lists[i].size());
    }
    for (int i = 0; i < m_outArity; i++)
    {
      outputs[i] = new ArrayList<Object>();
    }
    for (int i = 0; i < num_el; i++)
    {
      Object[] outs = evaluateInnerFunctionAt(i, in_lists);
      for (int j = 0; j < m_outArity; j++)
      {
        ((List<Object>) outputs[j]).add(outs[j]);
      }
    }		
  }

  protected Object[] evaluateInnerFunctionAt(int pos, List<?> ... lists)
  {
    m_function.reset();
    Object[] ins = new Object[m_inArity];
    Object[] outs = new Object[m_outArity];
    for (int j = 0; j < m_inArity; j++)
    {
      ins[j] = lists[j].get(pos);
    }
    m_function.getValue(ins, outs);
    return outs;
  }

  @Override
  protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d, List<List<DesignatorLink>> links)
  {
    Designator top = d.peek();
    if (!(top instanceof NthElement))
    {
      // Can't do anything with this query; at best, say that
      // output depends on all input
      ArrayList<DesignatorLink> in_list = new ArrayList<DesignatorLink>(m_inArity);
      for (int i = 0; i < m_inArity; i++)
      {
        ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new CircuitDesignator.NthInput(i), this);
        ConcreteDesignatorLink dl = new ConcreteDesignatorLink(Quality.OVER, dob);
        in_list.add(dl);
      }
      links.add(in_list);
    }
    else
    {
      int elem_index = ((NthElement) top).getIndex();
      Designator tail = d.tail();
      List<List<DesignatorLink>> l_f_links = getFunctionLinks(q, tail, output_nb, elem_index);
      for (List<DesignatorLink> f_links : l_f_links)
      {
        List<DesignatorLink> o_links = new ArrayList<DesignatorLink>(f_links.size());
        for (DesignatorLink f_dl : f_links)
        {
          List<DesignatedObject> l_f_dob = f_dl.getDesignatedObjects();
          for (DesignatedObject f_dob : l_f_dob)
          {
            Designator f_dob_d = f_dob.getDesignator().peek();
            if (f_dob_d instanceof NthInput)
            {
              // Convert the inner function's input into ApplyToAll's input
              int index = ((NthInput) f_dob_d).getIndex();
              // Input <index> of the inner function is the <elem_index>-th element
              // of input <index> of ApplyToAll
              ComposedDesignator cd = new ComposedDesignator(f_dob_d.tail(), new NthElement(elem_index), new NthInput(index));
              ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(cd, this);
              ConcreteDesignatorLink cdl = new ConcreteDesignatorLink(f_dl.getQuality(), cdo);
              o_links.add(cdl);
            }          
          }
        }
        links.add(o_links);
      }
    }
  }
  
  protected List<List<DesignatorLink>> getFunctionLinks(TraceabilityQuery q, Designator d, int output_nb, int elem_index)
  {
    ComposedDesignator cd = new ComposedDesignator(d, new NthOutput(output_nb));
    // Replace the function in the context when it evaluated this input
    evaluateInnerFunctionAt(elem_index, (List[]) m_inputs);
    return m_function.query(q, cd);
  }
}