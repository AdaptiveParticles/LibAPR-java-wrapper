# LibAPR-java-wrapper

Wrapper for [LibAPR](https://github.com/cheesema/LibAPR) library.

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
![run with provided zebra.h5](src/test/resources/screen.png/?raw=true)


## Contact us

If anything is not working as you think it should, or would like it to, please get in touch with us!!!

[![Join the chat at https://gitter.im/LibAPR](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/LibAPR/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
