package ca.uqac.lif.petitpoucet.functions.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.DownstreamQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.UpstreamQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.Queryable;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.common.StringDesignator;

public class FileLines implements Function
{
	/**
	 * The name of the file to read from
	 */
	protected String m_filename;
	
	protected FileLinesQueryable m_queryable;
	
	/**
	 * Creates a new instance of the function
	 * @param filename
	 */
	public FileLines(String filename)
	{
		super();
		m_filename = filename;
		m_queryable = new FileLinesQueryable(filename);
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
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
		return m_queryable;
	}
	
	@Override
	public String toString()
	{
		return "File " + m_filename;
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function duplicate(boolean with_state) 
	{
		FileLines fl = new FileLines(m_filename);
		return fl;
	}

	@Override
	public Function duplicate()
	{
		return duplicate(false);
	}

	@Override
	public Queryable evaluate(Object[] inputs, Object[] outputs) 
	{
		return evaluate(inputs, outputs, null);
	}

	@Override
	public Class<?> getInputType(int index) 
	{
		return null;
	}

	@Override
	public Class<?> getOutputType(int index) 
	{
		return List.class;
	}

	@Override
	public int getInputArity() 
	{
		return 0;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}

	@Override
	public void reset() 
	{
		// Nothing to do
	}
	
	public static class FileLinesQueryable extends FunctionQueryable
	{
		public FileLinesQueryable(String reference)
		{
			super(reference, 0, 1);
		}
		
		@Override
		public List<TraceabilityNode> query(TraceabilityQuery q,  
				Designator d, TraceabilityNode root, Tracer factory)
		{
			if (q instanceof UpstreamQuery)
			{
				if (d.peek() instanceof NthOutput)
				{
					return queryOutput(q, ((NthOutput) d.peek()).getIndex(), d.tail(), root, factory);
				}
				return new ArrayList<TraceabilityNode>(0);
			}
			if (q instanceof DownstreamQuery)
			{
				return queryInput(q, 0, d, root, factory);
			}
			return factory.unknownLink(root);
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator d, TraceabilityNode root, Tracer factory)
		{
			Designator top = d.peek();
			Designator tail = d.tail();
			if (!(top instanceof NthElement))
			{
				return factory.unknownLink(root);
			}
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			int pos = ((NthElement) top).getIndex();
			ComposedDesignator cd = new ComposedDesignator(tail, new StringDesignator.NthLine(pos));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.EXACT);
			leaves.add(child);
			return leaves;
		}
		
		@Override
		protected List<TraceabilityNode> queryInput(TraceabilityQuery q, int in_index, 
				Designator d, TraceabilityNode root, Tracer factory)
		{
			Designator top = d.peek();
			Designator tail = d.tail();
			if (!(top instanceof StringDesignator.NthLine))
			{
				return factory.unknownLink(root);
			}
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			int pos = ((StringDesignator.NthLine) top).getIndex();
			ComposedDesignator cd = new ComposedDesignator(tail, new NthElement(pos), new NthOutput(0));
			TraceabilityNode child = factory.getObjectNode(cd, this);
			root.addChild(child, Quality.EXACT);
			leaves.add(child);
			return leaves;
		}
	}
}
