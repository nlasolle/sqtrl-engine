# SPARQL Query Transformation Rule Engine

# General presentation

## A flexible querying system for Semantic Web data
The SPARQL Query Transformation Rule Engine is a system design to enable flexible querying over Semantic Web data. It relies on the use of transformation rules under XML or JSON syntax. 

Starting with an initial SPARQL query, an RDF graph and a set of transformation rules, the system
 can generate SPARQL queries which present similarities to the initial query.  
The execution of these queries can then return alternative results to the one initially returned.

> This flexible querying system has been integrated into several interfaces and systems, in particular in the context of the exploitation of the Henri Poincaré correspondence corpus.

## Purpose of this document

The current repository intends to describe the Java library containing the transformation engine.
To be used outside of Java, it is possible to use another Java application, which consists in a Web API exposing
 the functionalities of the system.
Use this [link](https://github.com/nlasolle/sqtre-web-api) for accessing the GitHub repository of this Web API.

## Licence

> This software is governed by the CeCILL licence. See licence.txt for details about this licence.

## References
To get information about the theoretical part of the system and its application to the Henri Poincaré correspondence corpus, please look at these articles:

> Bruneau, O., Lasolle, N., Lieber, J., Nauer, E., Pavlova, S., & Rollet, L. (2021). "Applying and Developing Semantic Web Technologies for Exploiting a Corpus in History of Science: the Case Study of the Henri Poincaré Correspondence". *Semantic Web – Interoperability, Usability, Applicability*. 12(2), (pp. 359-378). [10.3233/SW-200400](http://doi.org/10.3233/SW-200400).

> Bruneau, O., Gaillard, E., Lasolle, N., Lieber, J., Nauer, E., & Reynaud, J. (2017). "A SPARQL Query Transformation Rule Language — Application to
Retrieval and Adaptation in Case-Based Reasoning". *Case-Based Reasoning Research and Development. ICCBR 2017* (Trondheim, Norway). Edited by David Aha and Jean Lieber. Lecture Notes in Computer Science. Springer, Cham. (pp. 76-91). [10.1007/978-3-319-61030-6_6](http://doi.org/10.1007/978-3-319-61030-6_6).

> Lasolle, N., Bruneau, O., Lieber, J., Nabonnand, P., & Rollet, L. (2021). "A Semantic Web Navigation Tool for Exploring the Henri Poincaré Correspondence Corpus". *Proceedings of the International Joint Workshop on Semantic Web and Ontology Design for Cultural Heritage co-located with the Bolzano Summer of Knowledge 2021 (BOSK 2021)* (Bozen-Bolzano, Italie). Editeb by Antonis Bikakis, Roberta Ferrario, Stéphane Jean, Béatrice Markhoff, Alessandro Mosca and Marianna Nicolosi Asmundo. CEUR-WS.

> Lasolle, N., Bruneau, O., Lieber, J., Nauer, E., & Pavlova, S. (2020). "Assisting the RDF Annotation of a Digital Humanities Corpus using Case-Based Reasoning". *The Semantic Web-ISWC 2020*. Edited by Jeff Z. Pan, Valentina Tamma, Claudia d’Amato, Krzysztof Janowicz, Bo Fu, Axel Polleres, Oshani Seneviratne and Lalana Kagal. Springer, Cham. (pp. 617-633). DOI : [10.1007/978-3-030-62466-8_38](http://doi.org/10.1007/978-3-030-62466-8_38).

# Technical documentation
## Features
- Validation and loading of transformation rule files
- Application of transformation rules
- Management of cost based transformation processes
- Execution of SPARQL queries and saving of results
- Technical evaluation of the transformation engine

## Software requirements for developers 
### Java

To use the application in a development environment, it is required to download and install a JDK (Java development Kit). 

Java can be download online at [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

It is recommended to use an IDE (Integrated development environment). During the development, [Eclipse](https://www.eclipse.org/downloads/) was used but other exist such as [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=windows) or [NetBeans](https://netbeans.apache.org/download/index.html).

> Note: the application has been developed using Java 11, which was the more recent LTS (Long-Term Support) version at the time. Now, Java 17 is the latest LTS version available.

![](https://c.clc2l.com/t/j/a/java-N5uhyL.jpg)

### Maven
Apache Maven is a build tool which has been used for the management of dependencies, unit tests, code analysis and the archive creation.

The last stable version is available on the [Maven website](https://maven.apache.org/download.cgi).

Maven relies on the creation of a configuration file, named pom.xml,
 which contains information about the project and configuration details used by Maven to build the project.

![](https://maven.apache.org/images/maven-logo-black-on-white.png)

## Project architecture

### Maven pom.xml file and dependencies
The Java dependencies are defined in the pom.xml file, located at the project root. It lists all the libraries required for running the project.  
Here is an example, for the use of Apache Jena which is a free and open source Java framework for building Semantic Web applications:

```xml
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>apache-jena-libs</artifactId>
    <version>4.1.0</version>
</dependency>
```
Here is the list of dependencies used in the project, some of them required several project references in the pom.xml file:

| Name      | Description |
| ----------- | ----------- |
| JUnit      | Framework for writing and executing unit tests      |
| Apache Jena | Semantic Web library for executing SPARQL queries over the endpoint     |
| JDOM |  Framework for accessing, manipulating, and outputting XML data |
| Apache Log4j | Logging framework |
| SonarQube | Framework dedicated to code analysis, for detection of bugs, code smells and security hotspots |
| JSON | Framework for accessing and manipulating JSON data |


The Maven pom.xml file also gives the project archetype: 

```xml
<groupId>org.ahp</groupId>
<artifactId>sqtrl-engine</artifactId>
<version>1.0.1</version>
<packaging>jar</packaging>
```

It also include some plugins and other properties required by Maven for building the project.

### Java code source organization

The Java application is divided into 6 packages, all prefixed with `org.ahpo.sqtrlengine` and stored in the `src\main\java` repository: 

| Name      | Description |
| ----------- | ----------- |
| dao      | Data Access Object for managing the access to the RDF graph by executing SPARQL queries.      |
| evaluation | All Java classes related to the technical evaluation. This includes evaluation preparation, dedicated models classes for evaluation run parameters and results, and a results export class. |
| exception |  Project custom exceptions |
| model | Object gathering transformation process data |
| service | Class managing the transformation process and transformation rules load and applications |
| utils | Static classes for working with SPARQL queries and transformation rules |

### Unit tests

Several unit tests have been defined to check the behavior of the different classes in various situations.

All of these test classes are available in the `src\test\java` repository.
They rely on the use of the JUnit 5 framework, with the use of various annotation for defining the tests parameters.

### Resources 
#### Transformation rules

The `src\main\resources\validRules.xml` and  `src\main\resources\validRules.json` have been created for evaluation and unit tests purposes.
They both contain the same transformation rules under different syntax.
A transformation rule is defined by a set of attributes and fields. 
Some of them are mandatory (iri, label, context, left, right) while some are not (cost, exception, explanation).

Here is an example of an XML syntax transformation rule. The purpose of this rule is to generalize a Class appearing in a triple pattern within the SPARQL query. 

As an example, if one formulates a query to retrieve all mathematicians born in Nancy, the application of this rule could replace `Mathematician` by `Scientist` if the information that `Mathematician` is a subclass of `Scientist` is defined in the graph. 

```xml
<rule iri="http://sqtrl-rules/generic/1"
	  label="Generalize object class">
	<context>?C rdfs:subClassOf ?D</context>
	<left>?x ?p ?C</left>
	<right>?x ?p ?D</right>
	<exception>?C rdfs:subClassOf ?X . ?X rdfs:subClassOf ?D . 
	           FILTER(?C != ?X && ?X != ?D)</exception>
	<exception>FILTER(?C = ?D)</exception>
	<cost>5.0</cost>
	<explanation>Generalize ?C into ?D</explanation>
</rule>
```

A transformation file also contains the prefixes and associated namespaces related to the use of external classes and properties. 

```xml
<prefixes>
    <prefix label="rdf" iri="http://www.w3.org/1999/02/22-rdf-syntax-ns#"></prefix>
    <prefix label="rdfs" iri="http://www.w3.org/2000/01/rdf-schema#"></prefix>
    <prefix label="owl" iri="http://www.w3.org/2002/07/owl#"></prefix>
    <prefix label="ahpo" iri="http://e-hp.ahp-numerique.fr/ahpo#"></prefix>
    <prefix label="ahpot" iri="http://henripoincare.fr/ahpot"></prefix>
    <prefix label="dcterms" iri="http://purl.org/dc/terms/"></prefix>
</prefixes>
```
#### SPARQL queries
Several SPARQL queries have been defined for the evaluation process and for performing some unit tests.
They are available in the `src\main\resources\evaluation\queries\` folder and in the `src\main\test\resources\queries\`.

Here is an example query, related to the Henri Poincaré correspondence corpus : 

```sql
#Les lettres envoyées par Henri Poincaré à Gosta Mittag-Leffler 
# qui citent Charles Hermitte ayant pour thème la géométrie
PREFIX ahpo: <http://e-hp.ahp-numerique.fr/ahpo#>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT ?l 
WHERE {
    ?l ahpo:sentBy <http://henripoincare.fr/api/items/843> .
    ?l ahpo:sentTo <http://henripoincare.fr/api/items/452> .
    ?l ahpo:citeName <http://henripoincare.fr/api/items/333> .
    ?l dcterms:subject "Géométrie"
}
```

#### Log4j configuration file
The Log4j framework is used for logging management. 
The configuration is defined in the `src\main\resources\log4j2.xml` file. 
Here is its content:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="LogToConsole" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
         <File name="LogToFile" fileName="logs/app.log">
            <PatternLayout>
                <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
            </PatternLayout>
        </File>
    </Appenders>
    <Loggers>
        <Logger name="org.sqtrlLogger" level="info" additivity="false">
            <AppenderRef ref="LogToConsole"/>
            <AppenderRef ref="LogToFile"/>
        </Logger>
        <Root level="info">
            <AppenderRef ref="LogToFile"/>
            <AppenderRef ref="LogToConsole"/>
        </Root>
    </Loggers>
</Configuration>
```
The default log level is set to info, meaning that errors, warnings and informations will be printed on the log output.
The logs are printed in the  SYSTEM_OUT, which in our situation was the IDE console, and in the `logs\app.logs` file. 

> Log4j allow to defined pattern to put and organize information related to the context of the message.
 In our situation, the log file pattern is defined with  `%d %p %c{1.} [%t] %m%n`.
Information about the definition of patterns can be found [online](https://www.tutorialspoint.com/log4j/log4j_patternlayout.htm).

Here is an example of log line with information level:

```sql
2022-06-01 09:24:06,744 INFO o.a.s.RuleApplicationTest [main] 1 applications for rule http://sqtrl-rules/generic/1 and for query file queries/generic1.rq
```

# Acknowledgements 
This work is supported partly by the French PIA project
 "Lorraine Université d’Excellence", reference ANR-15-IDEX-04-LUE.