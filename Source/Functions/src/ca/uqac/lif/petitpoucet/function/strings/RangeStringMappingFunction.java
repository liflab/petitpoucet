package ca.uqac.lif.petitpoucet.function.strings;

/**
 * String mapping function that applies on a range of the input string,
 * denoted by a start and an end position.
 */
public abstract class RangeStringMappingFunction extends StringMappingFunction
{
	/**
	 * The start index of the range.
	 */
	protected final int m_start;
	
	/**
	 * The end index of the range.
	 */
	protected final int m_end;
	
	/**
	 * Creates a new instance of the function.
	 * @param start The start index of the range
	 * @param end The end index of the range
	 */
	public RangeStringMappingFunction(int start, int end)
	{
		super();
		m_start = start;
		m_end = end;
	}
	
	@Override
	protected String transformString(String s)
	{
		if (s.length() < m_start)
		{
			return "";
		}
		int end = Math.min(m_end, s.length());
		return applyOnRange(s, m_start, end);
	}
	
	/**
	 * Applies the function on a range whose bounds have already been checked
	 * with respect to the input string.
	 * @param s The string
	 * @param start The start index of the range
	 * @param end The end index of the range
	 * @return The result of the function
	 */
	protected abstract String applyOnRange(String s, int start, int end);
}
