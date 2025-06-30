package drawing

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import rdf.JenaUtils

class TestArt {
//	def base="C:/test/extinct/Carpinus_perryae" // modify as needed
//	def base="C:/test/extinct/Python_europaeus" // modify as needed
	def base="C:/test/dbpedia/Basal"
	def fname = "wikiLink"
	def size = 14000
	
	@Test
	void testDraw() {
		new DataGraphDriver().driver(
			"$base/${fname}.json",
			"$base/${fname}.jpg")
	}

	@Test
	void testDecor() {
		def namespace = "http://dbpedia.org/ontology/"
		def galleryPath = "$base/wikiLinkD.jpg"
		def ruleFile = "$base/wikiLink.json"
		def infile = "$base/wikiLink.ttl"
//		def m = new JenaUtils().loadOntImports(meta)
//		new JenaUtils().saveModelFile(m, infile, "TTL")
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = "EC4124" // vermillion

		new Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}
	
	@Test
	void testDecor0() {
		def namespace = "http://dbpedia.org/ontology/"
		def galleryPath = "$base/wikiLinkD.jpg"
		def ruleFile = "$base/wikiLink.json"
		def infile = "$base/wikiLink.ttl"
//		def m = new JenaUtils().loadOntImports(meta)
//		new JenaUtils().saveModelFile(m, infile, "TTL")
		def rulecode = "X=E,E=E[+E]E[-E][E]"
		def color = "EC4124" // vermillion

		new Generator()
		.generate(namespace,infile,ruleFile,galleryPath,size,rulecode,color)
	}
	
	@Test
	void testProcess() {
		queryLinks(eList())
		
	}
	
	def query = new Query()
	def eList() {
		["http://dbpedia.org/resource/The_Lord_of_the_Rings"]
//		["http://dbpedia.org/resource/Basal-cell_carcinoma"]
//		["http://dbpedia.org/resource/Python_europaeus"]
//		["http://dbpedia.org/resource/Carpinus_perryae"]
		//eMap.keySet()
	}
	@Test
	void testQueryLinks() {
		queryLinks(eList())
	}
	
	def queryLinks(list) {
		list.each {
			def sparql = """
select ?s ?l1 ?l2 ?l3 ?l4 ?l5
{
bind(<$it> as ?s)
 ?s dbo:wikiPageWikiLink ?l1 .
?l1 dbo:wikiPageWikiLink ?l2 .
?l2 dbo:wikiPageWikiLink ?l3 .
?l3 dbo:wikiPageWikiLink ?l4 .
?l4 dbo:wikiPageWikiLink ?l5
} order by ?l1 limit 100
"""
		def ms = query.get(sparql)
		def ttl = makeTtl(it, ms)
		def md = new JenaUtils().saveStringModel(ttl,"ttl")
		println "model size=${md.size()}"
		new JenaUtils().saveModelFile(md,"$base/wikiLink.ttl","ttl")
		}

	}

	def rand = new Random()
	
	def makeTtl(uri, mres) {
		
		def s="""
@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
@prefix schema: <https://schema.org/> .
@prefix vad:   <http://visualartsdna.org/2025/04/26/model/> .
@prefix xs:    <http://www.w3.org/2001/XMLSchema#> .
@prefix vad:   <http://visualartsdna.org/2025/04/26/model/> .

"""
		def vars = mres.head.vars
		mres.results.bindings.each{m->
			for (int i=0;i<vars.size()-1;i++) {
				def k=m[vars[i]].value
				def k2=m[vars[i+1]].value
				s += """<$k>\tvad:p${vars[i+1].hashCode()}\t<${k2}> .\n"""
//				s += """<$k>\tvad:p${rand.nextInt()}\t<${k2}> .\n"""
			}
		}
		s
	}

}
