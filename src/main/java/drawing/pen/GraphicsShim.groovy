package drawing.pen

import java.awt.Color
import java.awt.Font
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import drawing.lsys.SignSeries


/**
The turtle-graphics canvas follows the 
Cartesian coordinate system.  The center 
of the graphics window is at location 0,0. 
Positive X is to the right; positive Y is up. 
Headings (angles) are measured in degrees 
counterclockwise from the positive X axis.

cartesian coordinate space (example with 800x800 space)
 -------------------------------------
 -400,400				400,400
 				0,0
 -400,-400				400,-400
 -------------------------------------
 Graphics2D coordinate space
 -------------------------------------
 0,0							800,0
 				400,400
 0,800						800,800
 -------------------------------------
The image produced in the transition between
coordinate spaces requires a transform
for scale and rotation.  See save()
NOTE: in cartesian coordinates, the "y" axis
increases bottom to top, in the Graphics2D
coordinate space, "y" axis increases top
to bottom.
 * @author ricks
 *
 */
class GraphicsShim implements IGraphics {

	Graphics g
	def transformRotateDeg = 0
	def transformScaleX = 1.0
	def transformScaleY = 1.0
	def metric = 1.0

	public GraphicsShim(w,h) {
		g = new Graphics(w,h, Graphics.bgcolor, metric)
	}

	public GraphicsShim(color) {
		g = new Graphics(Graphics.width, Graphics.height, color, metric)
	}

	public GraphicsShim() {
		g = new Graphics(Graphics.width, Graphics.height, Graphics.bgcolor, metric)
	}

	public GraphicsShim(width, height, bgcolor, metric) {
		g = new Graphics(width, height, bgcolor)
		this.metric = metric
	}

	public GraphicsShim(width, height, bgcolor, scale, rotate, metric) {
		g = new Graphics(width, height, bgcolor)
		transformRotateDeg = rotate
		transformScaleX = scale
		transformScaleY = scale
		this.metric = metric
	}
	
	public double getTransformRotate() {
		return transformRotateDeg;
	}

	public void setTransformRotate(double transformRotateDeg) {
		this.transformRotateDeg = transformRotateDeg;
	}

	public double getTransformScaleX() {
		return transformScaleX;
	}

	public void setTransformScaleX(double transformScaleX) {
		this.transformScaleX = transformScaleX;
	}

	public double getTransformScaleY() {
		return transformScaleY;
	}

	public void setTransformScaleY(double transformScaleY) {
		this.transformScaleY = transformScaleY;
	}

	def setFont(kind,font) {
		g.setFont(kind,font)
	}

	@Override
	public void down() {
		g.down()
	}

	@Override
	public void forward(double distance) {
		g.forward( distance)
	}

	@Override
	public Color getColor() {
		return g.getColor()
	}

	@Override
	public double getDirection() {
		return  g.getDirection()
	}

	@Override
	public int getWidth() {
		return g.getWidth();
	}

	@Override
	public double getXPos() {
		toGpdX(g.getXPos())
	}

	@Override
	public double getYPos() {
		toGpdY(g.getYPos())
	}

	@Override
	public void move(double x, double y) {
		g.move(toG2dX( x), toG2dY(y))

	}

	@Override
	public void setColor(Color c) {
		g.setColor(c)
	}
	
	def random = new Random()
	def ss = new SignSeries()
	def getSign() {
		//random.nextDouble()  < 0.5 ? -1 : 1
		ss.getSeriesNext()
	}
	// TODO-metric signs
	def degrees(degrees) {
		def offset = metric * getSign()
		return degrees + offset
	}

	@Override
	public void setDirection(double deg) {
		g.setDirection( degrees(deg))
	}

	@Override
	public void setWidth(int width) {
		g.setWidth( width)
	}

	@Override
	public void turnLeft(double deg) {
		g.turnLeft(degrees(deg))
	}

	@Override
	public void turnRight(double deg) {
		g.turnRight(degrees(deg))
	}

	@Override
	public void up() {
		g.up()
	}
	def transform() {
		BufferedImage before = g.bi
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(transformRotateDeg),w/2,h/2)
		at.scale(transformScaleX, transformScaleY);
		AffineTransformOp scaleOp =
		  new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
	}
	/*	
	 Translation between coordinate spaces:
	 */		
	double toG2dX(double x) {
		def x1=0.5 * Graphics.width + x
//		println "toG2dX x=$x, x1=$x1"
		x1
	}

	double toG2dY(double y) {
		def y1 = 0.5 * Graphics.height - y
//		println "toG2dY y=$y, y1=$y1"
		y1
	}

	// gpd x = g2d - (.5 imageSize)
	double toGpdX(double x) {
		def x1 = x - 0.5 * Graphics.width
//		println "toGpdX x=$x, x1=$x1"
		x1
	}
	
	// gpd y = imageSize - g2d - (.5 imageSize)
	double toGpdY(double y) {
		//Graphics.height - x - 0.5 * Graphics.height
		def y1 = - y + 0.5 * Graphics.height
//		println "toGpdY y=$y, y1=$y1"
		y1
	}

	@Override
	public void mark(String s) {
		g.mark(s )
		
	}

	@Override
	public void label(String s) {
		g.label(s)
		
	}

	@Override
	public List forward(double distance, double angle) {
		g.forward(distance,angle)
		
	}

	@Override
	public void label(String s, double distance, double angle) {
		g.label(s,distance,angle)
		
	}

	@Override
	public void resetFoundMark() {
		g.resetFoundMark()
		
	}
	
	@Override
	public void  save(String filename) {
		BufferedImage bi = transform()
		File img = new File(filename)
		ImageIO.write(bi, "JPEG", img);
	}

	@Override
	public void save(OutputStream stream) {
		BufferedImage bi = transform()
		ImageIO.write(bi, "JPEG", stream);
	}

	@Override
	public void load(String filename) {
		g.load(filename)
	}
		
}
