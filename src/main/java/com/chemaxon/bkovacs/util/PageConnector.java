package com.chemaxon.bkovacs.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * The code expects a session_id.txt file in the current working directory.
 * The first line of the file should contain your session ID. A simple guide on how to acquire the session ID:
 * <a href="https://github.com/wimglenn/advent-of-code-wim/issues/1">LINK</a>
 * </p>
 * <p>Credit to @gkovacs20 @plajko</p>
 *
 */
public class PageConnector {

    private static final String URL_PATTERN = "https://adventofcode.com/%d/day/%d/%s";
    private static final String INPUT = "input";
    private static final String ANSWER = "answer";

    private static final Logger LOG = LogManager.getLogger(PageConnector.class);


    public static List<String> downloadInputFile(int year, int day) throws Exception {
        try (var in = new BufferedReader(new InputStreamReader(open(year, day, HttpResponse.BodyHandlers.ofInputStream())))) {
            return in.lines().collect(Collectors.toList());
        }
    }

    public static String downloadInputLine(int year, int day) throws Exception {
        return open(year, day, HttpResponse.BodyHandlers.ofString());
    }

    public static void submitSolution(int year, int day, int level, Object answer) throws Exception {
        var uri = URI.create(URL_PATTERN.formatted(year, day, ANSWER));

        var client = HttpClient.newBuilder().build();
        var body = getFormDataAsString(Map.of(
                "level", Integer.toString(level),
                "answer", String.valueOf(answer)));
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", String.format("session=%s", sessionId()))
                .uri(uri).build();
        var resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        var page = Jsoup.parse(resp.body());
        LOG.info("{}", page.select("body > main > article").text());
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        return formData.entrySet().stream()
                .map(e -> String.format("%s=%s",
                        URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8),
                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));
    }

    private static <T> T open(int year, int day, HttpResponse.BodyHandler<T> bodyHandler) throws Exception {
        var uri = URI.create(URL_PATTERN.formatted(year, day, INPUT));

        var cookieHandler = new CookieHandlerImpl();
        cookieHandler.cookies.put(uri.getHost(), List.of("session=" + sessionId()));

        var client = HttpClient.newBuilder()
                .cookieHandler(cookieHandler)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        var response = client.send(request, bodyHandler);

        LOG.info("Input downloaded with {}", response.statusCode());

        return response.body();
    }

    private static String sessionId() {
        var path = Path.of("session_id.txt");
        try {
            var lines = Files.readAllLines(path);
            try {
                return lines.get(0);
            } catch (IndexOutOfBoundsException e) {
                throw new RuntimeException("Session ID file exists but it is empty at: " + path.toAbsolutePath(), e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Missing session ID file at: " + path.toAbsolutePath(), e);
        }
    }


    public static class CookieHandlerImpl extends CookieHandler {
        private final Map<String, List<String>> cookies = new HashMap<>();

        @Override
        public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) {
            Map<String, List<String>> ret = new HashMap<>();
            synchronized (cookies) {
                List<String> store = cookies.get(uri.getHost());
                if (store != null) {
                    store = List.copyOf(store);
                    ret.put("Cookie", store);
                }
            }
            return Collections.unmodifiableMap(ret);
        }

        @Override
        public void put(URI uri, Map<String, List<String>> responseHeaders) {
            List<String> newCookies = responseHeaders.get("Set-Cookie");
            if (newCookies != null) {
                synchronized (cookies) {
                    cookies.computeIfAbsent(uri.getHost(), k -> new ArrayList<>()).addAll(newCookies);
                }
            }
        }
    }
}


