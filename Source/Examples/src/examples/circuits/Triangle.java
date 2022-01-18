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
package examples.circuits;

import static examples.util.GraphViewer.display;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.function.Circuit;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.Fork;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.booleans.And;
import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.IsGreaterThan;
import ca.uqac.lif.petitpoucet.function.reflect.InstanceOf;
import ca.uqac.lif.petitpoucet.function.vector.ElementAt;
import ca.uqac.lif.petitpoucet.function.vector.GetSize;

import static ca.uqac.lif.dag.NodeConnector.connect;

import java.util.Arrays;
import java.util.List;

/**
 * Given a list of elements, determines if they represent the lengths of the
 * sides of a valid triangle. This is a rephrasing of the "self-assessment
 * exercise" that opens Glenford Myers' book:
 * <ul>
 * <li>G. J. Myers. (1979). <i>The Art of Software Testing</i>, Wiley, ISBN
 * 0471043281.</li>
 * </ul>
 * Given a vector, the program verifies the following conditions, in this
 * order:
 * <ol>
 * <li>The vector must contain exactly three elements</li>
 * <li>The three elements must be numbers</li>
 * <li>The three numbers must be positive</li>
 * <li>Each pair of positive numbers must verify the triangle inequality with
 * respect to the third</li>
 * </ol>
 * These conditions are evaluated in a <em>fail-fast</em> manner: the first
 * that fails stops the operation. Indeed, if the vector does not contain
 * three elements, attempting to evaluate the second condition on it would
 * throw an exception —and so on.
 * <p>
 * The circuit itself is described in {@link IsValidTriangle}.
 * 
 * <h3>Explanation</h3>
 * <p>
 * It is interesting to examine the lineage graphs one can obtain, depending on
 * the input vector. This is done in the program by changing the contents of
 * variable {@code vector} that is given to the function.
 * 
 * <h4>Incorrect number of elements</h4>
 * <p>
 * The first graph corresponds to the execution of the program on the vector
 * [1, 2] (click on the graph to see it full size):
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/Triangle-not3-full.png"><img style="width:80px" src="{@docRoot}/doc-files/circuits/Triangle-not3-full.png" alt="Graph 1"></a>
 * <p>
 * The function that is mentioned in the graph is {@link GetSize}; the other
 * parts of the circuit are not present. Therefore, the graph correctly
 * "explains" that the issue with the input vector is related to its size. 
 * 
 * <h4>Incorrect element type</h4>
 * <p>
 * The second graph corresponds to the execution of the program on the vector
 * [1, 2, "foo"]:
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/Triangle-notR-full.png"><img style="width:60px" src="{@docRoot}/doc-files/circuits/Triangle-notR-full.png" alt="Graph 1"></a>
 * <p>
 * This time, the function that appears in the lineage graph is
 * {@link InstanceOf}. Moreover, one can observe that the only part of the
 * input vector that is mentioned is its third element ([2]). This correctly
 * reveals that this element, and only this one, is of an invalid type.
 * 
 * <h4>Negative values</h4>
 * <p>
 * The third graph corresponds to the execution of the program on the vector
 * [1, 2, -1]:
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/Triangle-notPos-full.png"><img style="width:120px" src="{@docRoot}/doc-files/circuits/Triangle-notPos-full.png" alt="Graph 1"></a>
 * <p>
 * The function that appears in the explanation is "&gt;0"; again this time,
 * the only part of the input involved in the explanation is the third element
 * of the vector ([2]). Therefore, this graph contains all the pieces
 * to explain that the element is not positive.
 * 
 * <h4>Violation of the triangle inequality</h4>
 * <p>
 * The fourth graph corresponds to the execution of the program on the vector
 * [1, 2, 1]: 
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/Triangle-notTri-full.png"><img style="width:200px" src="{@docRoot}/doc-files/circuits/Triangle-notTri-full.png" alt="Graph 1"></a>
 * <p>
 * {@link TriangleInequality} is the function that appears in the graph. Since
 * the inequality involves all three elements of the vector, it is normal to
 * see all three as leaves of the graph. However, the graph also reveals that,
 * among the three possible ways of grouping sides to form the inequality, only
 * one of them is invalid. The leaves corresponding to the first and third
 * element ([0] and [2]) are grouped under the addition, while the second [1]
 * is compared to them. Indeed, we see that 1+1 is not greater than 2. The
 * other two inequalities are satisfied, and therefore are not present in the
 * graph.
 * 
 * <h4>Valid triangle</h4>
 * <p> 
 * The last graph corresponds to the execution of the program on the vector
 * [2, 3, 4], which corresponds to a valid triangle:
 * <p>
 * <a href="{@docRoot}/doc-files/circuits/Triangle-valid-full.png"><img style="width:300px" src="{@docRoot}/doc-files/circuits/Triangle-valid-full.png" alt="Graph 1"></a>
 * <p>
 * This time, one can see that all parts of the circuit are present. Indeed,
 * all conditions must be true for the circuit to produce the value true.
 * <p>
 * This small example shows that a lineage graph can not only identify the
 * parts of the inputs that explain a result, but also the parts of the circuit
 * that are relevant for this result.
 * 
 * @author Sylvain Hallé
 */
