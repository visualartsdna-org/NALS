package drawing.translate

import org.junit.jupiter.api.Test

import groovy.json.JsonSlurper
import rdf.JenaUtils
import drawing.DataGraphDriver

class Translate {
	
	def appendVar = "x" 

	/**
	 * 	Turn RDF instance data into a tree format
	 * @param infile, the input JSON-LD RDF file
	 * @param tree, a file for intermediate results
	 * @param root, a known root of the RDF tree
	 * @return
	 */
	def jsonld2TreeInst(infile,tree,root) {
		def outfile = new File(tree)
		def m0 = new JsonSlurper().parse(new File(infile))
		def l = m0["@graph"]
		def strLimit = 100
		def tset = [:]
		l.each{
			def t = it["@type"] ?: "blankNode"
			tset[t] = null
		}

		def i=0
		outfile.write "["
		outfile.append """
	{
		"child":"$root"
	},
"""
		tset.each{k,v->
			outfile.append """
	{
		"child":"$k",
		"parent":"$root"
	},
"""
		}
		l.each{
			def t = it["@type"] ?: "blankNode"
			def id = it.label ?: it["@id"]
			if (i++>0) outfile.append ","

			outfile.append """{

"child":"${id}" ,
"parent":"${t}" ,
"edge":"type"
}"""
		}
		l.each{m2->
			m2.each {k,v->
				def vs = ""+v
				int n = vs.length()
				def value0 = n > strLimit ? vs.substring(0,strLimit) : vs
				def value = (value0).replaceAll(/["()]/,"")
				if (k=="label"
					|| k=="@type"
					|| k=="@id"
					) return
				def lab = m2.label ?: m2["@id"]
				
				if (i++>0) outfile.append ","

				outfile.append """{
				
"child":"${value}" ,
"parent":"${lab}" ,
"edge":"$k"
}"""
			}
		}
		outfile.append "]"
	}
	
	/**
	 * Turn RDF ontology data into a tree format
	 * Only the rdfs:subClassOf relation is recognized
	 * @param infile, the input JSON-LD RDF file
	 * @param tree, a file for intermediate results
	 * @param root, a known root of the RDF tree 
	 * @param baseClass, a list of known roots in the ontology
	 * @return
	 */
	def jsonld2TreeOnto(infile,tree,root,baseClass) {
		def outfile = new File(tree)
		def m0 = new JsonSlurper().parse(new File(infile))
		def l = m0["@graph"]
		def i=0
		outfile.write "["
		outfile.append """
	{
		"child":"$root"
	},
"""
		baseClass.each{k->
			outfile.append """
	{
		"child":"$k",
		"parent":"$root"
	},
"""
		}
		def scoMap=[:]
		l.each{
			def t = it["subClassOf"] 
			if (t == null) return
			def id = it["@id"]
			
			if (!scoMap.containsKey(t))
				scoMap[t] = []
			scoMap[t] += id
			
		}
		// filter out BN restrictions from
		// lists of parent keys
		def scoMap2 = scoMap.findAll{k,v->
			!(k instanceof List)
		}
		scoMap.findAll{k,v->
			(k instanceof List)
		}.each{k,v->
			k.findAll{
				!it.startsWith("_:b")
			}.each{
				if (!scoMap2.containsKey(it)) {
					scoMap2[it]= v
				}
				else {
					scoMap2[it]+= v
				}
			}
			
		}
		scoMap2.sort().each{k,v->
		//scoMap.each{k,v->
			v.each{
				if (i++>0) outfile.append ","
				outfile.append """{

"child":"${it}" ,
"parent":"${k}" ,
"edge":"subClassOf"
}"""
			}
		}
		outfile.append "]"
	}
	
	def data
	def map=[:]

	/**
	 * Turn the data tree into a turtle graphics rule
	 * @param dataFile, a JSON intermediate file of tree info
	 * @param root, a known root of the tree
	 * @return the resulting turtle graphic rule
	 */
	def tree2Turtle(dataFile,root) {
		def s = new File(dataFile).text
		// can't have any lsys-grammar parentheses in data
		s = s.replaceAll(/[\(\)]/,"") 
		data = new JsonSlurper().parseText(s)
		//data = new JsonSlurper().parse(new File(dataFile))
		def nodes = findNode("parent","$root")

		def sb = new StringBuilder()
		sb.append "@(${root})"
		printTurtle(nodes,sb,0)
		"$sb"
	}
	
	def uniqueNodeSet=[:]

	// eliminate descendants of node
	// that has already been seen
	/**
	 * 
	 * @param nodes, the input tree data
	 * @param sb, the resulting turtle string
	 * @param level, recursion level
	 * @return
	 */
	def printTurtle(nodes,sb,level) {
		int n = nodes.size()
		int i=n
		nodes.each{
			sb.append "["
			// this introduces a special behavior
			// of drawing lines vertically, an experiment
			def turn = false //appendVar && level == 1 
			? getTurn(i--,n,"\$","%") 
			: getTurn(i--,n)
			sb.append "$turn"
			sb.append "|(${it.edge})"
			sb.append "f@(${it.child})"
			
			def n2 = findNode(it.child)
			printTurtle(n2,sb,level+1)
		}
		if (level > 0)
			sb.append "${appendVar ?: ""}]"
	}

	// can be none, + right, - left, or multiples of right or left
	def getTurn(i,n) {
		getTurn(i,n,"+","-")
	}
	def getTurn(i,n,right,left) {
		

		if (i==1) return left
		
		def s = left
		(1..i/2).each {
			s += i%2 ? right : left
		}

		return s
	}

	def evalNodes(m) {
		def lm = [:]
		m.each{k,v->
			if (!lm.containsKey(v)) lm[v]= 0
			lm[v]++
		}
		lm.each{k,v->
			println "$k $v"
		}
	}


	def printNode(nodes,level) {
		nodes.each{
			map[it.child] = level
			def n2 = findNode(it.child)
			printNode(n2,level + 1)
		}
	}

	def findNode(name) {
		findNode("parent",name)
	}

	def findNode(prop,name) {
		data.findAll{ it[prop]==name}
	}

	def ttl2JSONLD(infile,outfile,ext) {
		File temp = new File(outfile)
		def m = new JenaUtils().loadFiles(infile, ext)
		new JenaUtils().saveModelFile(m, outfile, "JSON-LD")
	}

}
