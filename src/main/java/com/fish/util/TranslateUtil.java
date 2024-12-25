package com.fish.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateUtil {

    public static String translate(String text) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        String bingUrl = "https://cn.bing.com/translator?ref=TThis&text=&from=zh-Hans&to=en";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(bingUrl)).GET().build();
        HttpResponse<String> bingHtml = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = bingHtml.body();

        String IG = "";
        String pattern = ",IG:\"([^\"]+)\"";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(responseBody);
        if (m.find()) {
            IG = m.group(1);
        }

        String key = "";
        String token = "";
        String pattern1 = "var params_AbusePreventionHelper = \\[(.*?)\\]";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(responseBody);
        if (m1.find()) {
            String group1 = m1.group(1);
            String[] split = group1.split(",");
            key = split[0];
            token = split[1].replace("\"", "");
        }

        String urlPattern = "https://cn.bing.com/ttranslatev3?isVertical=1&&IG={0}&IID=translator.5026";
        URI uri = URI.create(MessageFormat.format(urlPattern, IG));
        String fromLang = "en";
        String to = "zh-Hans";
        String bodyPattern = "&fromLang={0}&to={1}&token={2}&key={3}&text={4}&tryFetchingGenderDebiasedTranslations=true";
        String body = MessageFormat.format(bodyPattern, fromLang, to, token, key, text);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, Charset.defaultCharset())).build();
        HttpResponse<String> translateResponse = client.send(request2, HttpResponse.BodyHandlers.ofString());
        String translateResponseBody = translateResponse.body();
        List list = new ObjectMapper().readValue(translateResponseBody, List.class);
        Map map = (Map) list.get(0);
        List translations = (List) map.get("translations");
        Map map2 = (Map) translations.get(0);
        return (String) map2.get("text");
    }
}
