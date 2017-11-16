# Stretcher
A Java-based Concrete fetch and store service that uses the file system for storage.

## Build
```
mvn clean package
```

## Run
This is a work in progress.
Stretcher supports serving files in:
 * single zip archive
 * a directory uncompressed named by id with no extension, "comm", or "concrete"
 * a directory gzipped compressed named by id with extension "gz" or "comm.gz"

Note: using a zip archive is read-only. The store service will drop changes.

Stretcher automatically detects the format of the data given a path to a directory or file.

Serve files from the directory /data on the default 9090 port:
```
./start.sh --path /data/
```

Serve Concrete objects from a zip file on port 8888:
```
./start.sh --path twitter.zip --fp 8888
```


## Docker
To build the image, run:
```
docker build -t hltcoe/stretcher .
```

To run the application with fetch on 8888 and store on 8989 out of the directory /opt/my_data:
```
docker run -d -v /opt/my_data:/data -p 8888:9090 -p 8989:9091 hltcoe/stretcher --path /data
```
