package drawing.translate

import static org.junit.jupiter.api.Assertions.*

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import util.Rson

import org.junit.jupiter.api.Test

/**
 * Supports the rule configuration process
 *
 */
class RuleCode {

	static def List hex2dec(hex) {
		if (!hex) return null
		def r0 = hex.substring(0,2)
		def g0 = hex.substring(2,4)
		def b0 = hex.substring(4,6)
		
		def r = Integer.parseInt(r0, 16)
		def g = Integer.parseInt(g0, 16)
		def b = Integer.parseInt(b0, 16)
		
		[r,g,b]
	}
	
	// dec is list [r,g,b]
	def dec2hex(dec) {

		"${Integer.toHexString(dec[0])}${Integer.toHexString(dec[1])}${Integer.toHexString(dec[2])}"
	}
	
	def dec2hex2(dec) {

		"${Integer.toHexString(dec)}"
	}
	
	
	def clone(c) {
		def js = new JsonOutput().toJson(c)
		new JsonSlurper().parseText(js)
	}
	
	def getClone(f,k) {
		def l = Rson.load(f)
		def c = l.find{
			it.name == k
		}
		c
	}
	
	
}
