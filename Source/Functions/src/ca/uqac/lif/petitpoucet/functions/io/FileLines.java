package ca.uqac.lif.petitpoucet.functions.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.functions.NaryFunction;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.StringDesignator;

public class FileLines extends NaryFunction
{
	/**
	 * The name of the file to read from
	 */
	protected String m_filename;
	
	/**
	 * Creates a new instance of the function
	 * @param filename
	 */
	public FileLines(String filename)
	{
		super(0);
		m_filename = filename;
	}

	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		List<String> lines = new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(new File(m_filename));
			while (scanner != null && scanner.hasNextLine())
			{
				lines.add(scanner.nextLine());
			}
			scanner.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		outputs[0] = lines;
		m_returnedValue[0] = lines;
	}
	
	@Override
	protected void answerQuery(TraceabilityQuery q, int output_nb, Designator d,
			TraceabilityNode root, Tracer factory, List<TraceabilityNode> leaves)
	{
		Designator top = d.peek();
		Designator tail = d.tail();
		if (tail == null)
		{
			tail = Designator.identity;
		}
		if (!(top instanceof NthElement))
		{
			super.answerQuery(q, output_nb, d, root, factory, leaves);
			return;
		}
		int pos = ((NthElement) top).getIndex();
		ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.NthLine(pos));
		TraceabilityNode child = factory.getObjectNode(cd, this);
		root.addChild(child, Quality.EXACT);
		leaves.add(child);
	}
	
	@Override
	public String toString()
	{
		return "File " + m_filename;
	}
}
