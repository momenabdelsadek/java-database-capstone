// build image
docker build -t smart-clinic-backend 

// run container 
docker run -d -p 8080:8080 --name smart-clinic smart-clinic-backend

// stop container
docker stop smart-clinic

// remove container 
docker rm smart-clinic

// remove image 
docker rmi smart-clinic-backend


## Push to a Container Registry

// prepare image to push using docker hub username
docker tag smart-clinic-backend your-docker-username/smart-clinic-backend:latest

// login 
docker login

// push the image
docker push your-docker-username/smart-clinic-backend:latest