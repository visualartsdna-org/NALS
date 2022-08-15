package drawing

import static org.junit.jupiter.api.Assertions.*

import drawing.lsys.LSystem
import drawing.pen.GraphicsShim
import drawing.translate.GenConfig
import drawing.translate.Translate
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import drawing.translate.RuleCode
import java.awt.Color
import org.junit.jupiter.api.Test
import rdf.JenaUtils
import util.Rson
import util.Tmp

class Generator {

	def colors=[:]
	def tmp = new Tmp()

	def generate(namespace,infile,
			ruleFile,galleryPath,size) {
		generate(namespace,infile,
				ruleFile,galleryPath,size,"X=X",null)
	}

	def generate(namespace,infile,
			ruleFile,galleryPath,size,rulecode,String color) {
		generate(namespace,infile,
				ruleFile,galleryPath,size,rulecode, RuleCode.hex2dec(color))
	}

	def generate(namespace,infile,
			ruleFile,galleryPath,size,rulecode,String color,author) {
		generate(namespace,infile,
				ruleFile,galleryPath,size,rulecode, RuleCode.hex2dec(color),author)
	}

	/**
	 * Given RDF/TTL, generate the graphic
	 * @param namespace, a term for the base of the graph
	 * @param infile, input RDF data in a TTL file
	 * @param ruleFile, the resulting rule configuration JSON file
	 * @param galleryPath, the resulting JPEG graphic file
	 * @param size, bounds of the graphic canvas
	 * @param rulecode, the extended l-system rule to decorate the graph
	 * @param color, the foreground color of the graph
	 * @return
	 */
	def generate(namespace,infile,
			ruleFile,galleryPath,size,rulecode,List color) {


		def m = new JenaUtils().loadOntImports(infile)
		def r = new JenaUtils().findAllRootsList(m)
		def ts = translate(infile,ruleFile,namespace,r,size)

		def guid = UUID.randomUUID()
		new GenConfig().write(ruleFile,ts,namespace,size,rulecode,guid)
		new DataGraphDriver().driver(ruleFile,galleryPath,0.02,color)
	}

	def generate(namespace,infile,
			ruleFile,galleryPath,size,rulecode,List color,author) {


		def m = new JenaUtils().loadOntImports(infile)
		def r = new JenaUtils().findAllRootsList(m)
		def ts = translate(infile,ruleFile,namespace,r,size)

		def guid = UUID.randomUUID()
		new GenConfig().write(ruleFile,ts,namespace,size,rulecode,guid,author)
		new DataGraphDriver().driver(ruleFile,galleryPath,0.02,color)
	}

	def translate(infile,ruleFile,root,String baseClass,size) {
		translate(infile,ruleFile,"TTL",root,[baseClass],size)
	}

	def translate(infile,ruleFile,root,List baseClass,size) {
		translate(infile,ruleFile,"TTL",root,baseClass,size)
	}

	/**
	 * Manage the translation process from RDF/TTL to l-system turtle
	 * @param infile
	 * @param ruleFile
	 * @param ext
	 * @param root
	 * @param baseClass
	 * @param size
	 * @return
	 */
	def translate(infile,ruleFile,ext,root,List baseClass,size) {
		def trans = new Translate()

		def jsonld = tmp.getTemp("jsonld",".txt")
		def tree = tmp.getTemp("tree",".txt")

		trans.ttl2JSONLD(infile,jsonld,ext)

		def strLimit = 100
		if (!baseClass) {
			trans.jsonld2TreeInst(jsonld,tree,root)
		} else {
			trans.jsonld2TreeOnto(jsonld,tree,root,baseClass)
		}

		//				println jsonld
		//				println tree

		trans.tree2Turtle(tree,root)

	}
}
