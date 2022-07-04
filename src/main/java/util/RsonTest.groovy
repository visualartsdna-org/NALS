package util

import static org.junit.jupiter.api.Assertions.*

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.jupiter.api.Test

class RsonTest {
	
	@Test
	void testList() {
		
		def m = Rson.load("rsonListTest.json")
		println JsonOutput.prettyPrint(
			JsonOutput.toJson(m))
		}

		@Test
		void testMap() {
			
			def m = Rson.load("rsonMapTest.json")
			println JsonOutput.prettyPrint(
				JsonOutput.toJson(m))
			}
	
	
	
}
