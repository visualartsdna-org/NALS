package drawing.translate

import java.text.SimpleDateFormat

class GenConfig {
	def version = "1.1"
	
	def write(cfgFile, rule) {
		write(cfgFile, rule, 12000)
	}
	def write(cfgFile, rule, size) {
		write(cfgFile, rule,"", size)
	}
	def write(cfgFile, rule, namespace, size) {
		write(cfgFile, rule, namespace, size, "X=X", "")
	}

	def write(cfgFile, rule, namespace, size, XRule, guid) {
		write(cfgFile, rule, namespace, size, XRule, guid, "the author")
	}
/**
 * 		
 * @param cfgFile, the JSON rule configuration file
 * @param rule, the l-system rule derived from the RDF
 * @param namespace, typically a namespace URI, or other identifier
 * @param size, size of the graphic canvas
 * @param XRule, the extended l-system rule to decorate the graph
 * @param guid, an identifier for the graphic
 * @param copyrightOwner, the author of the work 
 * @return
 */
	def write(cfgFile, rule, namespace, size, XRule, guid, copyrightOwner) {
		def time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
		def borderProportion = 0.2

		def template = """
[
	{
		"type": "config",
		"comment": "data lsys graphics",
		"xspan": $size,
		"yspan": $size,
		"scale": 1.0,
		"rotate": 0,
		"xbase": -1000,
		"ybase": 0,
		"autoPlace": false,
		"width": 5,
		"turn": -90,
		"border": ${0.2 * size as int},
		"version": ${version},
		"legend": "relative",
		"bgcolor": {
			"red": 213,
			"green": 214,
			"blue": 200
		},
		"color": {
			"red": 100,
			"green": 100,
			"blue": 100
		},
		"arrange":"individual",
		"guid": "$guid"
	},
	{
		"name": "plant1",
		"id":"2",
		"type": "legend",
		"active": true,
		"x": 0,
		"y": 0,
		"graph": {
			"vars": "X F",
			"consts": "+ - [ ] '",
			"start": "X",
			"rules": "X= F+[[X]-''X]-F[-FX]+''X,F=FF",
			"angle": -25.0,
			"length": 2,
			"level": 7
		}
	},
	{
		"name": "signature",
		"id":"3",
		"active": true,
		"type": "legend",
		"x": 0,
		"y": 100,
		"graph": {
			"vars": "X F",
			"consts": "+ - [ ] ' \\" @",
			"start": "X",
			"rules": "x=@(Copyright \u00a9 2022, $copyrightOwner)",
			"angle": 15.0,
			"length": 7,
			"level": 6
		}
	},
	{
		"name": "legend",
		"id":"4",
		"active": true,
		"type": "legend",
		"x": 0,
		"y": 75,
		"graph": {
			"vars": "X F",
			"consts": "+ - [ ] ' \\" @",
			"start": "X",
			"rules": "x=@($namespace)",
			"angle": 15.0,
			"length": 7,
			"level": 6
		}
	},
	{
		"name": "legend2",
		"id":"5",
		"active": true,
		"type": "legend",
		"x": 0,
		"y": 50,
		"graph": {
			"vars": "X F",
			"consts": "+ - [ ] ' \\" @",
			"start": "X",
			"rules": "x=@(Generated with NALS, the Nodes and Arcs Lindenmayer System)",
			"angle": 15.0,
			"length": 7,
			"level": 6
		}
	},
	{
		"name": "legend3",
		"id":"5",
		"active": true,
		"type": "legend",
		"x": 0,
		"y": 25,
		"graph": {
			"vars": "X F",
			"consts": "+ - [ ] ' \\" @",
			"start": "X",
			"rules": "x=@(${time}, ${guid})",
			"angle": 15.0,
			"length": 7,
			"level": 6
		}
	},
	{
		"name": "generated from TTL file",
		"id":"1",
		"active": true,
		"type": "lsys",
		"x": 1000,
		"y": 0,
		"color": {
			"red": 100,
			"green": 100,
			"blue": 100
		},
		"graph": {
			"vars": "G X F E",
			"consts": "+ - [ ] ' \\" @ | \$ %",
			"start": "G",
			"rules": "G=${rule},f = ff,${XRule}",
			"angle": 12.0,
			"length": 30,
			"level": 6,
			"width": 10,
			"redDelta": 1,
			"greenDelta": 1,
			"blueDelta": 1
		}
	}
]
"""
		new File(cfgFile).text = template
	}
	
}
