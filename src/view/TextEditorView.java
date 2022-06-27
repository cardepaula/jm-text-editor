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

public class TextEditorView extends JFrame implements ActionListener {

	private String userHomeDirectory = System.getProperty("user.home");
	private JMPortugueseDictionary portugueseDictionary = null;

	JTextPane textArea;
	JScrollPane scrollPane;
	JSpinner fontSizeSpinner;
	JLabel fontSizeSpinnerLabel;
	JButton fontColorButton;
	JComboBox fontBox;
	JMenuBar menubar;
	JMenu fileMenu;
	JMenuItem openFileMenuItem;
	JMenuItem saveFileMenuItem;
	JMenuItem exitMenuItem;

	public TextEditorView(JMPortugueseDictionary portugueseDictionary) {
		this.portugueseDictionary = portugueseDictionary;
		this.teste();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.fontColorButton) {
			Color color = JColorChooser.showDialog(null, "Escolha uma cor", Color.black);
			this.textArea.setForeground(color);
		}

		if (e.getSource() == this.fontBox) {
			this.textArea
					.setFont(new Font((String) fontBox.getSelectedItem(), Font.PLAIN, textArea.getFont().getSize()));
		}

		if (e.getSource() == this.openFileMenuItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT", "txt");
			fileChooser.setFileFilter(filter);

			int response = fileChooser.showSaveDialog(null);

			if (response == JFileChooser.APPROVE_OPTION) {
				File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
				Scanner fileIn = null;

				try {
					fileIn = new Scanner(file);
					if (file.isFile()) {
						while (fileIn.hasNextLine()) {
							String line = fileIn.nextLine() + "\n";
//							this.textArea.append(line);
						}
					}

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					fileIn.close();
				}
			}
		}

		if (e.getSource() == this.saveFileMenuItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(this.userHomeDirectory));

			int response = fileChooser.showSaveDialog(null);

			if (response == JFileChooser.APPROVE_OPTION) {
				File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
				PrintWriter fileOut = null;

				try {
					fileOut = new PrintWriter(file);
					fileOut.println(textArea.getText());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} finally {
					fileOut.close();
				}
			}
		}

		if (e.getSource() == this.exitMenuItem) {
			System.exit(0);
		}

	}

	public void teste() {
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

							final int offset = pane.viewToModel(mevt.getPoint());

							final int start = Utilities.getWordStart(pane, offset),
									end = Utilities.getWordEnd(pane, offset);

							pane.setSelectionStart(start);
							pane.setSelectionEnd(end);

							final String word = doc.getText(start, end - start);

							final JPanel popupPanel = new JPanel();

							final int cnt = 4;
							final ArrayList<JButton> words = new ArrayList<>();
							for (int i = 0; i < cnt; ++i) {
								final JButton button = new JButton(word + (i + 1));
								popupPanel.add(button);
								words.add(button);
							}
							final JButton cancel = new JButton("Cancel");
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
				private void clearStyle(final DocumentEvent e) {
					SwingUtilities
							.invokeLater(() -> doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true));
				}

				@Override
				public void insertUpdate(final DocumentEvent e) {
					System.out.println(checkLastWord(e));
				}

				@Override
				public void removeUpdate(final DocumentEvent e) {
					System.out.println(checkLastWord(e));
				}

				@Override
				public void changedUpdate(final DocumentEvent e) {					
					System.out.println(checkLastWord(e));
				}
				
				protected String checkLastWord(final DocumentEvent e) {
					try {
						final SimpleAttributeSet sas = new SimpleAttributeSet();
						StyleConstants.setUnderline(sas, true);
						final int start = Utilities.getWordStart(pane, pane.getCaretPosition());
						final int end = Utilities.getWordEnd(pane, pane.getCaretPosition());
						String text = pane.getDocument().getText(start, end - start);
						
						if (portugueseDictionary.getPortugueseWords().get(text) == null) {
							SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(start, end - start, sas, true));							
						} else {
							clearStyle(e);
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