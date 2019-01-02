## restHttpServer
restHttpServer is a lightweight HTTP server base on Netty, target for RESTFul API.

[中文版](http://ndrlslz.github.io/2018/09/26/rest-http-server/)

## Features
* NIO
* Router based on method and path
* Regular expression pattern matching for paths
* Path parameter support
* KeepAlive support
* Exception handle and readable json response

## Quick Start

Running below code to start a HTTP server.
```
RouterTable routerTable = new RouterTable();

RestHttpServer
        .create()
        .requestHandler(routerTable)
        .listen(8080);
```

Also you can define the routers like below
```
routerTable.get("/hi").handler(context -> context.response().setBody("hello world"));
```

## Detailed Documentation
`RouterTable` is the core concept of restHttpServer, which stores all routers.
Router likes a mapping between `path & method` and `handler`.

### PATH & METHOD
RouterTable support basic HTTP METHOD and it's able to dispatch the request via different path and method. below are some examples
```
//Support different http method
routerTable.get("/hi").handler(context -> { });
routerTable.post("/hi").handler(context -> { });
routerTable.delete("/hi").handler(context -> { });
routerTable.put("/hi").handler(context -> { });
routerTable.patch("/hi").handler(context -> { });

//Support path parameters
routerTable.get("/customers/{id}").handler(context -> { });

//Regular expressions can be used to match path.
routerTable.get("/hey.*").handler(context -> { });
```

### Handler
The object that gets passed into the handler is a RouterContext, which stores both request and response. below are some examples about how to use it.

```
routerTable.get("/customers/{id}/contacts").handler(context -> {
    HttpServerRequest request = context.request();
    HttpServerResponse response = context.response();

    request.getQueryParams();              //retrieve query parameters
    request.getPathParams().get("id");     //retrieve path parameter
    request.headers().get("Content-Type"); //retrieve headers
    request.getBodyAsString();             //retrieve request body

    response.setBody("hello, world}");     //set response body
    response.headers().set("key", "value");//set header
});
```

## CRUD Example
you can find a CRUD example [here](./examples)

## Benchmark

Mac(Intel Core i7/2.2GHz)
```
wrk -t4 -c150 -d30s http://localhost:10080/test

Running 30s test @ http://localhost:10080/test
  4 threads and 150 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.39ms  423.62us  36.46ms   95.61%
    Req/Sec    15.52k     1.22k   51.71k    95.50%
  1855064 requests in 30.10s, 187.53MB read
Requests/sec:  61627.08
Transfer/sec:      6.23MB
```








