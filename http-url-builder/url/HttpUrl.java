package io.qameta.atlas.webdriver.util.http.url;


import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;


/**
 * A uniform resource locator (URL) with a scheme of either {@code http} or {@code https}. Use this
 * class to compose and decompose Internet addresses. For example, this code will compose and print
 * a URL for Google search: <pre>   {@code
 *
 *   HttpUrl url = new HttpUrl.Builder()
 *       .scheme("https")
 *       .host("www.google.com")
 *       .addPathSegment("search")
 *       .addQueryParameter("q", "polar bears")
 *       .build();
 *   System.out.println(url);
 * }</pre>
 */
public final class HttpUrl {
    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Either "http" or "https".
     */
    final String scheme;

    /**
     * Decoded username.
     */
    final String username;

    /**
     * Decoded password.
     */
    final String password;

    /**
     * Canonical hostname.
     */
    final String host;

    /**
     * Either 80, 443 or a user-specified port. In range [1..65535].
     */
    final int port;

    /**
     * A list of canonical path segments. This list always contains at least one element, which may be
     * the empty string. Each segment is formatted with a leading '/', so if path segments were ["a",
     * "b", ""], then the encoded path would be "/a/b/".
     */
    private final List<String> pathSegments;

    /**
     * Alternating, decoded query names and values, or null for no query. Names may be empty or
     * non-empty, but never null. Values are null if the name has no corresponding '=' separator, or
     * empty, or non-empty.
     */
    private final List<String> queryNamesAndValues;

    /** Decoded fragment. */
    //private final   String fragment;

    /**
     * Canonical URL.
     */
    private final String url;

    HttpUrl(Builder builder) {
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        this.queryNamesAndValues = builder.encodedQueryNamesAndValues != null
                ? percentDecode(builder.encodedQueryNamesAndValues, true)
                : null;
//        this.fragment = builder.encodedFragment != null
//                ? percentDecode(builder.encodedFragment, false)
//                : null;
        this.url = builder.toString();
    }

    /**
     * Returns this URL as a {@link URL java.net.URL}.
     */
    public URL url() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // Unexpected!
        }
    }

    /**
     * Returns this URL as a {@link URI java.net.URI}. Because {@code URI} is more strict than this
     * class, the returned URI may be semantically different from this URL:
     *
     * <ul>
     * <li>Characters forbidden by URI like {@code [} and {@code |} will be escaped.
     * <li>Invalid percent-encoded sequences like {@code %xx} will be encoded like {@code %25xx}.
     * <li>Whitespace and control characters in the fragment will be stripped.
     * </ul>
     *
     * <p>These differences may have a significant consequence when the URI is interpreted by a
     * webserver. For this reason the {@linkplain URI URI class} and this method should be avoided.
     */
    public URI uri() {
        String uri = newBuilder().reencodeForUri().toString();
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            // Unlikely edge case: the URI has a forbidden character in the fragment. Strip it & retry.
            try {
                String stripped = uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", "");
                return URI.create(stripped);
            } catch (Exception e1) {
                throw new RuntimeException(e); // Unexpected!
            }
        }
    }

    /**
     * Returns either "http" or "https".
     */
    public String scheme() {
        return scheme;
    }

    public boolean isHttps() {
        return scheme.equals("https");
    }

    //    /**
//     * Returns the username, or an empty string if none is set.
//     *
//     * <p><table summary="">
//     *   <tr><th>URL</th><th>{@code encodedUsername()}</th></tr>
//     *   <tr><td>{@code http://host/}</td><td>{@code ""}</td></tr>
//     *   <tr><td>{@code http://username@host/}</td><td>{@code "username"}</td></tr>
//     *   <tr><td>{@code http://username:password@host/}</td><td>{@code "username"}</td></tr>
//     *   <tr><td>{@code http://a%20b:c%20d@host/}</td><td>{@code "a%20b"}</td></tr>
//     * </table>
//     */
    public String encodedUsername() {
        if (username.isEmpty()) return "";
        int usernameStart = scheme.length() + 3; // "://".length() == 3.
        int usernameEnd = delimiterOffset(url, usernameStart, url.length(), ":@");
        return url.substring(usernameStart, usernameEnd);
    }

    //    /**
