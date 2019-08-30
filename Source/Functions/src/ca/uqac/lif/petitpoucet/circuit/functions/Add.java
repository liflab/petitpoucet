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

public class Add extends NaryFunction
{
  public Add()
  {
    this(2);
  }
  
  public Add(int in_arity)
  {
    super(in_arity);
  }
  
  @Override
  public String toString()
  {
    return "+";
  }

  @Override
  public void getValue(Object[] inputs, Object[] outputs)
  {
    float out = 0;
    for (Object o : inputs)
    {
      if (o instanceof Number)
      {
        out += ((Number) o).floatValue();
      }
    }
    outputs[0] = out;
  }
}
