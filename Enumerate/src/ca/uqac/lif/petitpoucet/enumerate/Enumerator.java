package ca.uqac.lif.petitpoucet.enumerate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public Enumerator(/*@ non_null @*/ Computable function)
	{
		super();
		m_function = function;
		m_dnfs = new HashSet<>();
	}
	
	public void enumerate(/*@ non_null @*/ Bounded<Object[]> picker)
	{
		picker.reset();
		while (!picker.isDone())
		{
			m_function.reset();
			Object[] inputs = picker.pick();
			Object[] outputs = new Object[m_function.getOutputArity()];			
			m_function.evaluate(inputs, outputs);
			if (m_function instanceof Explainable)
			{
				Explainable e = (Explainable) m_function;
				try
				{
					Vertex v = e.explain(OutputPart.FIRST);
					MathSet<Clause> dnf = GraphUtilities.asDnf(v);
					for (MathSet<Clause> c : m_dnfs)
					{
						if (equivalent(dnf, c))
						{
							dnf = null;
							break;
						}
						if (dnf == null)
						{
							m_dnfs.add(dnf);
						}
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
