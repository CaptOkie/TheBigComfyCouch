# TheBigComfyCouch
Synchronized video streaming

# Requirements
* Either: _Oracle JDK 8 with JavaFX_ (JavaFX should be included in JDK 8) or _OpenJDK 8 with OpenJFX_
* Maven

# Eclipse Setup
1. Import as a Maven project
2. Right click the project > Maven > Update project
3. Right click the project > Properties > Java Build Path > Libraries
4. Expand JRE System Library > Access rules > Edit > Add
5. Set __Resolution__ = Accessible & __Rule Pattern__ = javafx/**
6. Ok > Ok > Ok
7. Repeat steps 3 - 6 each time a maven update is performed
