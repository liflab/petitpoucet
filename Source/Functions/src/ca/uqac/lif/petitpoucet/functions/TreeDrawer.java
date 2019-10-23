package ca.uqac.lif.petitpoucet.functions;

import java.io.File;
import java.io.IOException;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.ConcreteTracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeRenderer.CaptionStyle;
import ca.uqac.lif.util.CommandRunner;
import ca.uqac.lif.util.FileHelper;

public class TreeDrawer 
{
	public static void drawTree(TraceabilityQuery q, Designator d, CaptionStyle with_labels, boolean flatten, String out_filename, Function f, Object ... inputs)
	{
		Object[] out = new Object[f.getOutputArity()];
		Queryable queryable = f.evaluate(inputs, out);
		ConcreteTracer tracer = new ConcreteTracer();
		ConcreteTraceabilityNode root = tracer.getTree(q, d, queryable);
		FunctionTraceabilityNodeRenderer renderer = new FunctionTraceabilityNodeRenderer();
		renderer.setFlatten(flatten);
		renderer.setShowCaptions(with_labels);
		String dot_code = renderer.render(root);
		try
		{
			runDot(dot_code, out_filename);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected static void runDot(String s_stdin, String out_filename) throws IOException
	{
		CommandRunner runner = new CommandRunner(new String[] {"dot", "-Tpng"}, s_stdin);
		runner.run();
		byte[] img_bytes = runner.getBytes();
		FileHelper.writeFromBytes(new File(out_filename), img_bytes);
	}
}
