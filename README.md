
##### Nodes and Arcs Lindenmayer System

##### Introduction

The Nodes and Arcs Lindenmayer System (NALS) is a maven project coded in groovy.

This project is an artistic endeavor, the graphs are intended for aesthetic and not analytical purposes.  NALS can display graphs of RDF data using a [Lindenmayer System](https://en.wikipedia.org/wiki/L-system).  This is made possible with the addition of syntax to the grammar for a node (@), an arc (|) and related text.

NALS provides a mapping between RDF and the L-System to draw the graphs.  Ontologies are a special case where only the "rdfs:subClassOf" relation is recognized.

The L-system graphics semantics support the turtle drawing language requirements. The graphics subsystem is in two layers. The lower layer sits on java's Graphics2D API in the java coordinate space. The upper layer under the L-system translates orientation and coordinate space between the two.

This project is a modified version of [https://github.com/rspates/lsys](https://github.com/rspates/lsys), which is based on [https://github.com/kbhadury/LSystem](https://github.com/kbhadury/LSystem).  


##### Deployment

Clone the repository and build with maven.

##### Test

Run drawing.TestDrive to produce JPEG graph files of selected open-source ontologies.

Run drawing.TestDriveQuery to produce JPEG graph files from SPARQL query constructs on RDF data from [http://visualartsdna.org](http://visualartsdna.org).




