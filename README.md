# Stretcher
A Java-based Concrete fetch and store service that uses the file system for storage.

## Sources
Stretcher supports loading communications from different types of sources.
The source is specified on the command line with the --input option.
All sources use the communication ID as the unique identifier.

### Directory
A flat directory containing uncompressed or gzipped files.
Uncompressed extensions supported: no extension, .comm, or .concrete
Compressed extensions supported: .gz, .comm.gz, or .concrete.gz

### Zip File
A single zip file can be used as a source.
The files in the zip file must be named id.comm

## Filters
A filter runs immediately after loading a communication from the file system.
Filters can remove unnecessary annotations like NER or PoS.
Filters are specified in the configuration file.
Do not use a filter if the same directory is used for both source and storage.

## Cache
If an application is likely to request the same communications repeatedly, a cache can be used.
The cache is specified in the configuration file.

## Stores
Stretcher currently only supports one storage method.
The store is specified on the command line with the --output option.

### Directory
If the source is a directory, the store will use the same filename convention.
If the source is a zip file, the store will save the files with the extension .gz

## Combiners
A combiner is used to integrate annotations from more than one annotator.
The combiner is specified in the configuration file.

## Build
```
mvn clean package
```

## Run
Stretcher automatically detects the format of the data given a path to a directory or file.

To run fetch and store from the directory /data on the default 9090 and 9091 ports:
```
./start.sh --input /data/
```
To not overwrite the original communication files:
```
./start.sh --input /data/input/ --output /data/output/
```

Serve Concrete objects from a zip file with fetch on port 8888:
```
./start.sh --input twitter.zip --fp 8888
```


## Docker
To build the image, run:
```
docker build -t hltcoe/stretcher .
```

To run the application with fetch on 8888 and store on 8989 out of the directory /opt/my_data:
```
docker run -d -v /opt/my_data:/data -p 8888:9090 -p 8989:9091 hltcoe/stretcher --input /data
```
