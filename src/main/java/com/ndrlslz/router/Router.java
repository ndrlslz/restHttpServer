package com.ndrlslz.router;

import com.ndrlslz.handler.Handler;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Router {
    private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{([A-Za-z][A-Za-z0-9_]*)}");
    private String path;
    private HttpMethod httpMethod;
    private Handler<RouterContext> handler;
    private Pattern regexPattern;
    private List<String> groups;

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

    public List<String> getGroups() {
        return groups;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    private void setRegexPattern(String path) {
        Matcher m = PATH_PARAM_PATTERN.matcher(format("^%s$", path));

        StringBuffer sb = new StringBuffer();

        groups = new ArrayList<>();
        int index = 0;
        while (m.find()) {
            m.appendReplacement(sb, format("(?<param%s>[^/]+)", index));
            String group = m.group();

            groups.add(group.substring(1, group.length() - 1));
            index++;
        }

        m.appendTail(sb);

        this.regexPattern = Pattern.compile(sb.toString());
    }
}
