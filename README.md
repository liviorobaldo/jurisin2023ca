This repository contains the computational artefact associated with the paper "Compliance checking in the energy domain via W3C standards", submitted to JURISIN 2023.

To compile the file <i>infer.java</i>: 

<p align="center">
  javac -cp .;./build/classes;./lib/spark/*;./lib/* infer.java
</p>

To run the file <i>infer.class</i>: 

<p align="center">
  java -cp .;./build/classes;./lib/spark/*;./lib/* infer
</p>

<br>
<p align="align">
  The latter creates two new files: <i>inferredOntology.rdf</i> and <i>inferredTriples.rdf</i> containing the results of the SHACL-SPARQL queries.
</p>
