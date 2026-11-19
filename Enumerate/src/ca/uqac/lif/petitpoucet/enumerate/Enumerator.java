/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.enumerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.circuit.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.function.Computable;
import ca.uqac.lif.petitpoucet.render.Clause;
import ca.uqac.lif.petitpoucet.render.GraphUtilities;
import ca.uqac.lif.petitpoucet.render.MathSet;
import ca.uqac.lif.synthia.Bounded;
import ca.uqac.lif.synthia.enumerative.AllPickers;
import ca.uqac.lif.synthia.sequence.Playback;

public class Enumerator
{
	/*@ non_null @*/ protected final Computable m_function;
	
	/*@ non_null @*/ protected final Set<MathSet<Clause>> m_dnfs;
	
	/*@ non_null @*/ protected final List<Vertex> m_explanations;
	
	public Enumerator(/*@ non_null @*/ Computable function)
	{
		super();
		m_function = function;
		m_dnfs = new HashSet<>();
		m_explanations = new ArrayList<>();
	}
	
	public void enumerate(/*@ non_null @*/ Bounded<? extends Object[]> all)
	{
		all.reset();
		while (!all.isDone())
		{
			m_function.reset();
			Object[] inputs = all.pick();
			Object[] outputs = new Object[m_function.getOutputArity()];			
			m_function.evaluate(inputs, outputs);
			if (m_function instanceof Explainable)
			{
				Explainable e = (Explainable) m_function;
				try
				{
					Vertex v = e.explain(OutputPart.FIRST);
					ConcreteVertex cv = Vertex.get(v);
					MathSet<Clause> dnf = GraphUtilities.asDnf(cv);
					for (MathSet<Clause> c : m_dnfs)
					{
						if (equivalent(dnf, c))
						{
							dnf = null;
							break;
						}
					}
					if (dnf != null)
					{
						m_dnfs.add(dnf);
						m_explanations.add(cv);
					}
				}
				catch (ExplanationException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	/*@ pure @*/ public int size()
	{
		return m_dnfs.size();
	}
	
	/*@ pure non_null @*/ public List<Vertex> getExplanations()
	{
		return m_explanations;
	}
	
	protected static boolean equivalent(MathSet<Clause> dnf1, MathSet<Clause> dnf2)
	{
		Set<PartVertex> allparts = new HashSet<>();
		for (Clause c : dnf1)
		{
			allparts.addAll(c.mentionedParts());
		}
		for (Clause c : dnf2)
		{
			allparts.addAll(c.mentionedParts());
		}
		List<PartVertex> parts = List.copyOf(allparts);
		Bounded<?>[] values = new Bounded[allparts.size()];
		for (int i = 0; i < allparts.size(); i++)
		{
			values[i] = new Playback<Boolean>(false, true).setLoop(false);
		}
		AllPickers valuations = new AllPickers(values);
		while (!valuations.isDone())
		{
			Object[] valuation = valuations.pick();
			Map<PartVertex, Boolean> valuationMap = new HashMap<>();
			for (int i = 0; i < parts.size(); i++)
			{
				valuationMap.put(parts.get(i), (Boolean) valuation[i]);
			}
			if (Clause.evaluateDnf(dnf1, valuationMap) != Clause.evaluateDnf(dnf2, valuationMap))
			{
				return false;
			}
		}
		return true;
	}
}
