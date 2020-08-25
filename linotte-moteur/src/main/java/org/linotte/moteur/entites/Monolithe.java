package org.linotte.moteur.entites;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Monolithe implements Comparable<Monolithe> {

	private Shape shape;

	private Color color;

	private int position;

	private BufferedImage image;

	public Monolithe(Shape shape, Color color, int position, BufferedImage image) {
		super();
		this.shape = shape;
		this.color = color;
		this.position = position;
		this.image = image;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape area) {
		this.shape = area;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int compareTo(Monolithe o) {
		if (o.getPosition() == getPosition())
			return 0;
		if (o.getPosition() < getPosition())
			return 1;
		else
			return -1;
	}

}
