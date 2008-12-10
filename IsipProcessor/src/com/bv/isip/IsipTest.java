package com.bv.isip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import com.bv.core.message.MessageManager;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.abs.gui.MainWindowInterface;
import com.bv.isis.console.abs.processor.ProcessorInterface;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.processor.NonEditableTextArea;
import com.bv.isis.console.processor.NonEditableTextPane;
import com.bv.isis.console.processor.ProcessorFrame;

@SuppressWarnings("serial")
public class IsipTest extends ProcessorFrame {

    /**
     * Isip contructor
     *
     * @param  closeable
     */
	public IsipTest() {
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipTest", "IsipTest");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
	
	

	public ProcessorInterface duplicate() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new IsipTest();
	}

	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "getDescription");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return MessageManager.getMessage("&DisplayProcessorDescription");
	}
	
	public void run (
			MainWindowInterface windowInterface,
			JMenuItem menuItem,
			String parameters,
			String preprocessing,
			String postprocessing,
			DefaultMutableTreeNode selectedNode
			)
			throws
				InnerException
	{
		
		super.run(windowInterface, menuItem, postprocessing, postprocessing, postprocessing, selectedNode);
		setTitle(menuItem.getText());
		makePanel();
		display();
	}
	
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipProcessor", "makePanel");
		int background = Color.white.getRGB();
		int foreground = Color.black.getRGB();
		Color foreground_color = null;
		Color background_color = null;

		trace_methods.beginningOfMethod();
		// Le layout manager du panneau est un BorderLayout
		getContentPane().setLayout(new BorderLayout());
		// On récupère les données de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("Execution");
			background = preferences.getInt("Background");
			foreground = preferences.getInt("Output");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		background_color = new Color(background);
		// La couleur d'avant plan est l'inverse de la couleur de fond
		foreground_color = new Color(foreground);
		// On construit la zone d'affichage du fichier
		NonEditableTextPane textArea = new NonEditableTextPane(new DefaultStyledDocument());
		textArea.setCaretPosition(0);
		textArea.setFont((new NonEditableTextArea()).getFont());
		textArea.setContentType("text/plain");
		// On fixe les couleurs
		textArea.setBackground(background_color);
		textArea.setForeground(foreground_color);
		textArea.setSelectedTextColor(background_color);
		textArea.setSelectionColor(foreground_color);
		// On place la zone dans un scroll pane
		JScrollPane output_scroll = new JScrollPane(textArea);
		getContentPane().add(output_scroll, BorderLayout.CENTER);
		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Events_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// On redimensionne la fenêtre
		setSize(400, 400);
		trace_methods.endOfMethod();
	}

}
