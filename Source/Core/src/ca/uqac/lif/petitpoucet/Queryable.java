package ca.uqac.lif.petitpoucet;

import java.util.List;

public interface Queryable 
{
	/**
	 * Answers a traceability query.
	 * 
	 * @param q
	 *          The query
	 * @param d
	 *          The part of the object that is the subject of the query
	 * @param root
	 *          The node to which the results of the query should be appended as
	 *          children
	 * @param factory
	 *          A factory to produce traceability nodes
	 * @return The list of terminal traceability nodes produced by this query
	 */
	/* @ non_null @ */ public List<TraceabilityNode> query(/* @ non_null @ */ TraceabilityQuery q,
			/* @ non_null @ */ Designator d, /* @ non_null @ */ TraceabilityNode root,
			/* @ non_null @ */ Tracer factory);
}
