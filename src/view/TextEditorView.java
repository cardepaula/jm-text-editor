package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import core.JMPortugueseDictionary;

public class TextEditorView extends JFrame {

//	private String userHomeDirectory = System.getProperty("user.home");
	private JMPortugueseDictionary portugueseDictionary = null;

	public TextEditorView(JMPortugueseDictionary portugueseDictionary) {
		this.portugueseDictionary = portugueseDictionary;
		this.textEditor();
	}

	public void textEditor() {
		
		SwingUtilities.invokeLater(() -> {
			final JTextPane pane = new JTextPane();
			final Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			final StyledDocument doc = pane.getStyledDocument();

			pane.addMouseListener(new MouseAdapter() {
				private boolean pendingPopUp = false;

				private void pop(final MouseEvent mevt) {
					if (SwingUtilities.isRightMouseButton(mevt)) {
						try {
							final StyledDocument doc = pane.getStyledDocument();

							final int offset = pane.viewToModel2D(mevt.getPoint());

							final int start = Utilities.getWordStart(pane, offset),
									end = Utilities.getWordEnd(pane, offset);

							pane.setSelectionStart(start);
							pane.setSelectionEnd(end);

							final String word = doc.getText(start, end - start);

							final JPanel popupPanel = new JPanel();

							LinkedList<String> words2 =portugueseDictionary.getSugestions(word);
							final int cnt = words2.size();
							final ArrayList<JButton> words = new ArrayList<>();
							for (int i = 0; i < cnt; ++i) {
								final JButton button = new JButton(words2.get(i));
								popupPanel.add(button);
								words.add(button);
							}
							final JButton cancel = new JButton("x");
							popupPanel.add(cancel);

							final Popup popup = PopupFactory.getSharedInstance().getPopup(pane, popupPanel,
									mevt.getXOnScreen(), mevt.getYOnScreen());

							words.forEach(button -> button.addActionListener(e -> {
								try {
									final String newWord = ((JButton) e.getSource()).getText();

									doc.remove(start, end - start);
									doc.insertString(start, newWord, null);

									pane.setCaretPosition(start + newWord.length());
								} catch (final BadLocationException | RuntimeException x) {
									JOptionPane.showMessageDialog(pane, "Oups!");
								} finally {
									popup.hide();
									pendingPopUp = false;
								}
							}));

							cancel.addActionListener(e -> {
								popup.hide();
								pane.setSelectionStart(offset);
								pane.setSelectionEnd(offset);
								pendingPopUp = false;
							});

							pendingPopUp = true;
							popup.show();
						} catch (final BadLocationException | RuntimeException x) {
							JOptionPane.showMessageDialog(pane, "Oups! No word found?...");
						}
					}
				}

				private void maybePop(final MouseEvent mevt) {
					if (mevt.isPopupTrigger()) {
						if (pendingPopUp)
							System.err.println("A popup is already popped. Close it to pop a new one.");
						else
							pop(mevt);
					}
				}

				@Override
				public void mouseClicked(final MouseEvent mevt) {
					maybePop(mevt);
				}

				@Override
				public void mousePressed(final MouseEvent mevt) {
					maybePop(mevt);
				}

				@Override
				public void mouseReleased(final MouseEvent mevt) {
					maybePop(mevt);
				}
			});

			doc.addDocumentListener(new DocumentListener() {
				private void clearStyle(final DocumentEvent e, int start, int end) {
					SwingUtilities
							.invokeLater(() -> doc.setCharacterAttributes(start, end - start, defaultStyle, true));
				}

				@Override
				public void insertUpdate(final DocumentEvent e) {
//					System.out.println(checkLastWord(e));
					checkLastWord(e);
				}

				@Override
				public void removeUpdate(final DocumentEvent e) {
//					System.out.println(checkLastWord(e));
					checkLastWord(e);
				}

				@Override
				public void changedUpdate(final DocumentEvent e) {					
//					System.out.println(checkLastWord(e));
					checkLastWord(e);
				}
				
				protected String checkLastWord(final DocumentEvent e) {
					try {
						final SimpleAttributeSet sas = new SimpleAttributeSet();
						StyleConstants.setUnderline(sas, true);
						final int start = Utilities.getWordStart(pane, pane.getCaretPosition());
						final int end = Utilities.getWordEnd(pane, pane.getCaretPosition());
						String text = pane.getDocument().getText(start, end - start);
						
						if (text.matches("^[a-zA-Z\\u00C0-\\u00FF]+$")) {
							if (!portugueseDictionary.hasPortugueseWord(text)) {
								SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(start, end - start, sas, true));							
							} else {
								clearStyle(e, start, end);
							}							
						}
						return text;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return null;
				}
				
			});


			final JFrame frame = new JFrame("JMTextEditor");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new JScrollPane(pane));

			final Dimension dim = frame.getPreferredSize();
			dim.width += 500;
			dim.height += 500;
			frame.setPreferredSize(dim);

			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}

}
