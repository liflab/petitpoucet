/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;

/**
 * Utility class providing basic arithmetic operations.
 * @author Sylvain Hallé
 */
public abstract class Numbers extends Node
{
	/**
	 * Creates a new instance of the class.
	 * @param in_arity The input arity; the output arity is assumed to be 1
	 */
	public Numbers(int in_arity)
	{
		super(in_arity, 1);
	}
	
	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		float[] operands = new float[input.length];
		for (int i = 0; i < operands.length; i++)
		{
			Object o = input[i];
			if (!(o instanceof Number))
			{
				throw new IllegalArgumentException("Expected a number");
			}
			operands[i] = ((Number) o).floatValue();
		}
		float v = evaluate(operands);
		output[0] = v;
	}
	
	/**
	 * Calculates the return value of the function, given the numerical operands
	 * passed as arguments.
	 * @param operands The operands
	 * @return The return value
	 */
	protected abstract float evaluate(float[] operands);
	
	/**
	 * Implementation of addition on floating point numbers.
	 */
	public static class Addition extends Numbers
	{
		/**
		 * Creates a new instance of the function.
		 * @param in_arity The input arity of this instance
		 */
		public Addition(int in_arity)
		{
			super(in_arity);
		}

		@Override
		protected float evaluate(float[] operands)
		{
			float t = 0;
			for (float x : operands)
			{
				t += x;
			}
			return t;
		}
		
		@Override
		public String toString()
		{
			return "+";
		}
		
		@Override
		public Addition duplicate(boolean with_state)
		{
			return new Addition(getInputArity());
		}
	}
	
	/**
	 * Implementation of multiplication on floating point numbers.
	 */
	public static class Multiplication extends Numbers
	{
		/**
		 * The list of indices in the arguments where the value is 0.
		 * This is used to provide the explanation for the output result.
		 */
		/*@ null @*/ protected List<Integer> m_zeros;
		
		/**
		 * Creates a new instance of the function.
		 * @param in_arity The input arity of this instance
		 */
		public Multiplication(int in_arity)
		{
			super(in_arity);
			m_zeros = null;
		}

		@Override
		protected float evaluate(float[] operands)
		{
			float t = 1;
			for (int i = 0; i < operands.length; i++)
			{
				float x = operands[i];
				t *= x;
				if (x == 0)
				{
					if (m_zeros == null)
					{
						m_zeros = new ArrayList<>();
					}
					m_zeros.add(i);
				}
			}
			return t;
		}
		
		@Override
		public Vertex explain(Part p, VertexFactory f) throws ExplanationException
		{
			checkHead(p);
			if (m_zeros != null)
			{
				if (m_zeros.size() == 1)
				{
					return f.getPart(new InputPart(m_zeros.get(0)), this);
				}
				Vertex o = f.getOr();
				for (int z : m_zeros)
				{
					o.addChild(f.getPart(new InputPart(z), this));
				}
				return o;
			}
			return super.explain(p, f);
		}
		
		@Override
		public void reset()
		{
			super.reset();
			m_zeros = null;
		}
		
		@Override
		public String toString()
		{
			return "\u00d7";
		}

		@Override
		public Multiplication duplicate(boolean with_state)
		{
			return new Multiplication(getInputArity());
		}
	}
}
