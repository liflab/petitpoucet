package ca.uqac.lif.petitpoucet.functions.strings;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.StringDesignator.Range;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;

public class Split extends NaryFunction
{
	protected String m_regex;
	
	protected List<Integer> m_offsets;
	
	public Split(String regex)
	{
		super(1);
		m_regex = regex;
	}
	
	@Override
	public String toString()
	{
		return "Split on " + m_regex;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		String s = inputs[0].toString();
		String[] parts = s.split(m_regex);
		List<String> l_parts = new ArrayList<String>(parts.length);
		m_offsets = new ArrayList<Integer>(parts.length + 1);
		int pos = 0;
		for (int i = 0; i < parts.length; i++)
		{
			String part = parts[i];
			l_parts.add(part);
			m_offsets.add(pos);
			pos += part.length();
			if (i < parts.length - 1)
			{
				pos += m_regex.length();
			}
		}
		m_offsets.add(pos);
		m_returnedValue[0] = l_parts;
		outputs[0] = l_parts;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator top = d.peek();
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		if (!(top instanceof NthElement))
		{
			answerQueryDefault(q, output_nb, d, root, factory, leaves, Quality.EXACT);
		}
		int pos = ((NthElement) top).getIndex();
		Designator h_tail = tail.peek();
		Designator cd;
		if (h_tail instanceof Range)
		{
			// Offset the range by the position in the input string
			Range r = (Range) h_tail;
			Designator t_tail = tail.tail();
			cd = new ComposedDesignator(t_tail, new Range(r.getStartIndex() + m_offsets.get(pos), r.getEndIndex() + m_offsets.get(pos)), new NthInput(0));
		}
		else
		{
			cd = new ComposedDesignator(tail, new Range(m_offsets.get(pos), m_offsets.get(pos + 1) - 1), new NthInput(0));
		}
		TraceabilityNode child = factory.getObjectNode(cd, this);
		root.addChild(child, Quality.EXACT);
		leaves.add(child);
	}
}
