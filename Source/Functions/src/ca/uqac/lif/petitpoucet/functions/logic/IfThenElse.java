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
package ca.uqac.lif.petitpoucet.functions.logic;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.LazyFunction;

public class IfThenElse implements Function, LazyFunction
{
	public static final transient IfThenElse instance = new IfThenElse();
	
	protected static final transient IfThenElseQueryable s_queryableFirst = new IfThenElseQueryable(true);
	
	protected static final transient IfThenElseQueryable s_queryableSecond = new IfThenElseQueryable(false);
	
	protected IfThenElse()
	{
		super();
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IfThenElse duplicate(boolean with_state) 
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track)
	{
		boolean b = (Boolean) inputs[0];
		FunctionQueryable q = null;
		if (b)
		{
			if (inputs[1] == null)
			{
				return null;
			}
			outputs[0] = inputs[1];
			if (track)
			{
				q = s_queryableFirst;
			}
		}
		else
		{
			if (inputs[2] == null)
			{
				return null;
			}
			outputs[0] = inputs[2];
			if (track)
			{
				q = s_queryableSecond;
			}
		}
		return q;
	}
	
	@Override
	public String toString()
	{
		return "IfThenElse";
	}
	
	protected static class IfThenElseQueryable extends FunctionQueryable
	{
		protected boolean m_first;
		
		public IfThenElseQueryable(boolean first) 
		{
			super("IfThenElse", 3, 1);
			m_first = first;
		}
		
		@Override
		protected List<TraceabilityNode> queryCausality(int out_index, 
				Designator d, TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(2);
			TraceabilityNode and = factory.getAndNode();
			root.addChild(and, Quality.EXACT);
			TraceabilityNode cond_n = factory.getObjectNode(NthInput.get(0), this);
			and.addChild(cond_n, Quality.EXACT);
			leaves.add(cond_n);
			if (m_first)
			{
				TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(d, NthInput.get(1)), this);
				and.addChild(n, Quality.EXACT);
				leaves.add(n);
			}
			else
			{
				TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(d, NthInput.get(2)), this);
				and.addChild(n, Quality.EXACT);
				leaves.add(n);
			}
			return leaves;
		}

		@Override
		protected List<TraceabilityNode> queryConsequence(int in_index, 
				Designator d, TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			if ((m_first && in_index == 2) || (!m_first && in_index == 1))
			{
				TraceabilityNode n = factory.getObjectNode(Designator.nothing, this);
				root.addChild(n, Quality.NONE);
				leaves.add(n);
			}
			else
			{
				TraceabilityNode n;
				if (in_index == 0)
				{
					n = factory.getObjectNode(NthOutput.get(0), this);
				}
				else
				{
					n = factory.getObjectNode(new ComposedDesignator(d, NthOutput.get(0)), this);
				}
				root.addChild(n, Quality.EXACT);
				leaves.add(n);
			}
			return leaves;
		}
	}

	@Override
	public IfThenElse duplicate()
	{
		return duplicate(false);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs, Context c) 
	{
		return evaluate(inputs, outputs, c, true);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs, boolean track) 
	{
		return evaluate(inputs, outputs, null, track);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs)
	{
		return evaluate(inputs, outputs, null, true);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		return Boolean.class;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		return Object.class;
	}

	@Override
	public int getInputArity() 
	{
		return 3;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}

	@Override
	public void reset() 
	{
		// Nothing to do
	}
}
