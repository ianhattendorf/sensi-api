package com.ianhattendorf.sensi.sensiapi;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ignore URL
public final class PersistentCookieJar implements CookieJar {
    private final Set<String> cookieSet = new HashSet<>();
    private final List<Cookie> cookies = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        for (Cookie cookie : list) {
            if (cookieSet.add(cookie.name())) {
                cookies.add(cookie);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return cookies;
    }
}
