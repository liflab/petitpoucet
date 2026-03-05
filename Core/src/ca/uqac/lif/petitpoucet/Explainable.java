/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet;

/**
 * Interface implemented by objects that can provide an explanation for some
 * part of their output.
 * @author Sylvain Hallé
 */
public interface Explainable
{
	/**
	 * Explains a part of the output of the object, by returning a vertex that
	 * can be used to trace back the origin of the part. The default implementation
	 * of this method uses a {@link VertexFactory} to create the vertex, but
	 * subclasses can override this method to use a custom factory.
	 * @param p The part to explain
	 * @return A vertex that can be used to trace back the origin of the part
	 * @throws ExplanationException If an error occurs during the calculation of
	 * the explanation
	 */
	public default AbstractVertex explain(Part p) throws ExplanationException
	{
		return explain(p, new VertexFactory());
	}
	
	/**
	 * Explains a part of the output of the object, by returning a vertex that
	 * can be used to trace back the origin of the part. The method uses a
	 * {@link VertexFactory} provided as an argument to create the vertex, which
	 * can be used to create custom vertices.
	 * @param p The part to explain
	 * @param f The factory to use to create the vertex
	 * @return A vertex that can be used to trace back the origin of the part
	 * @throws ExplanationException If an error occurs during the calculation
	 * of the explanation
	 */
	public AbstractVertex explain(Part p, VertexFactory f) throws ExplanationException;
	
	public void hint(Part p);
	
	/**
	 * Exception raised when an error occurs in the calculation of the
	 * explanation.
	 */
	public static class ExplanationException extends Exception
	{
		/**
		 * Dummy UID.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Creates a new instance of the exception.
		 * @param t The cause of the exception
		 */
		public ExplanationException(Throwable t)
		{
			super(t);
		}
		
		/**
		 * Creates a new instance of the exception.
		 * @param s The message of the exception
		 */
		public ExplanationException(String s)
		{
			super(s);
		}
		
	}
}
