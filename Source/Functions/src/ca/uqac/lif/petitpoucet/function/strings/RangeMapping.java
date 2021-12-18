/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet.function.strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of associations between input and output ranges.
 */
public class RangeMapping
{
	/**
	 * The (sorted) list of associations between ranges of characters.
	 */
	/*@ non_null @*/ protected final List<RangePair> m_mapping;

	/**
	 * Creates a new empty range mapping.
	 */
	public RangeMapping(RangePair ... pairs)
	{
		super();
		m_mapping = new LinkedList<RangePair>();
		for (RangePair rp : pairs)
		{
			m_mapping.add(rp);
		}
		Collections.sort(m_mapping);
	}

	/**
	 * Creates a new range mapping from the contents of another one.
	 * @param m The other mapping
	 */
	public RangeMapping(RangeMapping m)
	{
		this();
		m_mapping.addAll(m.m_mapping);
	}

	/**
	 * Adds a pair of ranges to the mapping.
	 * @param from The first range of the pair
	 * @param to The second range of the pair
	 * @return This mapping
	 */
	/*@ non_null @*/ public RangeMapping add(Range from, Range to)
	{
		return add(new RangePair(from, to));
	}

	/**
	 * Adds a range pair to the mapping.
	 * @param new_rp The range pair to add
	 * @return This mapping
	 */
	/*@ non_null @*/ protected RangeMapping add(RangePair new_rp)
	{
		int left_index = -1;
		for (RangePair rp : m_mapping)
		{
			if (rp.getTo().getEnd() < new_rp.getTo().getStart())
			{
				left_index++;
			}
			else
			{
				break;
			}
		}
		if (left_index == m_mapping.size())
		{
			m_mapping.add(new_rp);
		}
		else
		{
			m_mapping.add(left_index + 1, new_rp);	
		}
		merge(left_index + 1);
		return this;
	}

