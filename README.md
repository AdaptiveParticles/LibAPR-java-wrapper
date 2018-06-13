# LibAPR-java-wrapper


## How to download and build?

```
git clone --recurse https://github.com/krzysg/LibAPR-java-wrapper.git
cd LibAPR-java-wrapper
mvn package
```

## Need that project in IDE?
Import to Eclipse by running
```
File->Import->Maven->Existing Maven Projects
```
and point directory with pom.xml


## How to run example?
Provide correct file path and run:

```
BdvTest.java
```

two exemplary images are included in this repository in 'src/main/resources' so typical full path (if git clone was run in /tmp) would look like:
```
String filename = "/tmp/LibAPR-java-wrapper/src/main/resources/zebra.h5";
```

Example visualization with excellent [bigdataviewer-vistools](https://github.com/bigdataviewer/bigdataviewer-vistools):
![run with provided zebra.h5](src/main/resources/screen.png/?raw=true)
