package drawing.lsys

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

/**
 * Generates random factors determining 
 * left and right turns of l-system rule
 * productions.
 *
 */
class SignSeries {

	def random = new Random()
	def getSign() {
		random.nextDouble()  < 0.5 ? -1 : 1
	}
	
	def seriesMax=50000
	def getSeriesSize() {
		random.nextInt(seriesMax)+1
	}
	
	def seriesCnt=0
	def seriesSign=0
	def getSeriesNext() {
		if (!seriesCnt) {
			seriesCnt = getSeriesSize()
			//println "\t$seriesCnt"
			seriesSign=0
		}
		if (!seriesSign)
			seriesSign = getSign()
			
		seriesCnt--
		//println "$seriesCnt: $seriesSign"
		return seriesSign
		
	}
	
	@Test
	void test() {
		(0..60).each{
			println "get sign=${getSeriesNext()}"
		}
	}

}
