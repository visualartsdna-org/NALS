package drawing.parser

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class LsysParserTest {

	@Test
	void testUnicode() {

		[
			"X= F+[[X]-''X]-F[-FX]+''X,F=FF",
			"""G=(subClassOf)f@(http://dbpedia.org/ontology/شہر)[----|(subClassOf)f@(http://dbpedia.org/ontology/جزیرہ)""",
			"x = f@(abc *,]12'sdf',\"weoriu\"3DFG)[+f@(b)[+f@(e)]-[f@(d)]][-f@(c)[+f@(f)]],f = ff",
		].each{
			println it
			def baos  = new ByteArrayInputStream( it.getBytes())
			LsysParser parser = new LsysParser(baos);
			try {
				parser.parse();
			} catch (TokenMgrError pe) {
				println pe
			} catch (ParseException pe) {
				println pe
			}

			//parser.rules.each{ println it }
			
			def rules = makeRuleMap(parser.rules)
			
			
			//println rules
			rules.each{k,v-> println "$k = $v"}
			println ""
		}
	}
	
	@Test
	void test() {

		[
			"X= F+[[X]-''X]-F[-FX]+''X,F=FF",
			"x = f@(abc)[+f@(b)[+f@(e)]-[f@(d)]][-f@(c)[+f@(f)]],f = ff",
			"x = f@(abc *,]12'sdf',\"weoriu\"3DFG)[+f@(b)[+f@(e)]-[f@(d)]][-f@(c)[+f@(f)]],f = ff",
		].each{
			println it
			def baos  = new ByteArrayInputStream( it.getBytes())
			LsysParser parser = new LsysParser(baos);
			try {
				parser.parse();
			} catch (TokenMgrError pe) {
				println pe
			} catch (ParseException pe) {
				println pe
			}

			//parser.rules.each{ println it }
			
			def rules = makeRuleMap(parser.rules)
			
			
			//println rules
			rules.each{k,v-> println "$k = $v"}
			println ""
		}
	}
	
	def makeRuleMap(inRules) {
		def rules = [:]
		
		inRules.each{
			def var = it.remove(0)
			it.remove(0)
			rules[var]=it
		}
		rules
	}
}
