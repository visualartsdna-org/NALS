package drawing

import org.apache.jena.rdf.model.Model
import org.junit.jupiter.api.Test
import java.awt.Color
import drawing.translate.GenConfig
import drawing.translate.Translate
import drawing.lsys.LSystem
import drawing.pen.*
import util.Rson
import util.Tmp
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import rdf.JenaUtils
import com.icafe4j.image.gif.GIFTweaker;

class QueryGraphDriver {
	
	def tmp = new Tmp()

	/**
	 * 
	 * @param infile, the source RDF/TTL
	 * @param ruleFile, the resulting rule configuration file
	 * @param query, the SPARQL construct or describe query
	 * @param size, the size of the graphic canvas
	 * @return
	 */
	def translate(infile,ruleFile,query,size) {
		def trans = new Translate()
		
		def jsonld = tmp.getTemp("jsonld",".txt")
		def tree = tmp.getTemp("tree",".txt")
		def root = "query"
		
		def jena = new JenaUtils()
		def model = jena.loadFiles(infile)

		def m2
		if (query.contains("construct"))
			m2 = jena.queryExecConstruct(model,"", query)
		else if (query.contains("describe"))
			m2 = jena.queryDescribe(model,"", query)
		jena.saveModelFile(m2, jsonld, "JSON-LD")
		
		def strLimit = 100
		trans.jsonld2TreeInst(jsonld,tree,root)
		
//		println jsonld
//		println tree
		
		def ts = trans.tree2Turtle(tree,root)
		def desc = getQueryDesc(query)
		
		new GenConfig().write(ruleFile,ts,desc,size)
		tmp.rmTemps()
	}
	
	def getQueryDesc(q) {
		def desc
		def l = q.split(/\n/)
		desc = l.find {
			it =~ /^#/
		}
		return desc.replaceAll(/^#[ ]*/,"")
	}

}
