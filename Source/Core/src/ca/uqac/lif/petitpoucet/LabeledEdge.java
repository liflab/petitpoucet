package ca.uqac.lif.petitpoucet;

public interface LabeledEdge
{
	/**
	 * The "quality" of the link, which can be:
	 * <ul>
	 * <li>exact: all the designators in this link represent it exactly</li>
	 * <li>an over-approximation: the actual link is included by the
	 * designators</li>
	 * <li>an under-approximation: the actual link includes the designators</li>
	 * </ul>
	 */
	public enum Quality
	{
		EXACT, OVER, UNDER
	}

	public Quality getQuality();

	public TraceabilityNode getNode();
}