public class Triangle
{
	public static void main(String[] args)
	{
		List<?> vector = Arrays.asList(1, 1, 1);
		IsValidTriangle f = new IsValidTriangle();
		if (Boolean.TRUE.equals(f.evaluate(vector)[0]))
		{
			System.out.println("The triangle is valid");
		}
		else
		{
			System.out.println("The triangle is invalid");
		}
		Node graph = f.getExplanation(NthOutput.FIRST);
		display(graph);
	}
	
	/**
	 * Determines if a vector contains the lengths of sides of a valid triangle.
	 * This global circuit itself refers to multiple other circuits (also defined
	 * in this file: {@link AllNumbers}, {@link TriangleInequality},
	 * {@link AllPositive}). If one expands all these circuits, it results in the
	 * following graphical representation:
	 * <p>
	 * <img src="{@docRoot}/doc-files/circuits/Triangle.IsValidTriangle.png" alt="Circuit">
	 * <p>
	 * The input of the circuit is the pin labeled "A", and its output is "B".
	 */
	public static class IsValidTriangle extends Circuit
	{
		public IsValidTriangle()
		{
			super(1, 1, "Triangle?");
			Fork f1 = new Fork(4);
			associateInput(0, f1.getInputPin(0));
			And and = new And(4, true);
			GetSize size = new GetSize();
			Equals eq_3 = new Equals();
			Constant three = new Constant(3);
			connect(f1, 0, size, 0);
			connect(size, 0, eq_3, 0);
			connect(three, 0, eq_3, 1);
			connect(eq_3, 0, and, 0);
			AllNumbers numbers = new AllNumbers();
			connect(f1, 1, numbers, 0);
			connect(numbers, 0, and, 1);
			AllPositive positive = new AllPositive();
			connect(f1, 2, positive, 0);
			connect(positive, 0, and, 2);
			Fork f3 = new Fork(3);
			connect(f1, 3, f3, 0);
			TriangleInequality te_1 = new TriangleInequality(0, 1, 2);
			TriangleInequality te_2 = new TriangleInequality(0, 2, 1);
			TriangleInequality te_3 = new TriangleInequality(1, 2, 0);
			connect(f3, 0, te_1, 0);
			connect(f3, 1, te_2, 0);
			connect(f3, 2, te_3, 0);
			And and2 = new And(3);
			connect(te_1, 0, and2, 0);
			connect(te_2, 0, and2, 1);
			connect(te_3, 0, and2, 2);
			connect(and2, 0, and, 3);
			associateOutput(0, and.getOutputPin(0));
			addNodes(f1, size, three, eq_3, numbers, positive, f3, te_1, te_2, te_3, and2, and);
		}
	}
	
	/**
	 * Determines if all three elements of of vector are positive numbers.
	 * Graphically, the circuit can be represented as:
	 * <p>
	 * <img src="{@docRoot}/doc-files/circuits/Triangle.AllNumbers.png" alt="Circuit">
	 */
	protected static class AllNumbers extends Circuit
	{
		public AllNumbers()
		{
			super(1, 1, "Num");
			Fork f = new Fork(3);
			associateInput(0, f.getInputPin(0));
			ElementAt get_1 = new ElementAt(0);
			ElementAt get_2 = new ElementAt(1);
			ElementAt get_3 = new ElementAt(2);
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			InstanceOf isnum_1 = new InstanceOf(Number.class);
			InstanceOf isnum_2 = new InstanceOf(Number.class);
			InstanceOf isnum_3 = new InstanceOf(Number.class);
			connect(get_1, 0, isnum_1, 0);
			connect(get_2, 0, isnum_2, 0);
			connect(get_3, 0, isnum_3, 0);
			And and_1 = new And(3);
			connect(isnum_1, 0, and_1, 0);
			connect(isnum_2, 0, and_1, 1);
			connect(isnum_3, 0, and_1, 2);
			associateOutput(0, and_1.getOutputPin(0));
			addNodes(f, get_1, get_2, get_3, isnum_1, isnum_2, isnum_3, and_1);
		}		
	}
	
