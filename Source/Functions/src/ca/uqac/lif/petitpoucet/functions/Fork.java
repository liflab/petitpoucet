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
package ca.uqac.lif.petitpoucet.functions;

import java.util.List;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.common.Context;

public class Fork implements Function
{
	/*@ non_null @*/ protected Class<?> m_class;
	
	/*@ non_null @*/ protected transient ForkQueryable m_forkQueryable;
	
	protected int m_outArity;
	
	public Fork(Class<?> clazz, int out_arity)
	{
		super();
		m_class = clazz;
		m_outArity = out_arity;
		m_forkQueryable = new ForkQueryable(out_arity);
	}
	
	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fork duplicate(boolean with_state) 
	{
		return this;
	}

	@Override
	public Fork duplicate()
	{
		return duplicate(false);
	}

	@Override
	public ForkQueryable evaluate(Object[] inputs, Object[] outputs, Context c) 
	{
		for (int i = 0; i < m_outArity; i++)
		{
			outputs[i] = inputs[0];
		}
		return m_forkQueryable;
	}

	@Override
	public ForkQueryable evaluate(Object[] inputs, Object[] outputs)
	{
		return evaluate(inputs, outputs, null);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		return m_class;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		return m_class;
	}

	@Override
	public int getInputArity() 
	{
		return 1;
	}

	@Override
	public int getOutputArity() 
	{
		return m_outArity;
	}

	@Override
	public void reset() 
	{
		// Nothing to do
	}

	@Override
	public String toString()
	{
		return "fork " + m_outArity;
	}
	
	public static class ForkQueryable extends FunctionQueryable
	{
		protected ForkQueryable(int out_arity)
		{
			super("fork", 1, out_arity);
		}
		
		@Override
		protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int in_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			return allOutputsLink(in_index, tail, root, factory);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			return allInputsLink(out_index, tail, root, factory);
		}
		
		@Override
		public ForkQueryable duplicate(boolean with_state)
		{
			return this;
		}
	}
}
