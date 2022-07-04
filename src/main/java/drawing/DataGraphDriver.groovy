package drawing


import static org.junit.jupiter.api.Assertions.*
import java.awt.Color
import drawing.translate.GenConfig
import drawing.translate.Translate
import drawing.lsys.LSystem
import groovy.json.JsonOutput
import drawing.pen.*
import util.Rson
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import rdf.JenaUtils
import com.icafe4j.image.gif.GIFTweaker;
import org.junit.jupiter.api.Test

class DataGraphDriver {

	def driver(json,galleryPath) {
		driver(json,galleryPath,0.2)
	}
	def driver(json,galleryPath,metric) {
		driver(json,galleryPath,metric,null)
	}

/**
 * Given a rule configuration file, generat the graphic file
 * @param json, the JSON rule configuration file
 * @param galleryPath, the target JPEG file
 * @param metric, a factor in graph variability
 * @param color, the foreground color of the graph
 * @return
 */
	def driver(json,galleryPath,metric,List color) {

		def c = Rson.load(json)

		def cfg = c.find{m->
			m.type=="config"
		}

		def xspan = cfg.xspan ?: 4000
		def yspan = cfg.yspan ?: 3000
		def scale = cfg.scale ?: 1.0
		def rotate = cfg.rotate ?: 0
		def xbase = cfg.xbase ?: 0
		def ybase = cfg.ybase ?: 0
		def autoPlace = cfg.autoPlace != null ? cfg.autoPlace : true
		def width = cfg.width ?: 2
		def turn = cfg.turn ?: 90
		def redBg = cfg.bgcolor && cfg.bgcolor.red != null   ? cfg.bgcolor.red : 255
		def greenBg = cfg.bgcolor && cfg.bgcolor.green != null   ? cfg.bgcolor.green : 255
		def blueBg = cfg.bgcolor && cfg.bgcolor.blue != null   ? cfg.bgcolor.blue : 255
		def red = cfg.color && cfg.color.red != null   ? cfg.color.red : 1
		def green = cfg.color && cfg.color.green != null   ? cfg.color.green : 150
		def blue = cfg.color && cfg.color.blue != null   ? cfg.color.blue : 150
		def border = cfg.border ?: 0
		def legend = cfg.legend ?: "relative"  // or absolute
		def nodeFont = cfg.nodeFont ?: [name:"Arial",size:25,style:0]
		def labelFont = cfg.labelFont ?: [name:"Arial",size:20,style:2]
		def sx = xspan + 2 * border
		def sy = yspan + 2 * border

		def shim = new GraphicsShim(
				sx,sy,
				new Color(redBg,greenBg,blueBg),
				scale, rotate, metric)

		shim.setFont("node",nodeFont)
		shim.setFont("label",labelFont)

		LSystem sys = new LSystem(shim)

		c.findAll{m->
			(m.type=="lsys" || m.type=="legend") &&  m.active
		}.each { m->
			shim.resetFoundMark()
			sys.getInput(m.graph)
			
			def redFg = color ? color[0] : m.color ? m.color.red : red
			def greenFg = color ? color[1] : m.color ? m.color.green : green
			def blueFg = color ? color[2] : m.color ? m.color.blue : blue
			def w = m.graph.width ?: width
			
			def xpos = xbase
			def ypos = ybase

			if (legend == "relative" && m.type=="legend") {
					xpos = xspan/2
					ypos = -yspan/2
			}

			sys.draw(
					xpos + m.x as int,
					ypos + m.y as int,
					turn, w,
					new Color(redFg,greenFg,blueFg)
					)
		}
		sys.save(galleryPath)

	}

}
