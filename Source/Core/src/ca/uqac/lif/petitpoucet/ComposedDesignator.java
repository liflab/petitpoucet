package ca.uqac.lif.petitpoucet;

import java.util.ArrayList;
import java.util.List;

public class ComposedDesignator implements Designator
{
	/**
	 * The list of designators representing the composition
	 */
	protected List<Designator> m_designators;

	/**
	 * Creates a new composed designator
	 * 
	 * @param designators
	 *          The list of designators representing the composition
	 */
	public ComposedDesignator(Designator... designators)
	{
		super();
		m_designators = new ArrayList<Designator>(designators.length);
		for (Designator d : designators)
		{
			add(d);
		}
	}

	/**
	 * Adds a new designator to the composition
	 * 
	 * @param d
	 *          The designator to add. If null, the operation is simply ignored.
	 * @return This composed designator
	 */
	public ComposedDesignator add(/* @ null @ */ Designator d)
	{
		if (d == null)
		{
			return this;
		}
		if (d instanceof ComposedDesignator)
		{
			// Don't unnecessarily nest composed designators
			m_designators.addAll(((ComposedDesignator) d).m_designators);
		}
		else
		{
			m_designators.add(d);
		}
		return this;
	}

	/**
	 * Creates a new designator by removing the first element of the composition
	 * 
	 * @return A new designator with the first element of the composition removed
	 */
	public Designator tail()
	{
		switch (m_designators.size())
		{
		case 1:
			return Designator.identity;
		case 2:
			return m_designators.get(0);
		default:
			ComposedDesignator cd = new ComposedDesignator();
			for (int i = 0; i < m_designators.size() - 1; i++)
			{
				cd.add(m_designators.get(i));
			}
			return cd;
		}
	}

	/**
	 * Gets the first designator of the composition, without removing it
	 * 
	 * @return The first designator, or <tt>null</tt> if the composition is empty
	 */
	/* @ pure null @ */ public Designator peek()
	{
		if (m_designators.isEmpty())
		{
			return null;
		}
		return m_designators.get(m_designators.size() - 1);
	}

	@Override
	/* @ pure @ */ public String toString()
	{
		StringBuilder out = new StringBuilder();
		Designator previous = null;
		for (int i = 0; i < m_designators.size(); i++)
		{
			if (i > 0 && (previous != null && !(previous instanceof Identity)))
			{
				out.append(" ");
			}
			previous = m_designators.get(i);
			out.append(previous);
		}
		return out.toString();
	}

	@Override
	public int hashCode()
	{
		int h = 0;
		for (Designator d : m_designators)
		{
			h += d.hashCode();
		}
		return h;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ComposedDesignator))
		{
			return false;
		}
		ComposedDesignator cd = (ComposedDesignator) o;
		if (cd.m_designators.size() != m_designators.size())
		{
			return false;
		}
		for (int i = 0; i < m_designators.size(); i++)
		{
			if (!m_designators.get(i).equals(cd.m_designators.get(i)))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean appliesTo(Object o)
	{
		if (m_designators.isEmpty())
		{
			return false;
		}
		Designator d = m_designators.get(m_designators.size() - 1);
		return d.appliesTo(o);
	}
}
