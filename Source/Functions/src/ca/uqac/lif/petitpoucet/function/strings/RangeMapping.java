/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of associations between elements of two ordered lists, called the
 * "input" and the "output". A range mapping stores these associations in
 * the form of pairs of {@link Range}s. It provides methods to retrieve the
 * input range(s) associated to a given output range, and vice versa. Thus,
 * if <i>R</i> is a set of ranges, a range mapping can be seen as a pair of
 * functions <i>f</i><sup>&rarr;</sup> : <i>R</i>&rarr;2<sup><i>R</i></sup>
 * and <i>f</i><sup>&larr;</sup> : <i>R</i>&rarr;2<sup><i>R</i></sup>, one
 * associating input ranges to sets of output ranges, and the other
 * associating output ranges to sets of input ranges.
 */
public class RangeMapping
{
	/**
	 * The (sorted) list of associations between ranges of characters.
	 */
	/*@ non_null @*/ protected final List<RangePair> m_mapping;
	
	/**
	 * A flag indicating if the mapping has changed since the last call to
	 * {@link #simplify()}.
	 */
	protected boolean m_changed;
	
	/**
	 * Composes two range mappings. If a rang
	 * @param rm1 The first range mapping
	 * @param rm2 The second range mapping
	 * @return The composition of the two mappings
	 */
	/*@ non_null @*/ public static RangeMapping compose(/*@ non_null @*/ RangeMapping rm1, /*@ non_null @*/ RangeMapping rm2)
	{
		List<Range> rm1_to = rm1.getToRanges();
		List<Range> rm2_from = rm2.getFromRanges();
		List<Range> fragmented = fragment(rm1_to, rm2_from);
		RangeMapping rm_out = new RangeMapping();
		for (Range r : fragmented)
		{
			List<Range> ranges_up = rm1.trackToInput(r);
			List<Range> ranges_down = rm2.trackToOutput(r);
			if (!(ranges_up.isEmpty() && ranges_down.isEmpty()))
			{
				rm_out.add(new RangePair(ranges_up.get(0), ranges_down.get(0)));
			}
		}
		rm_out.simplify();
		return rm_out;
	}
	
	/**
	 * Merges the bounds of two lists of ranges and creates a new list of ranges
	 * out of it. The list is such that every range contains positions coming
	 * from the same range in both lists. This has for effect of "fragmenting"
	 * the original range along all the boundaries mentioned in ranges of either
	 * list.
	 * <p>
	 * For example, if the first list of ranges is [0,2], [3,7] and the second
	 * list is [0,5], [6,6], [7,10], the expected result is the list
	 * [0,2], [3,5], [6,6], [7,7], [8,10].
	 * @param list1 The first list of ranges; assumed to be sorted
	 * @param list2 The second list of ranges; assumed to be sorted
	 * @return The list of ranges resulting from the merging described above
	 */
	/*@ non_null @*/ static List<Range> fragment(List<Range> list1, List<Range> list2)
	{
		List<Integer> indices = new ArrayList<>();
		for (Range r : list1)
		{
			int start = r.getStart();
			int end = r.getEnd() + 1;
			if (!indices.contains(start))
			{
				indices.add(start);
			}
			if (!indices.contains(end))
			{
				indices.add(end);
			}
		}
		for (Range r : list2)
		{
			int start = r.getStart();
			int end = r.getEnd() + 1;
			if (!indices.contains(start))
			{
				indices.add(start);
			}
			if (!indices.contains(end))
			{
				indices.add(end);
			}
		}
		Collections.sort(indices);
		List<Range> out_list = new ArrayList<>(indices.size());
		for (int i = 0; i < indices.size() - 1; i++)
		{
			out_list.add(new Range(indices.get(i), indices.get(i + 1) - 1));
		}
		return out_list;
	}
	
	/**
	 * Creates a new empty range mapping.
	 */
	public RangeMapping(RangePair ... pairs)
	{
		super();
		m_mapping = new LinkedList<>();
		m_mapping.addAll(Arrays.asList(pairs));
		m_changed = true;
		simplify();
	}

	/**
	 * Creates a new range mapping from the contents of another one.
	 * @param m The other mapping
	 */
	public RangeMapping(RangeMapping m)
	{
		super();
		m_mapping = new LinkedList<>();
		m_mapping.addAll(m.m_mapping);
		m_changed = true;
		simplify();
	}
	