	/**
	 * Gets the "input" ranges mapped to a given "output" range.
	 * @param r The output range
	 * @return The list of ranges to which this output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> invert(/*@ null @*/ Range r)
	{
		List<Range> ranges = new ArrayList<Range>();
		if (r == null)
		{
			return ranges;
		}
		for (RangePair rp : m_mapping)
		{
			if (rp.getTo().overlaps(r))
			{
				Range i_r = rp.invert(r);
				if (!ranges.isEmpty())
				{
					Range last = ranges.get(ranges.size() - 1);
					if (last.getEnd() == i_r.getStart() - 1)
					{
						Range merged = new Range(last.getStart(), i_r.getEnd());
						ranges.remove(ranges.size() - 1);
						ranges.add(merged);
					}
					else
					{
						ranges.add(i_r);
					}
				}
				else
				{
					ranges.add(i_r);
				}
			}
		}
		return ranges;
	}

	/**
	 * Gets the "input" ranges mapped to a given "output" range.
	 * @param start The start position
	 * @param end The end position
	 * @return The list of ranges to which this output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> invert(int start, int end)
	{
		return invert(new Range(start, end));
	}

	/**
	 * Gets the "input" ranges mapped to the whole "output" range.
	 * @return The list of ranges to which the whole output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> invert()
	{
		List<Range> ranges = new ArrayList<Range>();
		for (RangePair rp : m_mapping)
		{
			Range r = rp.getFrom();
			if (ranges.isEmpty())
			{
				ranges.add(r);
			}
			else
			{
				Range previous = ranges.get(ranges.size() - 1);
				if (previous.getEnd() == r.getStart() - 1)
				{
					ranges.remove(ranges.size() - 1);
					Range merged = new Range(previous.getStart(), r.getEnd());
					ranges.add(merged);
				}
			}
		}
		return ranges;
	}

	/**
	 * Attempts to merge a range pair at a given position in the list to its two
	 * neighboring range pairs on the left and the right, if they exist. Two
	 * pairs can be merged if both their input ranges and their output ranges
	 * are contiguous, and both are bijective. In such a case, the two pairs can
	 * be replaced by a single pair with the union of their input and output
	 * ranges.
	 * @param position The position of the range pair to examine
	 */
	protected void merge(int position)
	{
		if (!m_mapping.get(position).isBijective())
		{
			// If the newly inserted mapping is not bijective, cannot merge
			return;
		}
		// Check if merge left
		if (position > 0)
		{
			RangePair rp_left = m_mapping.get(position - 1);
			RangePair rp_current = m_mapping.get(position);
			if (rp_left.isContiguous(rp_current) && rp_left.isBijective())
			{
				RangePair new_rp = new RangePair(new Range(rp_left.getFrom().getStart(), rp_current.getFrom().getEnd()),
						new Range(rp_left.getTo().getStart(), rp_current.getTo().getEnd()));
				m_mapping.remove(position);
				m_mapping.remove(position - 1);
				m_mapping.add(position - 1, new_rp);
				position--;
			}
		}
		// Check if merge right
		if (position < m_mapping.size() - 1)
		{
			RangePair rp_current = m_mapping.get(position);
			RangePair rp_right = m_mapping.get(position + 1);
			if (rp_current.isContiguous(rp_right))
			{
				RangePair new_rp = new RangePair(new Range(rp_current.getFrom().getStart(), rp_right.getFrom().getEnd()),
						new Range(rp_current.getTo().getStart(), rp_right.getTo().getEnd()));
				m_mapping.remove(position + 1);
				m_mapping.remove(position);
				m_mapping.add(position, new_rp);
			}
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof RangeMapping))
		{
			return false;
		}
		RangeMapping rm = (RangeMapping) o;
		if (rm.m_mapping.size() != m_mapping.size())
		{
			return false;
		}
		for (RangePair rp : m_mapping)
		{
			if (!rm.m_mapping.contains(rp))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return m_mapping.toString();
	}

	/**
	 * Association between two ranges.
	 */
	protected static class RangePair implements Comparable<RangePair>
	{
		/**
		 * The first range.
		 */
		/*@ non_null @*/ protected final Range m_from;

		/**
		 * The second range.
		 */
		/*@ non_null @*/ protected final Range m_to;

		/**
		 * Creates a new range pair.
		 * @param from The first range
		 * @param to The second range
		 */
		public RangePair(/*@ non_null @*/ Range from, /*@ non_null @*/ Range to)
		{
			super();
			m_from = from;
			m_to = to;
		}
		
		/**
		 * Creates a new range pair.
		 * @param from_start The start index of the first range
		 * @param from_end The end index of the first range
		 * @param to_start The start index of the second range
		 * @param to_end The end index of the second range
		 */
		public RangePair(int from_start, int from_end, int to_start, int to_end)
		{
			this(new Range(from_start, from_end), new Range(to_start, to_end));
		}

		/**
		 * Gets the first range of the pair.
		 * @return The first range
		 */
		/*@ pure non_null @*/ public Range getFrom()
		{
			return m_from;
		}

		/**
		 * Gets the second range of the pair.
		 * @return The second range
		 */
		/*@ pure non_null @*/ public Range getTo()
		{
			return m_to;
		}

		/**
		 * Gets the portion of the first range corresponding to a portion of the
		 * second range.
		 * @param r The second range
		 * @return The portion of the first range
		 */
		/*@ null @*/ public Range invert(/*@ null @*/ Range r)
		{
			if (r == null)
			{
				return null;
			}
			if (!isBijective())
			{
				return m_from;
			}
			Range out_portion = m_to.intersect(r);
			if (out_portion == null)
			{
				return null;
			}
			return out_portion.shift(m_from.getStart() - m_to.getStart());
		}

		/**
		 * Determines if this range pair is contiguous to another one.
		 * @param rp The range pair to compare it to 
		 * @return <tt>true</tt> if it is contiguous, <tt>false</tt> otherwise
		 */
		/*@ pure @*/ public boolean isContiguous(RangePair rp)
		{
			return rp.getFrom().getEnd() == m_from.getStart() - 1 && rp.getTo().getEnd() == m_to.getStart() - 1;
		}
		
		/**
		 * Determines if a pair of ranges is bijective. It is so when both its
		 * ranges have the same length. In such a case, each element of a range can
		 * be mapped to a unique element of the other.
		 * @return <tt>true</tt> if the pair is bijective, <tt>false</tt> otherwise
		 */
		/*@ pure @*/ public boolean isBijective()
		{
			return m_from.length() == m_to.length();
		}

		@Override
		public int compareTo(RangePair p)
		{
			int p_start = p.getFrom().getStart();
			int start = m_from.getStart();
			if (start > p_start)
			{
				return 1;
			}
			if (start < p_start)
			{
				return -1;
			}
			return 0;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof RangePair))
			{
				return false;
			}
			RangePair rp = (RangePair) o;
			return m_from.equals(rp.m_from) && m_to.equals(rp.m_to);
		}
		
		@Override
		public int hashCode()
		{
			return m_from.hashCode() + m_to.hashCode();
		}

		@Override
		public String toString()
		{
			return m_from + "->" + m_to;
		}
	}
}
