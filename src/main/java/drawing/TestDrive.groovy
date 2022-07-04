package drawing

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import rdf.JenaUtils

/**
 * Test of NALS with opensource ontologies
 * Run tests in order due to dependencies
 * @author ricks
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDrive {
	
	def base="." // modify as needed
	def size = 14000

	
	// uses http file access for source
	@Test
	@Order(1)
	void testDbpedia() {
		def namespace = "http://dbpedia.org/ontology/"
		def galleryPath = "$base/dbpediaNals.jpg"
		def ruleFile = "$base/lsysDbpedia.json"
		def meta = "http://archivo.dbpedia.org/download?o=http%3A//dbpedia.org/ontology/&f=ttl"
		def infile = "$base/ontology.ttl"
		def m = new JenaUtils().loadOntImports(meta)
		new JenaUtils().saveModelFile(m, infile, "TTL")
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = "EC4124" // vermillion

		new drawing.Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}
	
	@Test
	@Order(2)
	void testDbpedia0() {
		def namespace = "http://dbpedia.org/ontology/"
		def galleryPath = "$base/dbpediaNals2.jpg"
		def ruleFile = "$base/lsysDbpedia2.json"
		def meta = "http://archivo.dbpedia.org/download?o=http%3A//dbpedia.org/ontology/&f=ttl"
		def infile = "$base/ontology.ttl"
		def m = new JenaUtils().loadOntImports(meta)
		new JenaUtils().saveModelFile(m, infile, "TTL")
		
		new Generator().generate(namespace,infile,ruleFile,galleryPath,size)
	}

	@Test
	@Order(3)
	void testSchema() {

		def namespace = "http://schema.org"
		def galleryPath = "$base/schemaNals.jpg"
		def ruleFile = "$base/lsysSchema.json"
		def meta = "https://schema.org/version/latest/schemaorg-current-https.ttl"
		def infile = "$base/schema.org.ttl"
		def m = new JenaUtils().loadOntImports(meta)
		new JenaUtils().saveModelFile(m, infile, "TTL")	
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = "EC4124" // vermillion

		new drawing.Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}

	// depends on existing infile
	@Test
	@Order(4)
	void testSchema0() {

		def namespace = "http://schema.org"
		def galleryPath = "$base/schemaNals2.jpg"
		def ruleFile = "$base/lsysSchema2.json"
		def infile = "$base/schema.org.ttl"
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = [236, 65, 36] // vermillion
		
		new drawing.Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}

	// depends on existing ruleFile
	// Given a json config file already exists
	// create a graph
	@Test
	@Order(5)
	void testDraw() {
		new DataGraphDriver().driver(
			"$base/lsysSchema2.json",
			"$base/schemaNals1.jpg")
	}

}
