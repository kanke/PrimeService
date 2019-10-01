package com.cloudnc.interview;

import static java.net.http.HttpClient.newHttpClient;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class PrimeServiceTest {
    private final PrimeService primeService = PrimeService.createEphemeralServer();

    @Before
    public void setUp() throws Exception {
        primeService.start();
    }

    @After
    public void tearDown() throws Exception {
        primeService.stop();
    }

    @Test
    public void testGet2xx() throws IOException, InterruptedException {
        final HttpClient httpClient = newHttpClient();
        final URI uri = URI.create("http://localhost:" + primeService.port() + "/primalities/3");
        final HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                                                   .build();
        final HttpResponse<Void> response = httpClient.send(httpRequest, BodyHandlers.discarding());
        assertThat(response, is(successfulResponse()));
    }

    private static SuccessfulMatcher successfulResponse() {
        return new SuccessfulMatcher();
    }

    private static class SuccessfulMatcher extends CustomTypeSafeMatcher<HttpResponse> {
        SuccessfulMatcher() {
            super("Is 2xx response");
        }

        @Override
        protected boolean matchesSafely(final HttpResponse response) {
            return response.statusCode() >= 200 && response.statusCode() < 300;
        }

        @Override
        protected void describeMismatchSafely(final HttpResponse response, final Description mismatchDescription) {
            mismatchDescription.appendText("Response status was ")
                               .appendValue(response.statusCode());
        }
    }
}