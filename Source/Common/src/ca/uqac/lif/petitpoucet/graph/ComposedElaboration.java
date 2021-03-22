package ca.uqac.lif.petitpoucet.graph;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Elaboration;

public class ComposedElaboration implements Elaboration
{
	/**
	 * The elements of the elaboration.
	 */
	protected List<Elaboration> m_parts;
	
	/**
	 * The short elaboration to return.
	 */
	protected ConstantElaboration m_short;
	
	public static Elaboration create(ConstantElaboration short_e, Object ... parts)
	{
		if (parts.length == 0)
		{
			return short_e;
		}
		return new ComposedElaboration(short_e, parts);
	}
	
	public ComposedElaboration(ConstantElaboration short_e, Object ... parts)
	{
		super();
		m_short = short_e;
		m_parts = new ArrayList<Elaboration>();
		for (Object part : parts)
		{
			if (part instanceof Elaboration)
			{
				m_parts.add((Elaboration) part);
			}
			else
			{
				m_parts.add(new ConstantElaboration(part));
			}
		}
	}
	
	public void add(Elaboration e)
	{
		m_parts.add(e);
	}
	
	@Override
	public ConstantElaboration getShort()
	{
		return m_short;
	}
	
	@Override
	public Elaboration getLong()
	{
		if (m_parts.isEmpty())
		{
			return this;
		}
		return m_short;
	}
	
	@Override
	public String toString()
	{
		if (m_parts.isEmpty())
		{
			return m_short.toString();
		}
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < m_parts.size(); i++)
		{
			if (i > 0)
			{
				out.append(", ");
			}
			out.append(m_parts.get(i).getShort());
		}
		return out.toString();
	}
}