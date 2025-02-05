package com.obsidiansoln.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class OAuth1Verifier {

	    public static boolean verifyOAuth1Token(
	            HttpServletRequest request,
	            String consumerSecret,
	            String tokenSecret,
	            String providedSignature) {
	        try {
	            // Extract HTTP method and URL
	            String httpMethod = request.getMethod();
	            String baseUrl = getBaseUrl(request);

	            // Extract and sort parameters from the request
	            Map<String, String> params = getParameters(request);

	            // Create the signature base string
	            String baseString = createBaseString(httpMethod, baseUrl, params);

	            // Create the signing key
	            String signingKey = URLEncoder.encode(consumerSecret, StandardCharsets.UTF_8.name()) + "&" +
	                    (tokenSecret != null ? URLEncoder.encode(tokenSecret, StandardCharsets.UTF_8.name()) : "");

	            // Compute the HMAC-SHA1 signature
	            String computedSignature = computeHmacSha1(baseString, signingKey);

	            // Compare computed signature with the provided one
	            return computedSignature.equals(providedSignature);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    private static String getBaseUrl(HttpServletRequest request) {
	        String scheme = request.getScheme();
	        String serverName = request.getServerName();
	        int serverPort = request.getServerPort();
	        String contextPath = request.getRequestURI();

	        // Determine if the port should be included in the URL
	        boolean isDefaultPort = (scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443);
	        return scheme + "://" + serverName + (isDefaultPort ? "" : ":" + serverPort) + contextPath;
	    }

	    private static Map<String, String> getParameters(HttpServletRequest request) {
	        Map<String, String> params = new TreeMap<>();
	        Enumeration<String> parameterNames = request.getParameterNames();

	        while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();
	            String paramValue = request.getParameter(paramName);
	            params.put(paramName, paramValue);
	        }

	        return params;
	    }

	    private static String createBaseString(String httpMethod, String baseUrl, Map<String, String> params) throws Exception {
	        // Encode each key-value pair and join with '&'
	        StringBuilder paramString = new StringBuilder();
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	            if (paramString.length() > 0) {
	                paramString.append("&");
	            }
	            paramString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
	                       .append("=")
	                       .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
	        }

	        // Build the base string
	        return httpMethod.toUpperCase() + "&" +
	                URLEncoder.encode(baseUrl, StandardCharsets.UTF_8.name()) + "&" +
	                URLEncoder.encode(paramString.toString(), StandardCharsets.UTF_8.name());
	    }

	    private static String computeHmacSha1(String data, String key) throws Exception {
	        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
	        Mac mac = Mac.getInstance("HmacSHA1");
	        mac.init(signingKey);
	        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	        return Base64.getEncoder().encodeToString(rawHmac);
	    }

	}
