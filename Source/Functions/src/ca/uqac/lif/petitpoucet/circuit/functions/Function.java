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

import ca.uqac.lif.petitpoucet.Trackable;
import ca.uqac.lif.petitpoucet.circuit.CircuitConnection;
import ca.uqac.lif.petitpoucet.circuit.CircuitElement;

public abstract class Function implements CircuitElement, Trackable
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
  public Function(int in_arity, int out_arity)
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
   * Puts the function back into an unevaluated state
   */
  public void reset()
  {
    m_evaluated = false;
    for (FunctionConnection fc : m_inputConnections)
    {
      if (fc != null)
      {
        fc.reset();
      }
    }
    for (FunctionConnection fc : m_outputConnections)
    {
      if (fc != null)
      {
        fc.reset();
      }
    }
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

  /**
   * Gets the value of this function for concrete inputs
   * @param inputs An array of input arguments
   * @param outputs An array of output arguments
   */
  public abstract void getValue(Object[] inputs, Object[] outputs);
  
  /**
   * Evaluates the function
   * @return The function's values
   */
  public abstract Object[] evaluate();
  
  /**
   * Creates a copy of an array
   * @param objects The objects to copy
   * @return A new array
   */
  public static Object[] copyArray(Object ... objects)
  {
    Object[] out = new Object[objects.length];
    for (int i = 0; i < objects.length; i++)
    {
      out[i] = objects[i];
    }
    return out;
  }
}
