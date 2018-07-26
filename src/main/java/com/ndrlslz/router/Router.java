package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;
import io.netty.handler.codec.http.HttpMethod;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Router {
    private String path;
    private HttpMethod httpMethod;
    private Handler<RouterContext> handler;
    private Pattern regexPattern;
    private Set<String> groups;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        setRegexPattern(path);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Handler<RouterContext> getHandler() {
        return handler;
    }

    public void setHandler(Handler<RouterContext> handler) {
        this.handler = handler;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    private void setRegexPattern(String path) {
        path = format("^%s$", path);
        Pattern p = Pattern.compile("\\{([A-Za-z][A-Za-z0-9_]*)}");
        Matcher m = p.matcher(path);

        StringBuffer sb = new StringBuffer();

        groups = new HashSet<>();
        while (m.find()) {
            m.appendReplacement(sb, "(?<$1>[^/]+)");
            String group = m.group();

            groups.add(group.substring(1, group.length() - 1));
        }

        m.appendTail(sb);

        this.regexPattern = Pattern.compile(sb.toString());
    }
}
