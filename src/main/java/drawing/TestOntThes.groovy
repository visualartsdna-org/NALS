package drawing

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import rdf.JenaUtils

class TestOntThes {

	def base="C:/test/testDrive" // modify as needed
	def size = 14000

	
	// uses http file access for source
	@Test
	void testVadna() {
		def namespace = "http://visualartsdna.org/model/"
		def galleryPath = "$base/vadnaNals.jpg"
		def ruleFile = "$base/lsysVadna.json"
		def meta = "http://visualartsdna.org/model/"
		def infile = "$base/vadna.ttl"
		def m = new JenaUtils().loadOntImports(meta, "ttl")
		new JenaUtils().saveModelFile(m, infile, "TTL")
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = "EC4124" // vermillion

		new Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}
	
}
