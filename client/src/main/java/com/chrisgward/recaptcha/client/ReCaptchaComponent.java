package com.chrisgward.recaptcha.client;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.swing.FocusManager;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static javax.swing.SpringLayout.*;

public class ReCaptchaComponent extends JComponent {
	private static XPathExpression expression;
	private static Tidy tidy;
	private static DocumentBuilder builder;
	static {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			expression = xpath.compile("//*[@id=\"recaptcha_challenge_field\"]/@value");
		} catch (Exception e) {
			e.printStackTrace();
			expression = null;
		}
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowErrors(0);
		tidy.setShowWarnings(false);
		tidy.setXmlOut(true);

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
			builder = null;
		}
	}

	private String publicKey;
	private String challengeField;
	private Image image;
	private Theme theme;
	private JButton refresh = new Button() {
		@Override
		public Image getImage() {
			return theme.getReloadButton();
		}
	};
	private JButton textChallenge = new Button() {
		@Override
		public Image getImage() {
			return theme.getTextChallenge();
		}
	};
	private JButton audioChallenge = new Button() {
		@Override
		public Image getImage() {
			return theme.getAudioChallenge();
		}
	};
	private JButton help  = new Button() {
		@Override
		public Image getImage() {
			return theme.getHelpButton();
		}
	};
	private JTextField input = new JTextField() {
		@Override
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);

			if(getText().isEmpty() && FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != this){
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setBackground(Color.gray);
				g2.drawString("Type the text", 5, 12);
				g2.dispose();
			}
		}
	};
	private JLabel privacyAndTerms = new JLabel("Privacy & Terms");

	SpringLayout layout;
	public ReCaptchaComponent(String publicKey) {
		this(publicKey, Theme.RED);
	}

	public ReCaptchaComponent(String publicKey, Theme theme) {
		this.publicKey = publicKey;
		refresh();

		setMinimumSize(new Dimension(318, 129));
		setMaximumSize(new Dimension(318, 129));
		setPreferredSize(new Dimension(318, 129));
		setSize(new Dimension(318, 129));
		setTheme(theme);

		layout = new SpringLayout();
		setLayout(layout);

		layout.putConstraint(NORTH, refresh, 72, NORTH, this);
		layout.putConstraint(WEST, refresh, 187, WEST, this);
		add(refresh);
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				refresh();
			}
		});

		layout.putConstraint(NORTH, audioChallenge, 0, SOUTH, refresh);
		layout.putConstraint(WEST, audioChallenge, 0, WEST, refresh);
		add(audioChallenge);
		audioChallenge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				layout.putConstraint(NORTH, textChallenge, 0, SOUTH, refresh);
				layout.putConstraint(WEST, textChallenge, 0, WEST, refresh);
				layout.putConstraint(NORTH, help, -1, SOUTH, textChallenge);
				layout.putConstraint(WEST, help, 0, WEST, textChallenge);
				remove(audioChallenge);
				add(textChallenge);
				validate();
			}
		});

		textChallenge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				remove(textChallenge);
				layout.putConstraint(NORTH, audioChallenge, 0, SOUTH, refresh);
				layout.putConstraint(WEST, audioChallenge, 0, WEST, refresh);
				layout.putConstraint(NORTH, help, -1, SOUTH, audioChallenge);
				layout.putConstraint(WEST, help, 0, WEST, audioChallenge);
				add(audioChallenge);
				validate();
			}
		});

		layout.putConstraint(NORTH, help, -1, SOUTH, audioChallenge);
		layout.putConstraint(WEST, help, 0, WEST, audioChallenge);
		add(help);
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.google.com/recaptcha/help"));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});

		input.setPreferredSize(new Dimension(153, 18));
		layout.putConstraint(NORTH, input, 83, NORTH, this);
		layout.putConstraint(WEST, input, 16, WEST, this);
		add(input);

		layout.putConstraint(NORTH, privacyAndTerms, 3, SOUTH, input);
		layout.putConstraint(EAST, privacyAndTerms, 0, EAST, input);
		privacyAndTerms.setFont(privacyAndTerms.getFont().deriveFont(9f));
		privacyAndTerms.setForeground(Color.BLUE);
		privacyAndTerms.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.google.com/intl/en/policies/"));
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});
		add(privacyAndTerms);
	}

	public void setTheme(Theme theme) {
		this.theme = theme;

	}

	public synchronized void refresh() {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			tidy.parse((new URL("http://www.google.com/recaptcha/api/noscript?k=" + publicKey)).openStream(), buffer);
			String tidied = new String(buffer.toByteArray());
			Document document = builder.parse(new InputSource(new StringReader(tidied)));
			this.challengeField = (String)expression.evaluate(document, XPathConstants.STRING);
			this.image = ImageIO.read(new URL("http://www.google.com/recaptcha/api/image?c=" + challengeField));
			input.setText("");
			repaint();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void paint(Graphics graphics) {
		graphics.drawImage(theme.getBack(), 0, 0, null);
		graphics.drawImage(image, 9, 9, null);
		super.paint(graphics);
	}

	public String getChallengeField() {
		return challengeField;
	}

	public String getText() {
		return input.getText();
	}
}
