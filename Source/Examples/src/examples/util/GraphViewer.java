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
package examples.util;

import java.awt.FlowLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.function.LineageDotRenderer;

/**
 * Utility methods to render and display explanation graphs.
 */
public class GraphViewer
{
	/**
	 * Displays an explanation graph into a window. This method acts as a
	 * primitive image viewer, used to display the result of the examples.
	 * @param graph The graph to display
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 */
	public static void display(Node graph, boolean no_captions)
	{
		BitmapJFrame window = new BitmapJFrame(getGraph(graph, no_captions));
		window.setVisible(true);
	}
	
	/**
	 * Displays an explanation graph into a window. This method acts as a
	 * primitive image viewer, used to display the result of the examples.
	 * @param graph The graph to display
	 */
	public static void display(Node graph)
	{
		display(graph, false);
	}
	
	/**
	 * Saves a graph to a file.
	 * @param graph The graph to display
	 * @param filename The file where this graph will be saved
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 */
	public static void save(Node graph, String filename, boolean no_captions)
	{
		File outputFile = new File(filename);
		try (FileOutputStream outputStream = new FileOutputStream(outputFile))
		{
		    outputStream.write(getGraph(graph, no_captions));
		    outputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves a graph to a file.
	 * @param graph The graph to display
	 * @param filename The file where this graph will be saved
	 */
	public static void save(Node graph, String filename)
	{
		save(graph, filename, false);
	}
	
	/**
	 * Renders a graph, calls DOT in the background and retrieves the binary
	 * image it produces.
	 * @param graph The graph to render
	 * @return An array of bytes containing the image to display
	 */
	protected static byte[] getGraph(Node graph, boolean no_captions)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		LineageDotRenderer renderer = new LineageDotRenderer(graph);
		renderer.setNoCaptions(no_captions);
		renderer.render(ps);
		CommandRunner runner = new CommandRunner(new String[] {"dot", "-Tpng"}, baos.toString());
		runner.run();
		return runner.getBytes();
	}
		
	/**
	 * Receives a byte array as an input, and shows it in a Swing
	 * window as a picture.
	 */
	public static class BitmapJFrame extends JFrame
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 1L;

		protected transient JFrame m_frame;

		protected transient JLabel m_label;

		public BitmapJFrame(byte[] image_bytes)
		{
			super("Graph");
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			add(panel);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			m_label = new JLabel();
			panel.add(m_label);
			ImageIcon icon = new ImageIcon(image_bytes); 
			m_label.setIcon(icon);
			setSize(icon.getIconWidth(), icon.getIconHeight());
		}
		
		/**
		 * Gets the frame associated to the object
		 * @return The frame
		 */
		public JFrame getFrame()
		{
		  return m_frame;
		}
	}
}
