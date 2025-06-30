package drawing

import rdf.JenaUtils
import groovy.json.JsonSlurper
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

class Query {
	
	def limit // not used
	
	def encodeSparqlUri(sparql) {
		def sparqlEnc = URLEncoder.encode(sparql)
		def graph = URLEncoder.encode("http://dbpedia.org")

		//def res = "https://dbpedia.org/sparql/?default-graph-uri=${graph}&query=${sparqlEnc}&format=text%2Fturtle&timeout=30000&signal_void=on&signal_unconnected=on"
		def res = "https://dbpedia.org/sparql/?default-graph-uri=${graph}&query=${sparqlEnc}&format=application%2Fsparql-results%2Bjson&timeout=30000&signal_void=on&signal_unconnected=on"
		res
	}

	def get(String uri) {
		
		def url=encodeSparqlUri(uri)

		def json = ""
		try {
			json = new URL(url).text
		} catch (IOException ioe) {
			println "ERROR: $ioe\n$url"
			return result
		}
		
		new JsonSlurper().parseText(json)
	}
	

}