	/**
	 * Verifies the triangle inequality on a vector of three numbers. The
	 * circuit is parameterized by three values x, y and z, determining which
	 * two elements of the vector must be compared to the remaining one. The
	 * circuit can be represented graphically as:
	 * <p>
	 * <img src="{@docRoot}/doc-files/circuits/Triangle.TriangleInequality.png" alt="Circuit">
	 */
	protected static class TriangleInequality extends Circuit
	{
		/**
		 * The first side of the triangle.
		 */
		protected int m_x;
		
		/**
		 * The second side of the triangle.
		 */
		protected int m_y;
		
		/**
		 * The third side of the triangle.
		 */
		protected int m_z;
		
		public TriangleInequality(int x, int y, int z)
		{
			super(1, 1, "Triangle inequality");
			m_x = x;
			m_y = y;
			m_z = z;
			Fork f = new Fork(3);
			associateInput(0, f.getInputPin(0));
			ElementAt get_1 = new ElementAt(x);
			ElementAt get_2 = new ElementAt(y);
			ElementAt get_3 = new ElementAt(z);
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			Addition plus = new Addition(2);
			connect(get_1, 0, plus, 0);
			connect(get_2, 0, plus, 1);
			IsGreaterThan gt = new IsGreaterThan();
			connect(plus, 0, gt, 0);
			connect(get_3, 0, gt, 1);
			associateOutput(0, gt.getOutputPin(0));
			addNodes(f, get_1, get_2, get_3, plus, gt);
		}
		
		@Override
		public TriangleInequality duplicate(boolean with_state)
		{
			TriangleInequality t = new TriangleInequality(m_x, m_y, m_z);
			copyInto(t, with_state);
			return t;
		}
	}
	
	/**
	 * Determines if the first three elements of a vector are positive. The
	 * circuit can be represented graphically as:
	 * <p>
	 * <img src="{@docRoot}/doc-files/circuits/Triangle.AllPositive.png" alt="Circuit">
	 *
	 */
	protected static class AllPositive extends Circuit
	{
		public AllPositive()
		{
			super(1, 1, "&forall;&gt;0");
			Fork f = new Fork(3);
			associateInput(0, f.getInputPin(0));
			ElementAt get_1 = new ElementAt(0);
			ElementAt get_2 = new ElementAt(1);
			ElementAt get_3 = new ElementAt(2);
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			IsPositive isnum_1 = new IsPositive();
			IsPositive isnum_2 = new IsPositive();
			IsPositive isnum_3 = new IsPositive();
			connect(get_1, 0, isnum_1, 0);
			connect(get_2, 0, isnum_2, 0);
			connect(get_3, 0, isnum_3, 0);
			And and_1 = new And(3);
			connect(isnum_1, 0, and_1, 0);
			connect(isnum_2, 0, and_1, 1);
			connect(isnum_3, 0, and_1, 2);
			associateOutput(0, and_1.getOutputPin(0));
			addNodes(f, get_1, get_2, get_3, isnum_1, isnum_2, isnum_3, and_1);
		}
	}
	
	/**
	 * Function that determines if a number is positive. The
	 * circuit can be represented graphically as:
	 * <p>
	 * <img src="{@docRoot}/doc-files/circuits/Triangle.IsPositive.png" alt="Circuit">
	 */
	protected static class IsPositive extends Circuit
	{
		public IsPositive()
		{
			super(1, 1, "&gt;0");
			IsGreaterThan gt = new IsGreaterThan();
			associateInput(0, gt.getInputPin(0));
			Constant zero = new Constant(0);
			connect(zero, 0, gt, 1);
			associateOutput(0, gt.getOutputPin(0));
			addNodes(gt, zero);
		}		
	}
}
