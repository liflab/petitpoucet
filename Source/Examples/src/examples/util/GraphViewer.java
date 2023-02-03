/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.function.LineageDotRenderer;

/**
 * Utility methods to render and display explanation graphs.
 */
public class GraphViewer
{
	/**
	 * Private constructor.
	 */
	private GraphViewer()
	{
		super();
	}

	/**
	 * Displays an explanation graph into a window. This method acts as a
	 * primitive image viewer, used to display the result of the examples.
	 * @param roots The roots of the graph to display
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 */
	public static void display(List<Node> roots, boolean no_captions)
	{
		BitmapJFrame window = new BitmapJFrame(getGraph(roots, no_captions));
		window.setVisible(true);
	}

	/**
	 * Displays an explanation graph into a window. This method acts as a
	 * primitive image viewer, used to display the result of the examples.
	 * @param roots The roots of the graph to display
	 */
	public static void display(List<Node> roots)
	{
		display(roots, false);
	}

	/**
	 * Displays an explanation graph into a window. This method acts as a
	 * primitive image viewer, used to display the result of the examples.
	 * @param root The root of the graph to display
	 */
	public static void display(Node root)
	{
		display(Arrays.asList(root), false);
	}

	/**
	 * Saves a graph to a file.
	 * @param roots The roots of the graph to display
	 * @param filename The file where this graph will be saved
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 * @throws IOException If the file operation did not succeed
	 */
	public static void save(List<Node> roots, String filename, boolean no_captions) throws IOException
	{
		File outputFile = new File(filename);
		FileOutputStream outputStream = null;
		try
		{
			outputStream = new FileOutputStream(outputFile);
			outputStream.write(getGraph(roots, no_captions));
			outputStream.close();
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			outputStream.close();
		}
	}

	/**
	 * Saves a graph to a file.
	 * @param root The root of the graph to display
	 * @param filename The file where this graph will be saved
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 * @throws IOException If the file operation did not succeed
	 */
	public static void save(Node root, String filename, boolean no_captions) throws IOException
	{
		save(Arrays.asList(root), filename, no_captions);
	}

	/**
	 * Saves a graph to a file.
	 * @param roots The roots of the graph to display
	 * @param filename The file where this graph will be saved
	 * @throws IOException If the file operation did not succeed
	 */
	public static void save(List<Node> roots, String filename) throws IOException
	{
		save(roots, filename, false);
	}

	/**
	 * Renders a graph as a DOT file.
	 * @param roots The roots of the graph to render
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 * @return A string with the contents of the DOT file
	 */
	public static String toDot(List<Node> roots, boolean no_captions)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		LineageDotRenderer renderer = new LineageDotRenderer(roots);
		renderer.setNoCaptions(no_captions);
		renderer.render(ps);
		return baos.toString();
	}

	/**
	 * Renders a graph, calls DOT in the background and retrieves the binary
	 * image it produces.
	 * @param roots The roots of the graph to render
	 * @param no_captions Set to {@code true} to hide non-leaf captions
	 * @return An array of bytes containing the image to display
	 */
	protected static byte[] getGraph(List<Node> roots, boolean no_captions)
	{
		String graph = toDot(roots, no_captions);
		CommandRunner runner = new CommandRunner(new String[] {"dot", "-Tpng"}, graph);
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

		protected transient JPanel m_panel;

		protected transient JLabel m_label;
		
		protected transient ImageIcon m_imageIcon;

		public BitmapJFrame(byte[] image_bytes)
		{
			super("Graph");
			m_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			add(m_panel);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			m_label = new JLabel();
			m_panel.add(m_label);
			m_imageIcon = new ImageIcon(image_bytes); 
			m_label.setIcon(m_imageIcon);
			//setSize(icon.getIconWidth(), icon.getIconHeight());
			pack();
			addComponentListener(new ResizeListener());
		}

		/**
		 * Gets the panel associated to the object
		 * @return The panel
		 */
		public JPanel getPanel()
		{
			return m_panel;
		}

		protected class ResizeListener implements ComponentListener
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
				// Do nothing
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				// Do nothing
			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				int w = m_panel.getWidth();
				int h = m_panel.getHeight();
				if (w > 0 && h > 0)
				{
					m_label.setIcon(new ImageIcon(m_imageIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH)));
				}
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				// Do nothing
			}
		}
	}
}
