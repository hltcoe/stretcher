# stretcher
A Java-based Concrete fetch and store service that uses the file system for storage.

## Build
```
mvn clean package
```

## Run
This is a work in progress.
stretcher currently only supports a directory of Concrete objects with names [id].comm.
Additional support for gzipped files, zipped archives, tarballs, and alternate naming schemes will be coming.
We will also be adding higher throughput components and new features like filtering data before returning fetch results.

```
./start.sh -d [directory that contains communications]
```
## Docker
To build the image, run:
```
docker build -t hltcoe/stretcher .
```

To run the application with fetch on 8888 and store on 8989 out of the directory /opt/my_data:
```
docker run -d -v /opt/my_data:/data -p 8888:9090 -p 8989:9091 hltcoe/stretcher -d /data
```
