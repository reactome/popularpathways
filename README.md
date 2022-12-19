[<img src=https://user-images.githubusercontent.com/6883670/31999264-976dfb86-b98a-11e7-9432-0316345a72ea.png height=75 />](https://reactome.org)

# Reactome Popular Pathways

## What is the Reactome Popular Pathways

The aim of the project is to visualise the relative number of web hits each pathway receives, together with the last update date of the pathway. Together, these two dimensions should give internal guidance on curation priorities and potential funding applications.

#### Installation Guide

* Pre-Requirement (in the given order)
    1. Maven 3.X - [Installation Guide](http://maven.apache.org/install.html)
    2. Reactome Graph Database - [Installation Guide](https://reactome.org/dev/graph-database/)
 
##### Git Clone

```console
git clone https://github.com/Chuqiaoo/popularpathways.git
cd popularpathways
```

##### Configuring Maven Profile :memo:

Maven Profile is a set of configuration values which can be used to set or override default values of Maven build. Using a build profile, you can customize build for different environments such as Production v/s Development environments.
Add the following code-snippet containing all the Reactome properties inside the tag ```<profiles>``` into your ```~/.m2/settings.xml```.
Please refer to Maven Profile [Guideline](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) if you don't have settings.xml

```html
<profile>
    <id>reactome</id>
    <properties>
 
        <!-- Neo4J Configuration -->
        <neo4j.uri>bolt://localhost:7687</neo4j.uri>
        <neo4j.user>neo4j</neo4j.user>
        <neo4j.password>password</neo4j.password>

        <!-- Common folders and file locations -->
        <popularpathway.folder>/usr/local/reactome/Reactome/popularpathways</popularpathway.folder>
   
    </properties>
</profile>
```

##### Running popularpathways activating ```reactome``` profile.
```console
mvn spring-boot:run -P reactome
```

Check if Tomcat has been initialised
```rb
[INFO] Using existing Tomcat server configuration at /Users/reactome/popularpathways/target/tomcat
INFO: Starting ProtocolHandler ["http-bio-8686"]
```

#### Result

* Access your local [installation](http://localhost:8686/) and [upload](http://localhost:8686/upload) your log file.

<img width="900" alt="Reacfoam_dev" src="https://user-images.githubusercontent.com/6442828/76324276-ae501600-62dd-11ea-8db6-929056649151.png">

