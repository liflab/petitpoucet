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
 * Utility class providing basic logical operations.
 * @author Sylvain Hallé
 */
public abstract class Booleans extends Node
{
	/**
	 * Creates a new instance of the class.
	 * @param in_arity The input arity; the output arity is assumed to be 1
	 */
	public Booleans(int in_arity)
	{
		super(in_arity, 1);
	}
	
	@Override
	protected void evaluate(Object[] input, Object[] output)
	{
		boolean[] arguments = new boolean[input.length];
		for (int i = 0; i < arguments.length; i++)
		{
			Object o = input[i];
			if (!(o instanceof Boolean))
			{
				throw new IllegalArgumentException("Expected a boolean");
			}
			arguments[i] = (Boolean) o;
		}
		output[0] = getValue(arguments);
	}
	
	protected abstract boolean getValue(boolean[] arguments);
	
	/**
	 * A Boolean AND operator.
	 */
	public static class And extends Booleans
	{
		/**
		 * The list of inputs that are false, if any. If null, it means all inputs
		 * are true.
		 */
		/*@ null @*/ protected List<Integer> m_falseInputs;
		
		/**
		 * Creates a new instance of the class.
		 * @param in_arity The input arity; the output arity is assumed to be 1
		 */
		public And(int in_arity)
		{
			super(in_arity);
			m_falseInputs = null;
		}

		@Override
		public And duplicate(boolean with_state)
		{
			return new And(getInputArity());
		}

		@Override
		protected boolean getValue(boolean[] arguments)
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (!arguments[i])
				{
					if (m_falseInputs == null)
					{
						m_falseInputs = new ArrayList<>();
					}
					m_falseInputs.add(i);
				}
			}
			return m_falseInputs == null;
		}
		
		@Override
		public Vertex explain(Part p, VertexFactory f) throws ExplanationException
		{
			checkHead(p);
			if (m_falseInputs != null)
			{
				if (m_falseInputs.size() == 1)
				{
					return f.getPart(new InputPart(m_falseInputs.get(0)), this);
				}
				Vertex o = f.getOr();
				for (int z : m_falseInputs)
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
			m_falseInputs = null;
		}
		
		@Override
		public String toString()
		{
			return "\u2227";
		}
	}
	
	/**
	 * A Boolean OR operator.
	 */
	public static class Or extends Booleans
	{
		/**
		 * The list of inputs that are true, if any. If null, it means all inputs
		 * are false.
		 */
		/*@ null @*/ protected List<Integer> m_trueInputs;
		
		/**
		 * Creates a new instance of the class.
		 * @param in_arity The input arity; the output arity is assumed to be 1
		 */
		public Or(int in_arity)
		{
			super(in_arity);
			m_trueInputs = null;
		}

		@Override
		public Or duplicate(boolean with_state)
		{
			return new Or(getInputArity());
		}

		@Override
		protected boolean getValue(boolean[] arguments)
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (arguments[i])
				{
					if (m_trueInputs == null)
					{
						m_trueInputs = new ArrayList<>();
					}
					m_trueInputs.add(i);
				}
			}
			return m_trueInputs != null;
		}
		
		@Override
		public Vertex explain(Part p, VertexFactory f) throws ExplanationException
		{
			checkHead(p);
			if (m_trueInputs != null)
			{
				if (m_trueInputs.size() == 1)
				{
					return f.getPart(new InputPart(m_trueInputs.get(0)), this);
				}
				Vertex o = f.getOr();
				for (int z : m_trueInputs)
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
			m_trueInputs = null;
		}
		
		@Override
		public String toString()
		{
			return "\u2228";
		}
	}
}