//     * Returns the decoded username, or an empty string if none is present.
//     *
//     * <p><table summary="">
//     *   <tr><th>URL</th><th>{@code username()}</th></tr>
//     *   <tr><td>{@code http://host/}</td><td>{@code ""}</td></tr>
//     *   <tr><td>{@code http://username@host/}</td><td>{@code "username"}</td></tr>
//     *   <tr><td>{@code http://username:password@host/}</td><td>{@code "username"}</td></tr>
//     *   <tr><td>{@code http://a%20b:c%20d@host/}</td><td>{@code "a b"}</td></tr>
//     * </table>
//     */
    public String username() {
        return username;
    }

    public String encodedPassword() {
        if (password.isEmpty()) return "";
        int passwordStart = url.indexOf(':', scheme.length() + 3) + 1;
        int passwordEnd = url.indexOf('@');
        return url.substring(passwordStart, passwordEnd);
    }

    public String password() {
        return password;
    }

    /**
     * Returns the host address suitable for use with {@link InetAddress#getAllByName(String)}. May
     * be:
     *
     * <ul>
     * <li>A regular host name, like {@code android.com}.
     * <li>An IPv4 address, like {@code 127.0.0.1}.
     * <li>An IPv6 address, like {@code ::1}. Note that there are no square braces.
     * <li>An encoded IDN, like {@code xn--n3h.net}.
     * </ul>
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code host()}</th></tr>
     * <tr><td>{@code http://android.com/}</td><td>{@code "android.com"}</td></tr>
     * <tr><td>{@code http://127.0.0.1/}</td><td>{@code "127.0.0.1"}</td></tr>
     * <tr><td>{@code http://[::1]/}</td><td>{@code "::1"}</td></tr>
     * <tr><td>{@code http://xn--n3h.net/}</td><td>{@code "xn--n3h.net"}</td></tr>
     * </table>
     */
    public String host() {
        return host;
    }

    /**
     * Returns the explicitly-specified port if one was provided, or the default port for this URL's
     * scheme. For example, this returns 8443 for {@code https://square.com:8443/} and 443 for {@code
     * https://square.com/}. The result is in {@code [1..65535]}.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code port()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code 80}</td></tr>
     * <tr><td>{@code http://host:8000/}</td><td>{@code 8000}</td></tr>
     * <tr><td>{@code https://host/}</td><td>{@code 443}</td></tr>
     * </table>
     */
    public int port() {
        return port;
    }

    /**
     * Returns 80 if {@code scheme.equals("http")}, 443 if {@code scheme.equals("https")} and -1
     * otherwise.
     */
    public static int defaultPort(String scheme) {
        if (scheme.equals("http")) {
            return 80;
        } else if (scheme.equals("https")) {
            return 443;
        } else {
            return -1;
        }
    }

    /**
     * Returns the number of segments in this URL's path. This is also the number of slashes in the
     * URL's path, like 3 in {@code http://host/a/b/c}. This is always at least 1.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code pathSize()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code 1}</td></tr>
     * <tr><td>{@code http://host/a/b/c}</td><td>{@code 3}</td></tr>
     * <tr><td>{@code http://host/a/b/c/}</td><td>{@code 4}</td></tr>
     * </table>
     */
    public int pathSize() {
        return pathSegments.size();
    }

    /**
     * Returns the entire path of this URL encoded for use in HTTP resource resolution. The returned
     * path will start with {@code "/"}.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code encodedPath()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code "/"}</td></tr>
     * <tr><td>{@code http://host/a/b/c}</td><td>{@code "/a/b/c"}</td></tr>
     * <tr><td>{@code http://host/a/b%20c/d}</td><td>{@code "/a/b%20c/d"}</td></tr>
     * </table>
     */
    public String encodedPath() {
        int pathStart = url.indexOf('/', scheme.length() + 3); // "://".length() == 3.
        int pathEnd = delimiterOffset(url, pathStart, url.length(), "?#");
        return url.substring(pathStart, pathEnd);
    }

    static void pathSegmentsToString(StringBuilder out, List<String> pathSegments) {
        for (int i = 0, size = pathSegments.size(); i < size; i++) {
            out.append('/');
            out.append(pathSegments.get(i));
        }
    }

    /**
     * Returns a list of encoded path segments like {@code ["a", "b", "c"]} for the URL {@code
     * http://host/a/b/c}. This list is never empty though it may contain a single empty string.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code encodedPathSegments()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code [""]}</td></tr>
     * <tr><td>{@code http://host/a/b/c}</td><td>{@code ["a", "b", "c"]}</td></tr>
     * <tr><td>{@code http://host/a/b%20c/d}</td><td>{@code ["a", "b%20c", "d"]}</td></tr>
     * </table>
     */
    public List<String> encodedPathSegments() {
        int pathStart = url.indexOf('/', scheme.length() + 3);
        int pathEnd = delimiterOffset(url, pathStart, url.length(), "?#");
        List<String> result = new ArrayList<>();
        for (int i = pathStart; i < pathEnd; ) {
            i++; // Skip the '/'.
            int segmentEnd = delimiterOffset(url, i, pathEnd, '/');
            result.add(url.substring(i, segmentEnd));
            i = segmentEnd;
        }
        return result;
    }

    /**
     * Returns a list of path segments like {@code ["a", "b", "c"]} for the URL {@code
     * http://host/a/b/c}. This list is never empty though it may contain a single empty string.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code pathSegments()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code [""]}</td></tr>
     * <tr><td>{@code http://host/a/b/c"}</td><td>{@code ["a", "b", "c"]}</td></tr>
     * <tr><td>{@code http://host/a/b%20c/d"}</td><td>{@code ["a", "b c", "d"]}</td></tr>
     * </table>
     */
    public List<String> pathSegments() {
        return pathSegments;
    }

    /**
     * Returns the query of this URL, encoded for use in HTTP resource resolution. The returned string
     * may be null (for URLs with no query), empty (for URLs with an empty query) or non-empty (all
     * other URLs).
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code encodedQuery()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>null</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code ""}</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code
     * "a=apple&k=key+lime"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code "a=apple&a=apricot"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code "a=apple&b"}</td></tr>
     * </table>
     */
    public String encodedQuery() {
        if (queryNamesAndValues == null) return null; // No query.
        int queryStart = url.indexOf('?') + 1;
        int queryEnd = delimiterOffset(url, queryStart, url.length(), '#');
        return url.substring(queryStart, queryEnd);
    }

    static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        for (int i = 0, size = namesAndValues.size(); i < size; i += 2) {
            String name = namesAndValues.get(i);
            String value = namesAndValues.get(i + 1);
            if (i > 0) out.append('&');
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }

    /**
     * Cuts {@code encodedQuery} up into alternating parameter names and values. This divides a query
     * string like {@code subject=math&easy&problem=5-2=3} into the list {@code ["subject", "math",
     * "easy", null, "problem", "5-2=3"]}. Note that values may be null and may contain '='
     * characters.
     */
    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList<>();
        for (int pos = 0; pos <= encodedQuery.length(); ) {
            int ampersandOffset = encodedQuery.indexOf('&', pos);
            if (ampersandOffset == -1) ampersandOffset = encodedQuery.length();

            int equalsOffset = encodedQuery.indexOf('=', pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add(null); // No value for this name.
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    /**
     * Returns this URL's query, like {@code "abc"} for {@code http://host/?abc}. Most callers should
     * prefer {@link #queryParameterName} and {@link #queryParameterValue} because these methods offer
     * direct access to individual query parameters.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code query()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>null</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code ""}</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code "a=apple&k=key
     * lime"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code "a=apple&a=apricot"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code "a=apple&b"}</td></tr>
     * </table>
     */
    public String query() {
        if (queryNamesAndValues == null) return null; // No query.
        StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, queryNamesAndValues);
        return result.toString();
    }

    /**
     * Returns the number of query parameters in this URL, like 2 for {@code
     * http://host/?a=apple&b=banana}. If this URL has no query this returns 0. Otherwise it returns
     * one more than the number of {@code "&"} separators in the query.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code querySize()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code 0}</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code 1}</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code 2}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code 2}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code 2}</td></tr>
     * </table>
     */
    public int querySize() {
        return queryNamesAndValues != null ? queryNamesAndValues.size() / 2 : 0;
    }

    /**
     * Returns the first query parameter named {@code name} decoded using UTF-8, or null if there is
     * no such query parameter.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code queryParameter("a")}</th></tr>
     * <tr><td>{@code http://host/}</td><td>null</td></tr>
     * <tr><td>{@code http://host/?}</td><td>null</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code "apple"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code "apple"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code "apple"}</td></tr>
     * </table>
     */
    public String queryParameter(String name) {
        if (queryNamesAndValues == null) return null;
        for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
            if (name.equals(queryNamesAndValues.get(i))) {
                return queryNamesAndValues.get(i + 1);
            }
        }
        return null;
    }

    /**
     * Returns the distinct query parameter names in this URL, like {@code ["a", "b"]} for {@code
     * http://host/?a=apple&b=banana}. If this URL has no query this returns the empty set.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code queryParameterNames()}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code []}</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code [""]}</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code ["a", "k"]}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code ["a"]}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code ["a", "b"]}</td></tr>
     * </table>
     */
    public Set<String> queryParameterNames() {
        if (queryNamesAndValues == null) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
            result.add(queryNamesAndValues.get(i));
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns all values for the query parameter {@code name} ordered by their appearance in this
     * URL. For example this returns {@code ["banana"]} for {@code queryParameterValue("b")} on {@code
     * http://host/?a=apple&b=banana}.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code queryParameterValues("a")}</th><th>{@code
     * queryParameterValues("b")}</th></tr>
     * <tr><td>{@code http://host/}</td><td>{@code []}</td><td>{@code []}</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code []}</td><td>{@code []}</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code ["apple"]}</td><td>{@code
     * []}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code ["apple",
     * "apricot"]}</td><td>{@code []}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code ["apple"]}</td><td>{@code
     * [null]}</td></tr>
     * </table>
     */
    public List<String> queryParameterValues(String name) {
        if (queryNamesAndValues == null) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        for (int i = 0, size = queryNamesAndValues.size(); i < size; i += 2) {
            if (name.equals(queryNamesAndValues.get(i))) {
                result.add(queryNamesAndValues.get(i + 1));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the name of the query parameter at {@code index}. For example this returns {@code "a"}
     * for {@code queryParameterName(0)} on {@code http://host/?a=apple&b=banana}. This throws if
     * {@code index} is not less than the {@linkplain #querySize query size}.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code queryParameterName(0)}</th><th>{@code
     * queryParameterName(1)}</th></tr>
     * <tr><td>{@code http://host/}</td><td>exception</td><td>exception</td></tr>
     * <tr><td>{@code http://host/?}</td><td>{@code ""}</td><td>exception</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code "a"}</td><td>{@code
     * "k"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code "a"}</td><td>{@code
     * "a"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code "a"}</td><td>{@code "b"}</td></tr>
     * </table>
     */
    public String queryParameterName(int index) {
        if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
        return queryNamesAndValues.get(index * 2);
    }

    /**
     * Returns the value of the query parameter at {@code index}. For example this returns {@code
     * "apple"} for {@code queryParameterName(0)} on {@code http://host/?a=apple&b=banana}. This
     * throws if {@code index} is not less than the {@linkplain #querySize query size}.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code queryParameterValue(0)}</th><th>{@code
     * queryParameterValue(1)}</th></tr>
     * <tr><td>{@code http://host/}</td><td>exception</td><td>exception</td></tr>
     * <tr><td>{@code http://host/?}</td><td>null</td><td>exception</td></tr>
     * <tr><td>{@code http://host/?a=apple&k=key+lime}</td><td>{@code "apple"}</td><td>{@code
     * "key lime"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&a=apricot}</td><td>{@code "apple"}</td><td>{@code
     * "apricot"}</td></tr>
     * <tr><td>{@code http://host/?a=apple&b}</td><td>{@code "apple"}</td><td>null</td></tr>
     * </table>
     */
    public String queryParameterValue(int index) {
        if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
        return queryNamesAndValues.get(index * 2 + 1);
    }

//    /**
//     * Returns this URL's encoded fragment, like {@code "abc"} for {@code http://host/#abc}. This
//     * returns null if the URL has no fragment.
//     *
//     * <p><table summary="">
//     *   <tr><th>URL</th><th>{@code encodedFragment()}</th></tr>
//     *   <tr><td>{@code http://host/}</td><td>null</td></tr>
//     *   <tr><td>{@code http://host/#}</td><td>{@code ""}</td></tr>
//     *   <tr><td>{@code http://host/#abc}</td><td>{@code "abc"}</td></tr>
//     *   <tr><td>{@code http://host/#abc|def}</td><td>{@code "abc|def"}</td></tr>
//     * </table>
//     */
//    public   String encodedFragment() {
//        if (fragment == null) return null;
//        int fragmentStart = url.indexOf('#') + 1;
//        return url.substring(fragmentStart);
//    }
//
//    /**
//     * Returns this URL's fragment, like {@code "abc"} for {@code http://host/#abc}. This returns null
//     * if the URL has no fragment.
//     *
//     * <p><table summary="">
//     *   <tr><th>URL</th><th>{@code fragment()}</th></tr>
//     *   <tr><td>{@code http://host/}</td><td>null</td></tr>
//     *   <tr><td>{@code http://host/#}</td><td>{@code ""}</td></tr>
//     *   <tr><td>{@code http://host/#abc}</td><td>{@code "abc"}</td></tr>
//     *   <tr><td>{@code http://host/#abc|def}</td><td>{@code "abc|def"}</td></tr>
//     * </table>
//     */
//    public   String fragment() {
//        return fragment;
//    }

    /**
     * Returns a string with containing this URL with its username, password, query, and fragment
     * stripped, and its path replaced with {@code /...}. For example, redacting {@code
     * http://username:password@example.com/path} returns {@code http://example.com/...}.
     */
    public String redact() {
        return newBuilder("/...")
                .username("")
                .password("")
                .build()
                .toString();
    }

    /**
     * Returns the URL that would be retrieved by following {@code link} from this URL, or null if
     * the resulting URL is not well-formed.
     */
    public HttpUrl resolve(String link) {
        Builder builder = newBuilder(link);
        return builder != null ? builder.build() : null;
    }

    public Builder newBuilder() {
        Builder result = new Builder();
        result.scheme = scheme;
        result.encodedUsername = encodedUsername();
        result.encodedPassword = encodedPassword();
        result.host = host;
        // If we're set to a default port, unset it in case of a scheme change.
        result.port = port != defaultPort(scheme) ? port : -1;
        result.encodedPathSegments.clear();
        result.encodedPathSegments.addAll(encodedPathSegments());
        result.encodedQuery(encodedQuery());
        return result;
    }

    /**
     * Returns a builder for the URL that would be retrieved by following {@code link} from this URL,
     * or null if the resulting URL is not well-formed.
     */
    public Builder newBuilder(String link) {
        try {
            return new Builder().parse(this, link);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Returns a new {@code HttpUrl} representing {@code url} if it is a well-formed HTTP or HTTPS
     * URL, or null if it isn't.
     */
    public static HttpUrl parse(String url) {
        try {
            return get(url);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Returns a new {@code HttpUrl} representing {@code url}.
     *
     * @throws IllegalArgumentException If {@code url} is not a well-formed HTTP or HTTPS URL.
     */
    public static HttpUrl get(String url) {
        return new Builder().parse(null, url).build();
    }

    /**
     * Returns an {@link HttpUrl} for {@code url} if its protocol is {@code http} or {@code https}, or
     * null if it has any other protocol.
     */
    public static HttpUrl get(URL url) {
        return parse(url.toString());
    }

    public static HttpUrl get(URI uri) {
        return parse(uri.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof HttpUrl && ((HttpUrl) other).url.equals(url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return url;
    }

    /**
     * Returns the domain name of this URL's {@link #host()} that is one level beneath the public
     * suffix by consulting the <a href="https://publicsuffix.org">public suffix list</a>. Returns
     * null if this URL's {@link #host()} is an IP address or is considered a public suffix by the
     * public suffix list.
     *
     * <p>In general this method <strong>should not</strong> be used to test whether a domain is valid
     * or routable. Instead, DNS is the recommended source for that information.
     *
     * <p><table summary="">
     * <tr><th>URL</th><th>{@code topPrivateDomain()}</th></tr>
     * <tr><td>{@code http://google.com}</td><td>{@code "google.com"}</td></tr>
     * <tr><td>{@code http://adwords.google.co.uk}</td><td>{@code "google.co.uk"}</td></tr>
     * <tr><td>{@code http://square}</td><td>null</td></tr>
     * <tr><td>{@code http://co.uk}</td><td>null</td></tr>
     * <tr><td>{@code http://localhost}</td><td>null</td></tr>
     * <tr><td>{@code http://127.0.0.1}</td><td>null</td></tr>
     * </table>
     */
//    public   String topPrivateDomain() {
//        if (verifyAsIpAddress(host)) return null;
//        return PublicSuffixDatabase.get().getEffectiveTldPlusOne(host);
//    }

    public static final class Builder {

        String scheme;
        String encodedUsername = "";
        String encodedPassword = "";
        String host;
        int port = -1;
        final List<String> encodedPathSegments = new ArrayList<>();

        List<String> encodedQueryNamesAndValues;

        String encodedFragment;

        public Builder() {
            encodedPathSegments.add(""); // The default path is '/' which needs a trailing space.
        }

        public Builder scheme(String scheme) {
            if (scheme == null) {
                throw new NullPointerException("scheme == null");
            } else if (scheme.equalsIgnoreCase("http")) {
                this.scheme = "http";
            } else if (scheme.equalsIgnoreCase("https")) {
                this.scheme = "https";
            } else {
                throw new IllegalArgumentException("unexpected scheme: " + scheme);
            }
            return this;
        }

        public Builder username(String username) {
            if (username == null) throw new NullPointerException("username == null");
            this.encodedUsername = canonicalize(username, USERNAME_ENCODE_SET, false, false, false, true);
            return this;
        }

        public Builder encodedUsername(String encodedUsername) {
            if (encodedUsername == null) throw new NullPointerException("encodedUsername == null");
            this.encodedUsername = canonicalize(
                    encodedUsername, USERNAME_ENCODE_SET, true, false, false, true);
            return this;
        }

        public Builder password(String password) {
            if (password == null) throw new NullPointerException("password == null");
            this.encodedPassword = canonicalize(password, PASSWORD_ENCODE_SET, false, false, false, true);
            return this;
        }

        public Builder encodedPassword(String encodedPassword) {
            if (encodedPassword == null) throw new NullPointerException("encodedPassword == null");
            this.encodedPassword = canonicalize(
                    encodedPassword, PASSWORD_ENCODE_SET, true, false, false, true);
            return this;
        }

        /**
         * @param host either a regular hostname, International Domain Name, IPv4 address, or IPv6
         *             address.
         */
        public Builder host(String host) {
            if (host == null) throw new NullPointerException("host == null");
            // String encoded = canonicalizeHost(host, 0, host.length());
            String encoded = "";
            if (encoded == null) throw new IllegalArgumentException("unexpected host: " + host);
            this.host = encoded;
            return this;
        }

        public Builder port(int port) {
            if (port <= 0 || port > 65535) throw new IllegalArgumentException("unexpected port: " + port);
            this.port = port;
            return this;
        }

        int effectivePort() {
            return port != -1 ? port : defaultPort(scheme);
        }

        public Builder addPathSegment(String pathSegment) {
            if (pathSegment == null) throw new NullPointerException("pathSegment == null");
            push(pathSegment, 0, pathSegment.length(), false, false);
            return this;
        }

        /**
         * Adds a set of path segments separated by a slash (either {@code \} or {@code /}). If
         * {@code pathSegments} starts with a slash, the resulting URL will have empty path segment.
         */
        public Builder addPathSegments(String pathSegments) {
            if (pathSegments == null) throw new NullPointerException("pathSegments == null");
            return addPathSegments(pathSegments, false);
        }

        public Builder addEncodedPathSegment(String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
            return this;
        }

        /**
         * Adds a set of encoded path segments separated by a slash (either {@code \} or {@code /}). If
         * {@code encodedPathSegments} starts with a slash, the resulting URL will have empty path
         * segment.
         */
        public Builder addEncodedPathSegments(String encodedPathSegments) {
            if (encodedPathSegments == null) {
                throw new NullPointerException("encodedPathSegments == null");
            }
            return addPathSegments(encodedPathSegments, true);
        }

        private Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
            int offset = 0;
            do {
                int segmentEnd = delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
                boolean addTrailingSlash = segmentEnd < pathSegments.length();
                push(pathSegments, offset, segmentEnd, addTrailingSlash, alreadyEncoded);
                offset = segmentEnd + 1;
            } while (offset <= pathSegments.length());
            return this;
        }

        public Builder setPathSegment(int index, String pathSegment) {
            if (pathSegment == null) throw new NullPointerException("pathSegment == null");
            String canonicalPathSegment = canonicalize(
                    pathSegment, 0, pathSegment.length(), PATH_SEGMENT_ENCODE_SET, false, false, false, true,
                    null);
            if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
                throw new IllegalArgumentException("unexpected path segment: " + pathSegment);
            }
            encodedPathSegments.set(index, canonicalPathSegment);
            return this;
        }

        public Builder setEncodedPathSegment(int index, String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            String canonicalPathSegment = canonicalize(encodedPathSegment,
                    0, encodedPathSegment.length(), PATH_SEGMENT_ENCODE_SET, true, false, false, true,
                    null);
            encodedPathSegments.set(index, canonicalPathSegment);
            if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
                throw new IllegalArgumentException("unexpected path segment: " + encodedPathSegment);
            }
            return this;
        }

        public Builder removePathSegment(int index) {
            encodedPathSegments.remove(index);
            if (encodedPathSegments.isEmpty()) {
                encodedPathSegments.add(""); // Always leave at least one '/'.
            }
            return this;
        }

        public Builder encodedPath(String encodedPath) {
            if (encodedPath == null) throw new NullPointerException("encodedPath == null");
            if (!encodedPath.startsWith("/")) {
                throw new IllegalArgumentException("unexpected encodedPath: " + encodedPath);
            }
            resolvePath(encodedPath, 0, encodedPath.length());
            return this;
        }

        public Builder query(String query) {
            this.encodedQueryNamesAndValues = query != null
                    ? queryStringToNamesAndValues(canonicalize(
                    query, QUERY_ENCODE_SET, false, false, true, true))
                    : null;
            return this;
        }

        public Builder encodedQuery(String encodedQuery) {
            this.encodedQueryNamesAndValues = encodedQuery != null
                    ? queryStringToNamesAndValues(
                    canonicalize(encodedQuery, QUERY_ENCODE_SET, true, false, true, true))
                    : null;
            return this;
        }

        /**
         * Encodes the query parameter using UTF-8 and adds it to this URL's query string.
         */
        public Builder addQueryParameter(String name, String value) {
            if (name == null) throw new NullPointerException("name == null");
            if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList<>();
            encodedQueryNamesAndValues.add(
                    canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
            encodedQueryNamesAndValues.add(value != null
                    ? canonicalize(value, QUERY_COMPONENT_ENCODE_SET, false, false, true, true)
                    : null);
            return this;
        }

        /**
         * Adds the pre-encoded query parameter to this URL's query string.
         */
        public Builder addEncodedQueryParameter(String encodedName, String encodedValue) {
            if (encodedName == null) throw new NullPointerException("encodedName == null");
            if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList<>();
            encodedQueryNamesAndValues.add(
                    canonicalize(encodedName, QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
            encodedQueryNamesAndValues.add(encodedValue != null
                    ? canonicalize(encodedValue, QUERY_COMPONENT_REENCODE_SET, true, false, true, true)
                    : null);
            return this;
        }

        public Builder setQueryParameter(String name, String value) {
            removeAllQueryParameters(name);
            addQueryParameter(name, value);
            return this;
        }

        public Builder setEncodedQueryParameter(String encodedName, String encodedValue) {
            removeAllEncodedQueryParameters(encodedName);
            addEncodedQueryParameter(encodedName, encodedValue);
            return this;
        }

        public Builder removeAllQueryParameters(String name) {
            if (name == null) throw new NullPointerException("name == null");
            if (encodedQueryNamesAndValues == null) return this;
            String nameToRemove = canonicalize(
                    name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
            removeAllCanonicalQueryParameters(nameToRemove);
            return this;
        }

        public Builder removeAllEncodedQueryParameters(String encodedName) {
            if (encodedName == null) throw new NullPointerException("encodedName == null");
            if (encodedQueryNamesAndValues == null) return this;
            removeAllCanonicalQueryParameters(
                    canonicalize(encodedName, QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
            return this;
        }

        private void removeAllCanonicalQueryParameters(String canonicalName) {
            for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
                if (canonicalName.equals(encodedQueryNamesAndValues.get(i))) {
                    encodedQueryNamesAndValues.remove(i + 1);
                    encodedQueryNamesAndValues.remove(i);
                    if (encodedQueryNamesAndValues.isEmpty()) {
                        encodedQueryNamesAndValues = null;
                        return;
                    }
                }
            }
        }

        public Builder fragment(String fragment) {
            this.encodedFragment = fragment != null
                    ? canonicalize(fragment, FRAGMENT_ENCODE_SET, false, false, false, false)
                    : null;
            return this;
        }

        public Builder encodedFragment(String encodedFragment) {
            this.encodedFragment = encodedFragment != null
                    ? canonicalize(encodedFragment, FRAGMENT_ENCODE_SET, true, false, false, false)
                    : null;
            return this;
        }

        /**
         * Re-encodes the components of this URL so that it satisfies (obsolete) RFC 2396, which is
         * particularly strict for certain components.
         */
        Builder reencodeForUri() {
            for (int i = 0, size = encodedPathSegments.size(); i < size; i++) {
                String pathSegment = encodedPathSegments.get(i);
                encodedPathSegments.set(i,
                        canonicalize(pathSegment, PATH_SEGMENT_ENCODE_SET_URI, true, true, false, true));
            }
            if (encodedQueryNamesAndValues != null) {
                for (int i = 0, size = encodedQueryNamesAndValues.size(); i < size; i++) {
                    String component = encodedQueryNamesAndValues.get(i);
                    if (component != null) {
                        encodedQueryNamesAndValues.set(i,
                                canonicalize(component, QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, true));
                    }
                }
            }
            if (encodedFragment != null) {
                encodedFragment = canonicalize(
                        encodedFragment, FRAGMENT_ENCODE_SET_URI, true, true, false, false);
            }
            return this;
        }

        public HttpUrl build() {
            if (scheme == null) throw new IllegalStateException("scheme == null");
            if (host == null) throw new IllegalStateException("host == null");
            return new HttpUrl(this);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            if (scheme != null) {
                result.append(scheme);
                result.append("://");
            } else {
                result.append("//");
            }

            if (!encodedUsername.isEmpty() || !encodedPassword.isEmpty()) {
                result.append(encodedUsername);
                if (!encodedPassword.isEmpty()) {
                    result.append(':');
                    result.append(encodedPassword);
                }
                result.append('@');
            }

            if (host != null) {
                if (host.indexOf(':') != -1) {
                    // Host is an IPv6 address.
                    result.append('[');
                    result.append(host);
                    result.append(']');
                } else {
                    result.append(host);
                }
            }

            if (port != -1 || scheme != null) {
                int effectivePort = effectivePort();
                if (scheme == null || effectivePort != defaultPort(scheme)) {
                    result.append(':');
                    result.append(effectivePort);
                }
            }

            pathSegmentsToString(result, encodedPathSegments);

            if (encodedQueryNamesAndValues != null) {
                result.append('?');
                namesAndValuesToQueryString(result, encodedQueryNamesAndValues);
            }

            if (encodedFragment != null) {
                result.append('#');
                result.append(encodedFragment);
            }

            return result.toString();
        }

        static final String INVALID_HOST = "Invalid URL host";


        Builder parse(HttpUrl base, String input) {
            int pos = skipLeadingAsciiWhitespace(input, 0, input.length());
            int limit = skipTrailingAsciiWhitespace(input, pos, input.length());

            // Scheme.
            int schemeDelimiterOffset = schemeDelimiterOffset(input, pos, limit);
            if (schemeDelimiterOffset != -1) {
                if (input.regionMatches(true, pos, "https:", 0, 6)) {
                    this.scheme = "https";
                    pos += "https:".length();
                } else if (input.regionMatches(true, pos, "http:", 0, 5)) {
                    this.scheme = "http";
                    pos += "http:".length();
                } else {
                    throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but was '"
                            + input.substring(0, schemeDelimiterOffset) + "'");
                }
            } else if (base != null) {
                this.scheme = base.scheme;
            } else {
                throw new IllegalArgumentException(
                        "Expected URL scheme 'http' or 'https' but no colon was found");
            }

            // Authority.
            boolean hasUsername = false;
            boolean hasPassword = false;
            int slashCount = slashCount(input, pos, limit);
            if (slashCount >= 2 || base == null || !base.scheme.equals(this.scheme)) {
                // Read an authority if either:
                //  * The input starts with 2 or more slashes. These follow the scheme if it exists.
                //  * The input scheme exists and is different from the base URL's scheme.
                //
                // The structure of an authority is:
                //   username:password@host:port
                //
                // Username, password and port are optional.
                //   [username[:password]@]host[:port]
                pos += slashCount;
                authority:
                while (true) {
                    int componentDelimiterOffset = delimiterOffset(input, pos, limit, "@/\\?#");
                    int c = componentDelimiterOffset != limit
                            ? input.charAt(componentDelimiterOffset)
                            : -1;
                    switch (c) {
                        case '@':
                            // User info precedes.
                            if (!hasPassword) {
                                int passwordColonOffset = delimiterOffset(
                                        input, pos, componentDelimiterOffset, ':');
                                String canonicalUsername = canonicalize(
                                        input, pos, passwordColonOffset, USERNAME_ENCODE_SET, true, false, false, true,
                                        null);
                                this.encodedUsername = hasUsername
                                        ? this.encodedUsername + "%40" + canonicalUsername
                                        : canonicalUsername;
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = canonicalize(input, passwordColonOffset + 1,
                                            componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true,
                                            null);
                                }
                                hasUsername = true;
                            } else {
                                this.encodedPassword = this.encodedPassword + "%40" + canonicalize(input, pos,
                                        componentDelimiterOffset, PASSWORD_ENCODE_SET, true, false, false, true,
                                        null);
                            }
                            pos = componentDelimiterOffset + 1;
                            break;

                        case -1:
                        case '/':
                        case '\\':
                        case '?':
                        case '#':
                            // Host info precedes.
                            int portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                            if (portColonOffset + 1 < componentDelimiterOffset) {
                                host = canonicalizeHost(input, pos, portColonOffset);
                                port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                if (port == -1) {
                                    throw new IllegalArgumentException("Invalid URL port: \""
                                            + input.substring(portColonOffset + 1, componentDelimiterOffset) + '"');
                                }
                            } else {
                                host = canonicalizeHost(input, pos, portColonOffset);
                                port = defaultPort(scheme);
                            }
                            if (host == null) {
                                throw new IllegalArgumentException(
                                        INVALID_HOST + ": \"" + input.substring(pos, portColonOffset) + '"');
                            }
                            pos = componentDelimiterOffset;
                            break authority;
                    }
                }
            } else {
                // This is a relative link. Copy over all authority components. Also maybe the path & query.
                this.encodedUsername = base.encodedUsername();
                this.encodedPassword = base.encodedPassword();
                this.host = base.host;
                this.port = base.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(base.encodedPathSegments());
                if (pos == limit || input.charAt(pos) == '#') {
                    encodedQuery(base.encodedQuery());
                }
            }

            // Resolve the relative path.
            int pathDelimiterOffset = delimiterOffset(input, pos, limit, "?#");
            resolvePath(input, pos, pathDelimiterOffset);
            pos = pathDelimiterOffset;

            // Query.
            if (pos < limit && input.charAt(pos) == '?') {
                int queryDelimiterOffset = delimiterOffset(input, pos, limit, '#');
                this.encodedQueryNamesAndValues = queryStringToNamesAndValues(canonicalize(
                        input, pos + 1, queryDelimiterOffset, QUERY_ENCODE_SET, true, false, true, true, null));
                pos = queryDelimiterOffset;
            }

            // Fragment.
            if (pos < limit && input.charAt(pos) == '#') {
                this.encodedFragment = canonicalize(
                        input, pos + 1, limit, FRAGMENT_ENCODE_SET, true, false, false, false, null);
            }

            return this;
        }


        private void resolvePath(String input, int pos, int limit) {
            // Read a delimiter.
            if (pos == limit) {
                // Empty path: keep the base path as-is.
                return;
            }
            char c = input.charAt(pos);
            if (c == '/' || c == '\\') {
                // Absolute path: reset to the default "/".
                encodedPathSegments.clear();
                encodedPathSegments.add("");
                pos++;
            } else {
                // Relative path: clear everything after the last '/'.
                encodedPathSegments.set(encodedPathSegments.size() - 1, "");
            }

            // Read path segments.
            for (int i = pos; i < limit; ) {
                int pathSegmentDelimiterOffset = delimiterOffset(input, i, limit, "/\\");
                boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
                push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                i = pathSegmentDelimiterOffset;
                if (segmentHasTrailingSlash) i++;
            }
        }

        /**
         * Adds a path segment. If the input is ".." or equivalent, this pops a path segment.
         */
        private void push(String input, int pos, int limit, boolean addTrailingSlash,
                          boolean alreadyEncoded) {
            String segment = canonicalize(
                    input, pos, limit, PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, true, null);
            if (isDot(segment)) {
                return; // Skip '.' path segments.
            }
            if (isDotDot(segment)) {
                pop();
                return;
            }
            if (encodedPathSegments.get(encodedPathSegments.size() - 1).isEmpty()) {
                encodedPathSegments.set(encodedPathSegments.size() - 1, segment);
            } else {
                encodedPathSegments.add(segment);
            }
            if (addTrailingSlash) {
                encodedPathSegments.add("");
            }
        }

        private boolean isDot(String input) {
            return input.equals(".") || input.equalsIgnoreCase("%2e");
        }

        private boolean isDotDot(String input) {
            return input.equals("..")
                    || input.equalsIgnoreCase("%2e.")
                    || input.equalsIgnoreCase(".%2e")
                    || input.equalsIgnoreCase("%2e%2e");
        }

        /**
         * Removes a path segment. When this method returns the last segment is always "", which means
         * the encoded path will have a trailing '/'.
         *
         * <p>Popping "/a/b/c/" yields "/a/b/". In this case the list of path segments goes from ["a",
         * "b", "c", ""] to ["a", "b", ""].
         *
         * <p>Popping "/a/b/c" also yields "/a/b/". The list of path segments goes from ["a", "b", "c"]
         * to ["a", "b", ""].
         */
        private void pop() {
            String removed = encodedPathSegments.remove(encodedPathSegments.size() - 1);

            // Make sure the path ends with a '/' by either adding an empty string or clearing a segment.
            if (removed.isEmpty() && !encodedPathSegments.isEmpty()) {
                encodedPathSegments.set(encodedPathSegments.size() - 1, "");
            } else {
                encodedPathSegments.add("");
            }
        }

        /**
         * Returns the index of the ':' in {@code input} that is after scheme characters. Returns -1 if
         * {@code input} does not have a scheme that starts at {@code pos}.
         */
        private static int schemeDelimiterOffset(String input, int pos, int limit) {
            if (limit - pos < 2) return -1;

            char c0 = input.charAt(pos);
            if ((c0 < 'a' || c0 > 'z') && (c0 < 'A' || c0 > 'Z')) return -1; // Not a scheme start char.

            for (int i = pos + 1; i < limit; i++) {
                char c = input.charAt(i);

                if ((c >= 'a' && c <= 'z')
                        || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9')
                        || c == '+'
                        || c == '-'
                        || c == '.') {
                    continue; // Scheme character. Keep going.
                } else if (c == ':') {
                    return i; // Scheme prefix!
                } else {
                    return -1; // Non-scheme character before the first ':'.
                }
            }

            return -1; // No ':'; doesn't start with a scheme.
        }

        /**
         * Returns the number of '/' and '\' slashes in {@code input}, starting at {@code pos}.
         */
        private static int slashCount(String input, int pos, int limit) {
            int slashCount = 0;
            while (pos < limit) {
                char c = input.charAt(pos);
                if (c == '\\' || c == '/') {
                    slashCount++;
                    pos++;
                } else {
                    break;
                }
            }
            return slashCount;
        }

        /**
         * Finds the first ':' in {@code input}, skipping characters between square braces "[...]".
         */
        private static int portColonOffset(String input, int pos, int limit) {
            for (int i = pos; i < limit; i++) {
                switch (input.charAt(i)) {
                    case '[':
                        while (++i < limit) {
                            if (input.charAt(i) == ']') break;
                        }
                        break;
                    case ':':
                        return i;
                }
            }
            return limit; // No colon.
        }

        private static String canonicalizeHost(String input, int pos, int limit) {
            // Start by percent decoding the host. The WHATWG spec suggests doing this only after we've
            // checked for IPv6 square braces. But Chrome does it first, and that's more lenient.
            String percentDecoded = percentDecode(input, pos, limit, false);
            return canonicalizeHost(percentDecoded);
        }

        public static String canonicalizeHost(String host) {
            // If the input contains a :, it’s an IPv6 address.
            if (host.contains(":")) {
                // If the input is encased in square braces "[...]", drop 'em.
                InetAddress inetAddress = host.startsWith("[") && host.endsWith("]")
                        ? decodeIpv6(host, 1, host.length() - 1)
                        : decodeIpv6(host, 0, host.length());
                if (inetAddress == null) return null;
                byte[] address = inetAddress.getAddress();
                if (address.length == 16) return inet6AddressToAscii(address);
                throw new AssertionError("Invalid IPv6 address: '" + host + "'");
            }

            try {
                String result = IDN.toASCII(host).toLowerCase(Locale.US);
                if (result.isEmpty()) return null;

                // Confirm that the IDN ToASCII result doesn't contain any illegal characters.
                if (containsInvalidHostnameAsciiCodes(result)) {
                    return null;
                }
                // TODO: implement all label limits.
                return result;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private static int parsePort(String input, int pos, int limit) {
            try {
                // Canonicalize the port string to skip '\n' etc.
                String portString = canonicalize(input, pos, limit, "", false, false, false, true, null);
                int i = Integer.parseInt(portString);
                if (i > 0 && i <= 65535) return i;
                return -1;
            } catch (NumberFormatException e) {
                return -1; // Invalid port.
            }
        }
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        int size = list.size();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String s = list.get(i);
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                // Slow path: the character at i requires decoding!
                //Buffer out = new Buffer();
                //out.writeUtf8(encoded, pos, i);
//                percentDecode(out, encoded, i, limit, plusIsSpace);
                //return out.readUtf8();
                return "";
            }
        }

        // Fast path: no characters in [pos..limit) required decoding.
        return encoded.substring(pos, limit);
    }

//    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
//        int codePoint;
//        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
//            codePoint = encoded.codePointAt(i);
//            if (codePoint == '%' && i + 2 < limit) {
//                int d1 = decodeHexDigit(encoded.charAt(i + 1));
//                int d2 = decodeHexDigit(encoded.charAt(i + 2));
//                if (d1 != -1 && d2 != -1) {
//                    out.writeByte((d1 << 4) + d2);
//                    i += 2;
//                    continue;
//                }
//            } else if (codePoint == '+' && plusIsSpace) {
//                out.writeByte(' ');
//                continue;
//            }
//            out.writeUtf8CodePoint(codePoint);
//        }
//    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        return pos + 2 < limit
                && encoded.charAt(pos) == '%'
                && decodeHexDigit(encoded.charAt(pos + 1)) != -1
                && decodeHexDigit(encoded.charAt(pos + 2)) != -1;
    }

    static String canonicalize(String input, int pos, int limit, String encodeSet,
                               boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly,
                               Charset charset) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                    || codePoint == '+' && plusIsSpace) {
                // Slow path: the character at i requires encoding!
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
                        asciiOnly, charset);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required encoding.
        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
                             boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly,
                             Charset charset) {
        Buffer encodedCharBuffer = null; // Lazily allocated.
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded
                    && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                // Skip this character.
            } else if (codePoint == '+' && plusIsSpace) {
                // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                // Percent encode this character.
                if (encodedCharBuffer == null) {
                    encodedCharBuffer = new Buffer();
                }

                if (charset == null || charset.equals(UTF_8)) {
                    encodedCharBuffer.writeUtf8CodePoint(codePoint);
                } else {
                    encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
                }

                while (!encodedCharBuffer.exhausted()) {
                    int b = encodedCharBuffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        return canonicalize(
                input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly,
                charset);
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(
                input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
    }

    public static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    /**
     * Returns the index of the first character in {@code input} that contains a character in {@code
     * delimiters}. Returns limit if there is no such character.
     */
    public static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) return i;
        }
        return limit;
    }

    /**
     * Returns the index of the first character in {@code input} that is {@code delimiter}. Returns
     * limit if there is no such character.
     */
    public static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) return i;
        }
        return limit;
    }

    /**
     * Increments {@code pos} until {@code input[pos]} is not ASCII whitespace. Stops at {@code
     * limit}.
     */
    public static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = pos; i < limit; i++) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i;
            }
        }
        return limit;
    }

    /**
     * Decrements {@code limit} until {@code input[limit - 1]} is not ASCII whitespace. Stops at
     * {@code pos}.
     */
    public static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        for (int i = limit - 1; i >= pos; i--) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    continue;
                default:
                    return i + 1;
            }
        }
        return pos;
    }

    /**
     * Returns true if {@code host} is not a host name and might be an IP address.
     */
    public static boolean verifyAsIpAddress(String host) {
        final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile(
                "([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
        return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
    }


    public static String canonicalizeHost(String host) {
        // If the input contains a :, it’s an IPv6 address.
        if (host.contains(":")) {
            // If the input is encased in square braces "[...]", drop 'em.
            InetAddress inetAddress = host.startsWith("[") && host.endsWith("]")
                    ? decodeIpv6(host, 1, host.length() - 1)
                    : decodeIpv6(host, 0, host.length());
            if (inetAddress == null) return null;
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) return inet6AddressToAscii(address);
            throw new AssertionError("Invalid IPv6 address: '" + host + "'");
        }

        try {
            String result = IDN.toASCII(host).toLowerCase(Locale.US);
            if (result.isEmpty()) return null;

            // Confirm that the IDN ToASCII result doesn't contain any illegal characters.
            if (containsInvalidHostnameAsciiCodes(result)) {
                return null;
            }
            // TODO: implement all label limits.
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Decodes an IPv6 address like 1111:2222:3333:4444:5555:6666:7777:8888 or ::1.
     */
    private static InetAddress decodeIpv6(String input, int pos, int limit) {
        byte[] address = new byte[16];
        int b = 0;
        int compress = -1;
        int groupOffset = -1;

        for (int i = pos; i < limit; ) {
            if (b == address.length) return null; // Too many groups.

            // Read a delimiter.
            if (i + 2 <= limit && input.regionMatches(i, "::", 0, 2)) {
                // Compression "::" delimiter, which is anywhere in the input, including its prefix.
                if (compress != -1) return null; // Multiple "::" delimiters.
                i += 2;
                b += 2;
                compress = b;
                if (i == limit) break;
            } else if (b != 0) {
                // Group separator ":" delimiter.
                if (input.regionMatches(i, ":", 0, 1)) {
                    i++;
                } else if (input.regionMatches(i, ".", 0, 1)) {
                    // If we see a '.', rewind to the beginning of the previous group and parse as IPv4.
                    if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) return null;
                    b += 2; // We rewound two bytes and then added four.
                    break;
                } else {
                    return null; // Wrong delimiter.
                }
            }

            // Read a group, one to four hex digits.
            int value = 0;
            groupOffset = i;
            for (; i < limit; i++) {
                char c = input.charAt(i);
                int hexDigit = decodeHexDigit(c);
                if (hexDigit == -1) break;
                value = (value << 4) + hexDigit;
            }
            int groupLength = i - groupOffset;
            if (groupLength == 0 || groupLength > 4) return null; // Group is the wrong size.

            // We've successfully read a group. Assign its value to our byte array.
            address[b++] = (byte) ((value >>> 8) & 0xff);
            address[b++] = (byte) (value & 0xff);
        }

        // All done. If compression happened, we need to move bytes to the right place in the
        // address. Here's a sample:
        //
        //      input: "1111:2222:3333::7777:8888"
        //     before: { 11, 11, 22, 22, 33, 33, 00, 00, 77, 77, 88, 88, 00, 00, 00, 00  }
        //   compress: 6
        //          b: 10
        //      after: { 11, 11, 22, 22, 33, 33, 00, 00, 00, 00, 00, 00, 77, 77, 88, 88 }
        //
        if (b != address.length) {
            if (compress == -1) return null; // Address didn't have compression or enough groups.
            System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
            Arrays.fill(address, compress, compress + (address.length - b), (byte) 0);
        }

        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * Decodes an IPv4 address suffix of an IPv6 address, like 1111::5555:6666:192.168.0.1.
     */
    private static boolean decodeIpv4Suffix(
            String input, int pos, int limit, byte[] address, int addressOffset) {
        int b = addressOffset;

        for (int i = pos; i < limit; ) {
            if (b == address.length) return false; // Too many groups.

            // Read a delimiter.
            if (b != addressOffset) {
                if (input.charAt(i) != '.') return false; // Wrong delimiter.
                i++;
            }

            // Read 1 or more decimal digits for a value in 0..255.
            int value = 0;
            int groupOffset = i;
            for (; i < limit; i++) {
                char c = input.charAt(i);
                if (c < '0' || c > '9') break;
                if (value == 0 && groupOffset != i) return false; // Reject unnecessary leading '0's.
                value = (value * 10) + c - '0';
                if (value > 255) return false; // Value out of range.
            }
            int groupLength = i - groupOffset;
            if (groupLength == 0) return false; // No digits.

            // We've successfully read a byte.
            address[b++] = (byte) value;
        }

        if (b != addressOffset + 4) return false; // Too few groups. We wanted exactly four.
        return true; // Success.
    }

    /**
     * Encodes an IPv6 address in canonical form according to RFC 5952.
     */
    private static String inet6AddressToAscii(byte[] address) {
        // Go through the address looking for the longest run of 0s. Each group is 2-bytes.
        // A run must be longer than one group (section 4.2.2).
        // If there are multiple equal runs, the first one must be used (section 4.2.3).
        int longestRunOffset = -1;
        int longestRunLength = 0;
        for (int i = 0; i < address.length; i += 2) {
            int currentRunOffset = i;
            while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                i += 2;
            }
            int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
        }

        // Emit each 2-byte group in hex, separated by ':'. The longest run of zeroes is "::".
//        Buffer result = new Buffer();
//        for (int i = 0; i < address.length; ) {
//            if (i == longestRunOffset) {
//                result.writeByte(':');
//                i += longestRunLength;
//                if (i == 16) result.writeByte(':');
//            } else {
//                if (i > 0) result.writeByte(':');
//                int group = (address[i] & 0xff) << 8 | address[i + 1] & 0xff;
//                result.writeHexadecimalUnsignedLong(group);
//                i += 2;
//            }
//        }
//        return result.readUtf8();

        return "";
    }

    private static boolean containsInvalidHostnameAsciiCodes(String hostnameAscii) {
        for (int i = 0; i < hostnameAscii.length(); i++) {
            char c = hostnameAscii.charAt(i);
            // The WHATWG Host parsing rules accepts some character codes which are invalid by
            // definition for OkHttp's host header checks (and the WHATWG Host syntax definition). Here
            // we rule out characters that would cause problems in host headers.
            if (c <= '\u001f' || c >= '\u007f') {
                return true;
            }
            // Check for the characters mentioned in the WHATWG Host parsing spec:
            // U+0000, U+0009, U+000A, U+000D, U+0020, "#", "%", "/", ":", "?", "@", "[", "\", and "]"
            // (excluding the characters covered above).
            if (" #%/:?@[\\]".indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
}

