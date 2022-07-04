package drawing

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import rdf.JenaUtils
import util.Tmp

class TestDriveQuery {

	def queryC = """
# Watercolors
prefix vad:	<http://visualartsdna.org/2021/07/16/model#>
prefix work:	<http://visualartsdna.org/work/> 
prefix xs: <http://www.w3.org/2001/XMLSchema#> 
prefix skos: <http://www.w3.org/2004/02/skos/core#>
prefix owl: <http://www.w3.org/2002/07/owl#>
prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

construct {
?w ?p ?s .
}{
	#bind (work:da79b4be-3442-4b6b-bdb4-107b2682c560 as ?w)
	?w ?p ?s .
	?w a vad:Watercolor .
	#?w a vad:Drawing .
	#?w vad:hasNFT ?bn .
	#filter(?s="pencil" || ?s=vad:Drawing)
}
"""
	
	def queryD = """
# Drawings
prefix vad:	<http://visualartsdna.org/2021/07/16/model#>
prefix work:	<http://visualartsdna.org/work/> 
prefix xs: <http://www.w3.org/2001/XMLSchema#> 
prefix skos: <http://www.w3.org/2004/02/skos/core#>
prefix owl: <http://www.w3.org/2002/07/owl#>
prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

describe ?w
{
	#bind (work:da79b4be-3442-4b6b-bdb4-107b2682c560 as ?w)
	?w ?p ?s .
	#?w a vad:Watercolor .
	?w a vad:Drawing .
	#?w vad:hasNFT ?bn .
	#filter(?s="pencil" || ?s=vad:Drawing)
}
"""
	def base="." // modify as needed
	def tmp = new Tmp()

	@Test
	void test() {
		def infile = tmp.getTemp("ttl",".txt")
		def meta = "http://visualartsdna.org/data"
		new File(infile).text = new URL(meta).getText()
		def ruleFile = "$base/queryNals.json" // lsys config
		def imgFile = "$base/queryNals.jpg"

		new QueryGraphDriver().translate(infile,ruleFile,queryD,6000)
		new DataGraphDriver().driver(ruleFile,imgFile,0.02)
		tmp.rmTemps()
	}
	


}
