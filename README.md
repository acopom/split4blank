# split4blank
This tool splits a RDF dataset into multiple smaller files under the condition
that identical blank nodes are not separated.

# How to use the tool
java -jar -Xmx16g -Xms16g split4blank.jar targetfile numberOfFiles

## Docker build

```shell
docker build -t split4blank .
```

## Docker run

Put your n-triples file in the `/data` repository, and use it as shared volume.

```shell
docker run -it -v /data:/data split4blank example.nt 10
```

