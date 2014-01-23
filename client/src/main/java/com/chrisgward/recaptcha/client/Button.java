package com.chrisgward.recaptcha.client;

import javax.swing.*;
import java.awt.*;

abstract class Button extends JButton {
	public Button() {
		setMinimumSize(new Dimension(25, 17));
		setMaximumSize(new Dimension(25, 17));
		setPreferredSize(new Dimension(25, 17));
		setSize(new Dimension(25, 17));
	}

	public abstract Image getImage();

	@Override
	public void paint(Graphics graphics) {
		graphics.drawImage(getImage(), 0, 0, null);
	}
}
