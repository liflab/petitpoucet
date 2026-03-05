package ca.uqac.lif.petitpoucet;

public interface AbstractVertex extends Renderer
{
	/**
	 * Adds a child to this vertex. This method also adds this vertex as a parent
	 * of the child. It is not recommended to modify the list of children or parents
	 * directly, as it may cause inconsistencies in the graph.
	 * @param v The vertex to add as a child of this vertex
	 */
	public void addChild(Vertex v);
	
	
}
