package drawing.lsys

import drawing.pen.Graphics
import drawing.pen.GraphicsShim
import drawing.pen.IGraphics
import java.util.Scanner;
import drawing.parser.*
import java.util.ArrayDeque;
import java.awt.Color;

/**
 * 
 * @author rspates
 * based on https://github.com/kbhadury/LSystem
 *
 */
public class LSystem{
	static def verbose=false
	def vars
	def ops
	def rules;
	double angle, length;
	def start;
	int level;
	boolean varsDraw = true; //Test if variables draw by default TODO: review
	IGraphics pen
	def stackLevel = 0
	def deltaMin = 50
	def deltaMax = 255
	def rDelta = 0
	def gDelta = 0
	def bDelta = 0
	

	LSystem(){
		this(800,800,0)
	}
	LSystem(int x,int y,double metric){
		pen = new GraphicsShim(x,y,Color.white,metric)
	}

	LSystem(pen){
		this.pen = pen
	}

	/* Get config data
	 */
	def getInput(m){
		vars = m.vars.split(/ /)
		ops = m.consts.split(/ /)
		start = m.start
		rules = parseRuleInput(m.rules);
		angle = m.angle as double
		length = m.length
		level = m.level
		rDelta = m.redDelta ?: 0
		gDelta = m.greenDelta ?: 0
		bDelta = m.blueDelta ?: 0
	}

	def draw(int x, int y, double dir, int width, Color color){
		initPen(x, y, dir, width, color);
		drawRule(start, level);
	}

	def initPen(int x, int y, double dir, int width, Color color){
		pen.up();
		pen.move(x,y);
		pen.setDirection(dir);
		pen.down();

		//Set color and thickness
		pen.setColor(color);
		pen.setWidth(width);
	}
	
	def nodeCoordMap=[:]
	def resetNodeCoordMap() {
		nodeCoordMap=[:]
	}

	/*Draw the system based on the starting rule and recursion level*/
	def drawRule(list, int level){
		if (verbose) println "drawRule $list"
		ArrayDeque<Double> stack = new ArrayDeque<Double>(); //For ] and [ operations

		for (int i=0;i<list.size();i++){
			def c = list[i]
			if (verbose) println "drawRule c = $c"

			//Perform appropriate action
			if(ops.contains(c.substring(0,1))){
				if(c == '['){ //Save pen and length info to stack
					if (verbose) println "push $stackLevel, ${pen.getXPos()}, ${pen.getYPos()}, ${pen.getDirection()}"
					stack.push(length);
					stack.push(pen.getXPos());
					stack.push(pen.getYPos());
					stack.push(pen.getDirection());
					stack.push((double)pen.getWidth());
//					def w=pen.getWidth()
//					w-=w/2
//					pen.setWidth((int)w);
					stackLevel ++
				} else if(c == ']'){ //Restore angle and position from stack
					double width = stack.pop();
					double dir = stack.pop();
					double y = stack.pop();
					double x = stack.pop();
					length = stack.pop();
					stackLevel --
					if (verbose) println "pop $stackLevel, ${x}, ${y}, ${dir}"
					pen.up();
					pen.move(x,y);
					pen.setDirection(dir);
					pen.setWidth((int)width);
					pen.down();
				} else {
					if (c.startsWith("|")
						&& list[i+1]=="F"
						&& list[i+2].startsWith("@")) {
						def node = list[i+2].substring(1)
						if (!nodeCoordMap.containsKey(node)) {
							doOp(c)	// the '|()'
							def rule = getRule(list[i+1]) // the 'F'
							drawRule(rule,level-1);
							doOp(list[i+2]) // the '@()'

							def x = pen.getXPos()
							def y = pen.getYPos()
							nodeCoordMap[node] = [x,y] // put x,y in map
						i+=2 // update list pointer past 3 op elements
						continue
						}
						else {
							// get x,y for node location
							def coord = nodeCoordMap[node]
							def x1 = coord[0]
							def y1 = coord[1]
							// get current location
							def x0 = pen.getXPos()
							def y0 = pen.getYPos()
							// derive angle and distance to node
							
							def dx = x0>x1 ? -(x0-x1) : x1-x0
							def dy = y0>y1 ? -(y0-y1) : y1-y0
							def distance = Math.sqrt(dx*dx + dy*dy)
							def theta = Math.atan2(dy,dx)
							def deg = Math.toDegrees(theta)

							// draw line to node location
							// draw label given angle and distance
							def expr = c.substring(1)
							pen.forward(distance,-deg)
							pen.label(expr,distance,-deg)
							
						i+=2 // update list pointer past 3 op elements
						continue
						}

					}
					else doOp(c);
				}
			} else if(vars.contains(c)){
				if(level == 0){ //No more recursion, execute directly
					if (verbose) println "\tdoVar $c, $level"
					doVar()
				} else { //Replace with rule and recurse
					if (verbose) println "\tRpRec $c, $level"
					def rule = getRule(c)
//					def w=pen.getWidth()
//					w-=w/1000
//					pen.setWidth((int)w);
					drawRule(rule,level-1);
				}
			} else {
				println("Unknown character in drawRule: " + c);
			}
		}
	}

