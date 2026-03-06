package ca.uqac.lif.petitpoucet.examples.number;

import static ca.uqac.lif.petitpoucet.examples.GraphViewer.display;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Circuit;
import ca.uqac.lif.petitpoucet.circuit.Constant;
import ca.uqac.lif.petitpoucet.circuit.Numbers;

/**
 * Evaluates a circuit corresponding to the function (x+y)×z. Graphically, this
 * circuit can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit.png" alt="Circuit">
 * 
 * <h3>Explanation</h3>
 * 
 * The program evaluates this circuit on the input x=2, y=3, z=0, and then
 * requests the explanation graph corresponding to the resulting output.
 * 
 * <h4>Full graph</h4>
 * The full explanation graph corresponding to that explanation is the
 * following. 
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit2-full.png" alt="Full graph">
 * <p>
 * One can see that the circuit's output depends only on the value of z.
 * 
 * <h4>Simplified graph</h4>
 * 
 * The simplified graph only keeps the leaves and intermediate Boolean nodes:
 * <p>
 * <img src="{@docRoot}/doc-files/number/ArithmeticCircuit2-small.png" alt="Simplified graph">
 * 
 * @author Sylvain Hallé
 * @see ArithmeticCircuit1
 * @see ArithmeticCircuit3
 */
public class ArithmeticCircuit2
{
	public static void main(String[] args) throws ExplanationException
	{
		Circuit c = new Circuit(3, 1, "(x+y)×z");
		Numbers.Addition a = new Numbers.Addition(2);
		Numbers.Multiplication m = new Numbers.Multiplication(2);
		c.add(a, m);
		c.associateInput(0, a, 0);
		c.associateInput(1, a, 1);
		c.associateInput(2, m, 1);
		Connectable.connect(a, 0, m, 0);
		c.associateOutput(0, m, 0);
		Connectable.connect(new Constant(2), 0, c, 0);
		Connectable.connect(new Constant(3), 0, c, 1);
		Connectable.connect(new Constant(0), 0, c, 2);
		Number result = (Number) c.compute();
		System.out.println(result);
		AbstractVertex full_graph = c.explain(new OutputPart(0));
		display(full_graph);
	}

}
