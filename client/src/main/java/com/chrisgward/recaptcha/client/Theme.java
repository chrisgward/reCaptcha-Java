package com.chrisgward.recaptcha.client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public enum Theme {
	RED, WHITE, BLACKGLASS;

	BufferedImage back;
	BufferedImage reloadButton;
	BufferedImage audioChallenge;
	BufferedImage textChallenge;
	BufferedImage helpButton;

	private boolean loaded;

	public Image getBack() {
		if (!loaded) {
			load();
		}
		return back;
	}

	public Image getReloadButton() {
		if (!loaded) {
			load();
		}
		return reloadButton;
	}

	public Image getAudioChallenge() {
		if (!loaded) {
			load();
		}
		return audioChallenge;
	}

	public Image getTextChallenge() {
		if (!loaded) {
			load();
		}
		return textChallenge;
	}

	public Image getHelpButton() {
		if (!loaded) {
			load();
		}
		return helpButton;
	}
	private void load() {
		try {
			String urlBase = "http://www.google.com/recaptcha/api/img/" + name().toLowerCase();
			BufferedImage bufferedImage = ImageIO.read(new URL(urlBase + "/sprite.png"));
			back = new BufferedImage(318, 129, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = (Graphics2D)back.getGraphics();
			graphics2D.drawImage(bufferedImage.getSubimage(0, 63, 318, 9), 0, 0, null);
			graphics2D.drawImage(bufferedImage.getSubimage(18, 0, 9, 57), 0, 9, null);
			graphics2D.drawImage(bufferedImage.getSubimage(27, 0, 9, 57), 309, 9, null);
			graphics2D.drawImage(bufferedImage.getSubimage(0, 0, 9, 63), 0, 66, null);
			graphics2D.drawImage(bufferedImage.getSubimage(18, 57, 300, 6), 9, 66, null);
			graphics2D.drawImage(bufferedImage.getSubimage(9, 0, 9, 63), 309, 66, null);
			graphics2D.drawImage(bufferedImage.getSubimage(43, 0, 171, 49), 9, 72, null);
			graphics2D.drawImage(bufferedImage.getSubimage(36, 0, 7, 57), 180, 72, null);
			graphics2D.drawImage(bufferedImage.getSubimage(214, 0, 97, 57), 212, 72, null);
			graphics2D.drawImage(bufferedImage.getSubimage(43, 49, 171, 8), 9, 121, null);
			graphics2D.drawImage(bufferedImage.getSubimage(43, 49, 25, 8), 187, 121, null);
			reloadButton = ImageIO.read(new URL(urlBase + "/refresh.gif"));
			audioChallenge = ImageIO.read(new URL(urlBase + "/audio.gif"));
			textChallenge = ImageIO.read(new URL(urlBase + "/text.gif"));
			helpButton = ImageIO.read(new URL(urlBase + "/help.gif"));
			loaded = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
