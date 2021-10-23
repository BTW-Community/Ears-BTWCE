package com.unascribed.ears.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import com.unascribed.ears.common.EarsFeatures.Alfalfa;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.util.Slice;

public class AlfalfaTest {

	public static void main(String[] args) throws IOException {
		System.setProperty("ears.debug", "true");
		System.setProperty("ears.debug.stdout", "true");
		Map<String, Slice> data = new HashMap<String, Slice>();
		byte[] bys = new byte[23];
		ThreadLocalRandom.current().nextBytes(bys);
		data.put("wing", new Slice(bys));
		Alfalfa al = new Alfalfa(1, data);
		BufferedImage img = ImageIO.read(new File("alfalfa-test-in.png"));
		AWTEarsImage ears = new AWTEarsImage(img);
		al.write(ears);
		ImageIO.write(img, "PNG", new File("alfalfa-test-out.png"));
		
		img = ImageIO.read(new File("alfalfa-test-out.png"));
		ears = new AWTEarsImage(img);
		Alfalfa read = Alfalfa.read(ears);
		System.out.println("original = "+al);
		System.out.println("reparsed = "+read);
		if (Objects.equals(al, read)) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILURE");
		}
	}
	
}
