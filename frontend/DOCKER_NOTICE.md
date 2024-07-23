This application provides container images for demonstration purposes.

## Notice for Docker image

DockerHub Frontend: https://hub.docker.com/r/tractusx/traceability-foss-frontend

Eclipse Tractus-X product(s) installed within the image:

__Traceability-foss__

- GitHub: https://github.com/eclipse-tractusx/traceability-foss
- Project home: https://projects.eclipse.org/projects/automotive.tractusx
- Dockerfile Frontend: [Dockerfile](frontend/Dockerfile)
- Project license: [Apache License, Version 2.0](LICENSE)

**Used base image**

- [node:18-alpine](https://github.com/nodejs/docker-node)
- Official Node DockerHub page: https://hub.docker.com/_/node/
- Dockerfile: https://github.com/nodejs/docker-node/blob/main/18/alpine3.18/Dockerfile


- [nginxinc/nginx-unprivileged:alpine](https://github.com/nginxinc/docker-nginx)
- Official nginx DockerHub page: https://hub.docker.com/_/nginx
- Dockerfile: https://github.com/nginxinc/docker-nginx/blob/master/mainline/alpine/Dockerfile

As with all Docker images, these likely also contain other software which may be under other licenses (such as Bash, etc from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies with any relevant licenses for all software contained within.
