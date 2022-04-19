sbt "clean;test;Docker/publishLocal"
echo "Docker image is built. Going to run the docker image"
docker run harry-potter-elixir-blender:0.1