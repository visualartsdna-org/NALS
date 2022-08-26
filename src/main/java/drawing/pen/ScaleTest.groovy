package drawing.pen

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ScaleTest {
	
	def ifile = "C:/test/archivo/reviewed/SemanticSensorNetworkOntology.jpg"

	@Test
	void test() {
		def sbi = ImageIO.read(new File(ifile));
		def h = sbi.getHeight()
		def w = sbi.getWidth()
		def bi = scale(sbi, BufferedImage.TYPE_INT_RGB, w/4 as int, h/4 as int, 0.25, 0.25)
		File img = new File("test.jpg")
		ImageIO.write(bi, "JPEG", img);
	}

	public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if(sbi != null) {
		  dbi = new BufferedImage(dWidth, dHeight, imageType);
		  java.awt.Graphics2D g = dbi.createGraphics();
		  AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
		  g.drawRenderedImage(sbi, at);
		}
		return dbi;
	  }
	
}
