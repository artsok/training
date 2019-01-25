Build URL with Path, Query, QueryMap. Small part from okhttp3

```java
   private static final class URIHandler {

        private final HttpUrl.Builder urlBuilder;

        URIHandler(final String url) {
            urlBuilder = HttpUrl.get(url).newBuilder();
        }
        
   }
   //
    private String buildURI(final String pathSegment, final Map<String, String> parameters) {
        urlBuilder.addPathSegments(pathSegment);
        parameters.forEach(urlBuilder::addQueryParameter);
        return urlBuilder.toString();
    }
   
```