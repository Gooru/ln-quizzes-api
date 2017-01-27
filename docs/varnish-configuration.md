Varnish Configuration
=============
This document describes the required configuration in order to setup a varnish instance to with the following specifications:
* Running on port 8082, serving pages from 8080
* Caching only requests containing `/v1/collections` or `/v1/assessments`
* Cache expiration time of 120 seconds


## Varnish installation

[Varnish download link](https://varnish-cache.org/releases/index.html)

Alternatively on ubuntu you can download it using apt-get
```
sudo apt-get update
sudo apt-get install varnish
```

## VCL Configuration

The default location for the vcl configuration file when installed on Ubuntu is: `/usr/share/varnish/reload-vcl`.  

Two points to note, the `backend default` should be pointing to the content server, and the code on the `vcl_recv` basically tell varnish to cache requests containing `/v1/collections/` or `/v1/assessments/` and pull from the content server any other request.
```
vcl 4.0;

# Default backend definition. Set this to point to your content server.
backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

# Happens before we check if we have this in cache already.
sub vcl_recv {
    if (req.url ~ "(/v1/collection/|/v1/assessments/)") {
        return (hash);
    }
    return (pass);
}

# Happens after we have read the response headers from the backend.
sub vcl_backend_response {
}

# Happens when we have all the pieces we need, and are about to send the
# response to the client.
sub vcl_deliver {
}

```

## Varnish service configuration

The other part of the configuration is the service execution, where we have to setup the cache expiration time for the cache records and update the port where varnish will be exposed. 

The following is a modified version of the default one created on linux, including the new -t parameter to setup the expiration time, and the updated -a parameter with the correct port:
```
[Unit]
Description=Varnish HTTP accelerator
Documentation=https://www.varnish-cache.org/docs/4.1/ man:varnishd

[Service]
Type=simple
LimitNOFILE=131072
LimitMEMLOCK=82000
ExecStart=/usr/sbin/varnishd -j unix,user=vcache -F -a :8082 -T localhost:6082 -f /etc/varnish/default.vcl -S /etc/varnish/secret -t 120 -s malloc,256m
ExecReload=/usr/share/varnish/reload-vcl
ProtectSystem=full
ProtectHome=true
PrivateTmp=true
PrivateDevices=true

[Install]
WantedBy=multi-user.target
```

According to the installation, you may start the varnish daemon manually, just make sure to add/update the required parameters:
`varnishd -a :8082 -T localhost:6082 -f /etc/varnish/default.vcl -S /etc/varnish/secret -t 120 -s malloc,256m`

## Testing

Once everything is installed and running you can test the api using the Varnish server instead of the content one, and you should see some extra headers:

```
⇒  http http://localhost:8082/quizzes/api/v1/collection/...
HTTP/1.1 200 OK
...
Age: 42
...
Via: 1.1 varnish-v4
X-Application-Context: application:8080
X-Varnish: 32826

```