	/**
	 * Gets the list of range pairs in this mapping.
	 * @return The list of pairs
	 */
	/*@ pure non_null @*/ public List<RangePair> getPairs()
	{
		return m_mapping;
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
	 * Adds a pair of ranges to the mapping.
	 * @param from The first range of the pair
	 * @param to The second range of the pair
	 * @param bijective Whether the pair is declared as bijective or not
	 * @return This mapping
	 */
	/*@ non_null @*/ public RangeMapping add(Range from, Range to, boolean bijective)
	{
		return add(new RangePair(from, to, bijective));
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
		m_changed = true;
		merge(left_index + 1);
		return this;
	}
		
	/**
	 * Adds multiple range pairs to the mapping.
	 * @param new_rps The range pairs to add
	 * @return This mapping
	 */
	/*@ non_null @*/ protected RangeMapping addAll(Collection<RangePair> new_rps)
	{
		for (RangePair new_rp : new_rps)
		{
			add(new_rp);
		}
		return this;
	}

	/**
	 * Gets the "input" ranges mapped to a given "output" range.
	 * @param r The output range
	 * @return The list of ranges to which this output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> trackToInput(/*@ null @*/ Range r)
	{
		List<Range> ranges = new ArrayList<>();
		if (r == null)
		{
			return ranges;
		}
		for (RangePair rp : m_mapping)
		{
			if (rp.getTo().overlaps(r))
			{
				Range i_r = rp.trackToInput(r);
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
	 * Gets the "output" ranges mapped to the whole "input" range.
	 * @return The list of ranges to which the whole input is mapped
	 */
	/*@ pure non_null @*/ public List<Range> trackToOutput()
	{
		List<Range> ranges = new ArrayList<>();
		for (RangePair rp : m_mapping)
		{
			Range r = rp.getTo();
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
	 * Gets the "output" ranges mapped to a given "input" range.
	 * @param r The input range
	 * @return The list of ranges to which this input is mapped
	 */
	/*@ pure non_null @*/ public List<Range> trackToOutput(/*@ null @*/ Range r)
	{
		List<Range> ranges = new ArrayList<>();
		if (r == null)
		{
			return ranges;
		}
		for (RangePair rp : m_mapping)
		{
			if (rp.getFrom().overlaps(r))
			{
				Range i_r = rp.trackToOutput(r);
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
	 * Determines if the range mapping contains associations.
	 * @return <tt>true</tt> if the mapping contains associations, <tt>false</tt>
	 * otherwise
	 */
	/*@ pure @*/ public boolean isEmpty()
	{
		return m_mapping.isEmpty();
	}
	
	/**
	 * Gets the list of all ranges in the "from" part of each range pair.
	 * @return The list or ranges
	 */
	/*@ pure non_null @*/ protected List<Range> getFromRanges()
	{
		List<Range> ranges = new ArrayList<>();
		for (RangePair rp : m_mapping)
		{
			ranges.add(rp.getFrom());
		}
		return ranges;
	}
	
	/**
	 * Gets the list of all ranges in the "to" part of each range pair.
	 * @return The list or ranges
	 */
	/*@ pure non_null @*/ protected List<Range> getToRanges()
	{
		List<Range> ranges = new ArrayList<>();
		for (RangePair rp : m_mapping)
		{
			ranges.add(rp.getTo());
		}
		return ranges;
	}

	/**
	 * Gets the "input" ranges mapped to a given "output" range.
	 * @param start The start position
	 * @param end The end position
	 * @return The list of ranges to which this output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> trackToInput(int start, int end)
	{
		return trackToInput(new Range(start, end));
	}
	
	/**
	 * Gets the "input" ranges mapped to the whole "output" range.
	 * @return The list of ranges to which the whole output is mapped
	 */
	/*@ pure non_null @*/ protected List<RangePair> invertMapping(Range r)
	{
		List<RangePair> ranges = new ArrayList<>();
		if (r == null)
		{
			return ranges;
		}
		for (RangePair rp : m_mapping)
		{
			if (rp.getTo().overlaps(r))
			{
				Range i_r = rp.trackToInput(r);
				RangePair new_rp = new RangePair(i_r, rp.getTo().intersect(r));
				ranges.add(new_rp);
			}
		}
		return ranges;
	}

	/**
	 * Gets the "input" ranges mapped to the whole "output" range.
	 * @return The list of ranges to which the whole output is mapped
	 */
	/*@ pure non_null @*/ public List<Range> trackToInput()
	{
		List<Range> ranges = new ArrayList<>();
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
	 * Attempts to simplify the list of range pairs in this mapping by merging
	 * adjacent entries. This amounts to calling {@link #mergeRight(int)}
	 * successively on each element of the list, except the last one.
	 * @return This mapping
	 */
	/*@ non_null @*/ public RangeMapping simplify()
	{
		// No change made to the mapping since last call
		if (!m_changed)
		{
			return this;
		}
		sort();
		int position = 0;
		while (position < m_mapping.size() - 1)
		{
			// If mergeRight returns true, the size of m_mapping is decremented by 1,
			// otherwise nothing has changed and we move to the next element
			if (!mergeRight(position))
			{
				position++;
			}
		}
		m_changed = false;
		return this;
	}
	
	/**
	 * Sorts the range pairs inside this mapping.
	 * @return This mapping
	 */
	/*@ non_null @*/ public RangeMapping sort()
	{
		Collections.sort(m_mapping);
		return this;
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
		if (mergeLeft(position))
		{
			position--;
		}
		// Check if merge right
		mergeRight(position);
		m_changed = false;
	}
	
	/**
	 * Attempts to merge a range pair at a given position to its left neighbor,
	 * if it exists.
	 * @param position The position of the range pair to examine
	 * @return <tt>true</tt> if merging was possible, <tt>false</tt> otherwise
	 * @see #merge(int)
	 */
	protected boolean mergeLeft(int position)
	{
		boolean merged = false;
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
				merged = true;
			}
		}
		return merged;
	}
	
	/**
	 * Attempts to merge a range pair at a given position to its right neighbor,
	 * if it exists.
	 * @param position The position of the range pair to examine
	 * @return <tt>true</tt> if merging was possible, <tt>false</tt> otherwise
	 * @see #merge(int)
	 */
	protected boolean mergeRight(int position)
	{
		boolean merged = false;
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
				merged = true;
			}
		}
		return merged;
	}
	
	/**
	 * Adds the range pairs of another range mapping to the current one,
	 * optionally shifting their intervals by a fixed offset.
	 * @param m The other range mapping 
	 * @param from_offset The offset to apply to the interval of the "from"
	 * part of each range pair
	 * @param to_offset The offset to apply to the interval of the "to"
	 * part of each range pair
	 * @return This range mapping
	 */
	/*@ non_null @*/ public RangeMapping uniteWith(/*@ non_null @*/ RangeMapping m, int from_offset, int to_offset)
	{
		for (RangePair rp : m.m_mapping)
		{
			RangePair new_rp = new RangePair(rp.getFrom().shift(from_offset), rp.getTo().shift(to_offset));
			m_mapping.add(new_rp);
		}
		m_changed = true;
		simplify();
		return this;
	}
	
	/**
	 * Produces a range mapping where the to/from pairs of each association
	 * are flipped.
	 * @return The new range mapping
	 */
	/*@ non_null @*/ public RangeMapping reverse()
	{
		RangeMapping rm = new RangeMapping();
		for (RangePair rp : m_mapping)
		{
			rm.add(new RangePair(rp.getTo(), rp.getFrom()));
		}
		return rm;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof RangeMapping))
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
	public int hashCode()
	{
		return m_mapping.size();
	}

	@Override
	public String toString()
	{
		return m_mapping.toString();
	}

	/**
	 * Association between two ranges.
	 */
	public static class RangePair implements Comparable<RangePair>
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
		 * Whether this range pair is intended to be interpreted as a bijection
		 * between the two ranges. 
		 */
		protected final boolean m_isBijective;

		/**
		 * Creates a new range pair.
		 * @param from The first range
		 * @param to The second range
		 * @param bijective Set to <tt>false</tt> to make the range pair declare
		 * itself as non-bijective, regardless of the size of its two ranges
		 */
		public RangePair(/*@ non_null @*/ Range from, /*@ non_null @*/ Range to, boolean bijective)
		{
			super();
			m_from = from;
			m_to = to;
			m_isBijective = bijective;
		}
		
		/**
		 * Creates a new range pair.
		 * @param from The first range
		 * @param to The second range
		 */
		public RangePair(/*@ non_null @*/ Range from, /*@ non_null @*/ Range to)
		{
			this(from, to, true);
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
		 * Creates a new range pair.
		 * @param from_start The start index of the first range
		 * @param from_end The end index of the first range
		 * @param to_start The start index of the second range
		 * @param to_end The end index of the second range
		 * @param bijective Set to <tt>false</tt> to make the range pair declare
		 * itself as non-bijective, regardless of the size of its two ranges 
		 */
		public RangePair(int from_start, int from_end, int to_start, int to_end, boolean bijective)
		{
			this(new Range(from_start, from_end), new Range(to_start, to_end), bijective);
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
		/*@ null @*/ public Range trackToInput(/*@ null @*/ Range r)
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
		 * Gets the portion of the second range corresponding to a portion of the
		 * first range.
		 * @param r The first range
		 * @return The portion of the second range
		 */
		/*@ null @*/ public Range trackToOutput(/*@ null @*/ Range r)
		{
			if (r == null)
			{
				return null;
			}
			if (!isBijective())
			{
				return m_to;
			}
			Range in_portion = m_from.intersect(r);
			if (in_portion == null)
			{
				return null;
			}
			return in_portion.shift(m_to.getStart() - m_from.getStart());
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
		 * ranges have the same length <em>and</em> the pair has not been declared
		 * non-bijective at construction. In such a case, each element of a range
		 * can be mapped to a unique element of the other.
		 * @return <tt>true</tt> if the pair is bijective, <tt>false</tt> otherwise
		 */
		/*@ pure @*/ public boolean isBijective()
		{
			return m_isBijective && m_from.length() == m_to.length();
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
			if (m_isBijective == p.m_isBijective)
			{
				return 0;
			}
			if (m_isBijective)
			{
				return -1;
			}
			return 1;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof RangePair))
			{
				return false;
			}
			RangePair rp = (RangePair) o;
			return m_from.equals(rp.m_from) && m_to.equals(rp.m_to) && isBijective() == rp.isBijective();
		}
		
		@Override
		public int hashCode()
		{
			return m_from.hashCode() + m_to.hashCode();
		}

		@Override
		public String toString()
		{
			return m_from + (isBijective() ? "\u2194" : ":") + m_to;
		}
	}
}
