# URL SHORTENER WEB SERVICE
This is a url shortener similar to tinyurl.com, bit.ly, goo.gl, etc. It can create a short *assigned url* for
any *original url* provided via `POST`. Later requests for the *assigned url* will be redirected to the *original url*.

### REQUIREMENTS

  * Given an *original* url on any domain the shortener webservice produces an associated *assigned* url
    in the application domain (e.g. "http://www.google.com" could be associated with "http://{domain}/XXXXX"
    where {domain} is the application domain)
    * Only authenticated requests to this function will be respected
    * There exists exactly one assigned url for each original url and vice versa
       * Trailing '/' will not be considered
          * e.g. "foo.com" and "foo.com/" will be assigned the same url
       * Urls that only differ in their protocols will still be assigned different urls
          * e.g. "http://google.com" and "https://google.com" will be assigned different urls
      * The webservice attempts to produce relatively short assigned urls

  * When a previously assigned url is requested from the webservice, the user's browser
    is redirected to the associated original url

### SPECIFICATIONS

  * `POST   /shorten`
    * Client requests should provide:
      * Correct HTTP Basic Auth credentials
      * x-www-form-urlencoded request body
      * A parameter named *originalUrl* that contains the url string to be "shortened"
    * The webservice will respond:
      * 401 (Unauthorized) if the credentials were incorrect
      * 200 (Ok) if the request was succesfully serviced. The response body only contains the assigned url

  * `GET  {assignedUrlPath}`
    * Client should request the path of an assigned url that was previously produced by a call to `/shorten`
    * The webservice will respond:
      * 404 (Not Found) if the url with the path *assignedUrlPath* on the application domain does not map to an
        original url
      * 302 (Found) if *assignedUrlPath* was associated with an original url previously provided via `/shorten`.
        This 302 is a redirect to the original url.