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
import ca.uqac.lif.petitpoucet.Trackable;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.circuit.CircuitElement;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatorLink;

public abstract class CircuitFunction implements CircuitElement, Trackable
{
  /**
   * The function's input arity
   */
  protected int m_inArity;

  /**
   * The function's output arity
   */
  protected int m_outArity;

  /**
   * The input connections of this function
   */
  protected FunctionConnection[] m_inputConnections;

  /**
   * The input connections of this function
   */
  protected FunctionConnection[] m_outputConnections;

  /**
   * The inputs given to the function when it was evaluated
   */
  protected Object[] m_inputs;

  /**
   * The value returned by the function the last time it was called
   */
  protected Object[] m_returnedValue;

  /**
   * A flag indicating whether the function has been evaluated once
   */
  protected boolean m_evaluated = false;

  /**
   * Creates a new circuit function
   * @param in_arity The function's input arity
   * @param out_arity The function's output arity
   */
  public CircuitFunction(int in_arity, int out_arity)
  {
    super();
    m_inArity = in_arity;
    m_outArity = out_arity;
    m_inputConnections = new FunctionConnection[in_arity];
    m_outputConnections = new FunctionConnection[out_arity];
    m_inputs = new Object[in_arity];
    m_returnedValue = new Object[out_arity];
  }

  /**
   * Evaluates the function with concrete values. Note that this implementation is
   * not very efficient, as a function that is involved as the input of 
   * @return outputs An array of output arguments
   */
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

  /**
   * Puts the function back into an unevaluated state
   */
  public void reset()
  {
    m_evaluated = false;
  }

  @Override
  public void setToInput(int index, CircuitConnection connection)
  {
    m_inputConnections[index] = (FunctionConnection) connection;
  }

  @Override
  public void setToOutput(int index, CircuitConnection connection)
  {
    m_outputConnections[index] = (FunctionConnection) connection;
  }

  @Override
  public int getInputArity() 
  {
    return m_inArity;
  }

  @Override
  public int getOutputArity() 
  {
    return m_outArity;
  }

  @Override
  public Trackable getTrackable() 
  {
    return this;
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

  /**
   * Gets the value of this function for concrete inputs
   * @param inputs An array of input arguments
   * @param outputs An array of output arguments
   */
  public abstract void getValue(Object[] inputs, Object[] outputs);

  protected abstract void answerQuery(TraceabilityQuery q, int output_nb, Designator d, List<List<DesignatorLink>> links);
}
