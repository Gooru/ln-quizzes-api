vcl 4.0;

# Default backend definition. Set this to point to your content server.
backend default {
    .host = "127.0.0.1";
    .port = "8080";
}

# Happens before we check if we have this in cache already.
sub vcl_recv {
    if (req.url ~ "(/v1/collections/|/v1/assessments/)") {
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
