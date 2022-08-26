/**
 * 
 */
package drawing.pen

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Font

/**
 * @author ricks
 * Implements the turtle graphics semantics
 * supporting the l-system grammar
 */
class Graphics implements IGraphics {

	// pen state
	def down = true
	static def color = Color.black
	def angle = 180
	def size = 4
	double x1 = 0
	double y1 = 0

	static def width=800,height=800
	//static def bgcolor = Color.white
	static def bgcolor = new Color(213,214,200,255)
	BufferedImage bi
	Graphics2D ig2
	def filename
	Font nodeFont = new Font ("Courier New", 1, 27)
	Font labelFont = new Font ("Courier New", 1, 27)
	def edgeLen	= 0// for locating edge labels
	def foundMark =0
	

	/**
	 * 
	 */
	public Graphics(h,w) {
		this(h,w, Graphics.bgcolor)
	}

	public Graphics(color) {
		this(Graphics.width, Graphics.height, color)
	}

	public Graphics() {
		this(Graphics.width, Graphics.height, Graphics.bgcolor)
	}

	public Graphics(width, height, bgcolor) {
		super()
		bgcolor = bgcolor
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ig2 = bi.createGraphics();
		ig2.setPaint(bgcolor);
		ig2.fillRect(0,
				0,
				width,
				height)
		Graphics.width = width
		Graphics.height = height
		x1 = 0.5 * width
		y1 = 0.5 * height
		ig2.setFont (nodeFont)
	}
	
	@Override
	public void resetFoundMark() {
		foundMark = 0
	}
	
	def setFont(kind,fontMap) {
		if (kind.toUpperCase() == "NODE") {
			nodeFont = new Font (fontMap.name, fontMap.style, fontMap.size)
		}
		if (kind.toUpperCase() == "LABEL") {
			labelFont = new Font (fontMap.name, fontMap.style, fontMap.size)
		}
	}

	@Override
	public void down() {
		down = true

	}

	@Override
	public void mark(String s) {
		def r=20
		int x = x1-(r/2)
		int y = y1-(r/2)
		foundMark++

		if (down) {
			ig2.setPaint(Color.black);
			ig2.fillOval(x,y,r,r);
			// note label placement to the right of node
			int xs = x+r*2
			ig2.setFont (nodeFont)
			ig2.drawString(s, xs, y)
		}

	}

	@Override
	public void label(String s) {
		
		label( s, edgeLen, angle)
	}

	@Override
	public void label(String s, double distance, double angle) {
		if (s=="null") return
		if (down) {
		
		def p2 = pointAngleDistance(x1,y1,angle,distance/2)
		int x = p2[0]
		int y = p2[1]
			ig2.setFont (labelFont)
			ig2.drawString(s, x, y)
		}
		
	}

	@Override
	public void forward(double distance) {
		
		def p2 = forward( distance,  angle)

		x1 = p2[0]
		y1 = p2[1]
	}

	@Override
	public List forward(double distance, double angle) {
		
		if (foundMark==1) edgeLen += distance
		
		def p2 = pointAngleDistance(x1,y1,angle,distance)
		def x2 = p2[0]
		def y2 = p2[1]

		if (down) {
			ig2.setPaint(color);
			ig2.setStroke(new BasicStroke(size))
			drawLine(x2,y2)
		} else { // a move
			x1 = x2
			y1 = y2
		}
		p2
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public double getDirection() {
		return angle;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public double getXPos() {
		// TODO Auto-generated method stub
		return x1;
	}

	@Override
	public double getYPos() {
		// TODO Auto-generated method stub
		return y1;
	}

	@Override
	public void move(double x2, double y2) {
		
		x1 = x2
		y1 = y2
	}

	@Override
	public void setColor(Color c) {
		color = c

	}

	@Override
	public void setDirection(double deg) {
		angle = deg

	}

	@Override
	public void setWidth(int width) {
		size = width

	}

	@Override
	public void turnLeft(double degrees) {
		angle -= degrees

	}

	@Override
	public void turnRight(double degrees) {
		angle += degrees
		angle = angle % 360
		
	}

	@Override
	public void up() {
		down = false

	}

	def drawLine(double x, double y) {
		drawLine(Double.valueOf(x).intValue(), Double.valueOf(y).intValue())
	}

	def drawLine(int x2, int y2) {
		def x1Int = Double.valueOf(x1).intValue()
		def y1Int = Double.valueOf(y1).intValue()
		ig2.drawLine(
			x1Int,
			y1Int,
			x2,
			y2)
	}

	def pointAngleDistance(x,y,angle,d) {

		def rad = Math.toRadians(angle)
		def y2 = Math.sin(rad) * d + y
		def x2 = Math.cos(rad) * d + x
	
		[x2, y2]
	}

	public void  save(String filename) {
		ImageIO.write(bi, "JPEG", new File(filename));
		
	}

	@Override
	public void save(OutputStream stream) {
		ImageIO.write(bi, "JPEG", stream);
		
	}

	@Override
	public void load(String filename) {
		bi = ImageIO.read(new File(filename));
	}

}
