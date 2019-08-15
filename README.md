# Battlesnakes  

Simple battlesnakes.io snake app. Java 12 required 

Base code is taken from Nortals SU 2019 assignment. 

Algorithm: 
* Get moves that cant be occupied by bigger snakes if there are more than 1 enemy. WIll accept draw if there is only one enemy left and our sizes are the same
* Find kill moves
    * Kill moves are coordinates where a smaller snake can move and after I move there I can still move my body size from there on
* If no kill moves exist and there's no food on the board find a set of moves where I can move my body length
* If there's food on the board
    * See if I can get to the food before my enemies and still move my body length
* If nothing above works then just find a biggest set of moves that I can take


Between 21.04.19 - 30.04.19 this snake was ranked 6-10 in battlesnakes.io arena out of 150 snakes.  

## Local setup

The game engine can be run as a Docker container, through the command line, on Linux with:  
docker run --net="host" -it --rm siimveskilt/battlesnakes  

And on Windows and Mac with:  
docker run -it --rm -p 3010:3010 -p 3005:3005 -p 3009:3009 siimveskilt/battlesnakes    

Once the Docker container is up and running you should be able to access the engine when visiting: http://localhost:3010 with a modern browser  

When the application is running, Snake controllers will be available for Linux at the following endpoints:  
http://localhost:8080/smart  
http://localhost:8080/mad-genius  

And for Windows and Mac at:  
http://host.docker.internal:8080/smart  
http://host.docker.internal:8080/mad-genius