	def doOp(op){
		if (verbose) println "doOp $op"
		switch(op.substring(0,1)){
			//Movement
			case '$':
				pen.setDirection(-90)
				pen.up(); pen.forward(length,0); pen.down();
				break;
			case '%':
				pen.setDirection(-90)
				pen.up(); pen.forward(length,180); pen.down();
				break;
			case '+':
				pen.turnRight(angle);
				break;
			case '-':
				pen.turnLeft(angle);
				break;
			case 'E': 
			case 'F': 
					pen.forward(length); changePenColor();
				break;
			case 'M':
						pen.up(); pen.forward(length); pen.down();
				break;

			//Pen
			case '@':
				def expr = op.substring(1)
				pen.mark(expr);
				break; // TODO: gobble up expression
			case '|':
				def expr = op.substring(1)
				pen.label(expr);
				break; // TODO: gobble up expression
			case '*':
				changePenColor();
				break;
			case '"':
				pen.setWidth(Math.min(100, pen.getWidth()+1));
				break;
			case '\'':
				pen.setWidth(Math.max(1, pen.getWidth()-1));
				break;

			//Length
			case '~':
				length = length*2;
				break;
			case '`':
				length = length/2;
				break;

			default:
				println("Unknown character in doOp: " + op);
		}
	}

	def doVar(){
		if(varsDraw){
			pen.forward(length);
			changePenColor();
		}
	}

	// parse rules
	def parseRuleInput(input){
		def baos  = new ByteArrayInputStream( input.getBytes())
		LsysParser parser = new LsysParser(baos);
		try {
			parser.parse();
		} catch (TokenMgrError pe) {
			println pe
		} catch (ParseException pe) {
			println pe
		}

		makeRuleMap(parser.rules)
	}

	def makeRuleMap(inRules) {
		def rules = [:]

		inRules.each{
			def var = it.remove(0)	// the var letter
			it.remove(0)			// the '='
			if (!rules[var])
					rules[var] = []
			rules[var].add(it)
		}
		rules
	}
	
	def random = new Random()
	def getRule(v) {
		def l = rules[v]
		def r = random.nextInt(l.size())
		l[r]
	}

	// change the pen color
	// delta value can be {1,0,-1}
	def changePenColor(){
		int r = (pen.getColor().getRed())
		int g = (pen.getColor().getGreen())
		int b = (pen.getColor().getBlue())

		//  colors delta
		if (rDelta && r + rDelta > deltaMax) rDelta = -Math.abs(rDelta)
		else if (rDelta && r + rDelta < deltaMin) rDelta = Math.abs(rDelta)
		r += rDelta

		if (gDelta && g + gDelta > deltaMax) gDelta = -Math.abs(gDelta)
		else if (gDelta && g + gDelta < deltaMin) gDelta = Math.abs(gDelta)
		g += gDelta

		if (bDelta && b + bDelta > deltaMax) bDelta = -Math.abs(bDelta)
		else if (bDelta && b + bDelta < deltaMin) bDelta = Math.abs(bDelta)
		b += bDelta

		pen.setColor(new Color(r,g,b));
	}
	
	def save(String fn) {
		pen.save(fn)
	}
	
	def save(OutputStream fn) {
		pen.save(fn)
	}
}
