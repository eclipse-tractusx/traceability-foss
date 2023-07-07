## Notice for Docker image

DockerHub Backend: https://hub.docker.com/r/tractusx/traceability-foss

DockerHub Frontend : https://hub.docker.com/r/tractusx/traceability-foss-frontend

Eclipse Tractus-X product(s) installed within the image:

- GitHub: https://github.com/eclipse-tractusx/traceability-foss
- Project home: https://projects.eclipse.org/projects/automotive.tractusx
- Dockerfile Backend: https://github.com/eclipse-tractusx/traceability-foss/blob/main/Dockerfile
- Dockerfile Frontend: https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/Dockerfile
- Project license: [Apache License, Version 2.0](https://github.com/eclipse-tractusx/traceability-foss/blob/main/LICENSE)

**Used base image backend**

- [Official Eclipse Temurin DockerHub page](https://hub.docker.com/_/eclipse-temurin)
- [GitHub repo](https://github.com/adoptium/containers)
- [Eclipse Temurin Project](https://projects.eclipse.org/projects/adoptium.temurin)
- [Additional information about the Eclipse Temurin images](https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin)

**Used base image frontend**

- [nginx-unprivileged](https://hub.docker.com/r/nginxinc/nginx-unprivileged)
- [Dockerfile (alpine)](https://github.com/nginxinc/docker-nginx-unprivileged/blob/main/Dockerfile-alpine.template)
- [GitHub project:](https://github.com/nginxinc/docker-nginx-unprivileged)
- [DockerHub](https://hub.docker.com/r/nginxinc/nginx-unprivileged)

As with all Docker images, these likely also contain other software which may be under other licenses (such as Bash, etc from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies with any relevant licenses for all software contained within.
