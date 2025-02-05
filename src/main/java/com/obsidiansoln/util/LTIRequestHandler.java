package com.obsidiansoln.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LTIRequestHandler {
	
	private static Logger mLog = LoggerFactory.getLogger(LTIRequestHandler.class);

    private final String consumerKey;
    private final String consumerSecret;

    public LTIRequestHandler(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public boolean validate(HttpServletRequest request) {
    	mLog.trace("In validate ...");
 
        try {
            // Extract all parameters from the request
            Map<String, String> parameters = extractParameters(request);

            // Get the provided OAuth signature
            String providedSignature = parameters.get("oauth_signature");
            mLog.trace("Provided Signature: " + providedSignature);
            if (providedSignature == null) {
                System.err.println("Missing OAuth signature.");
                return false;
            }
            

            // Remove the OAuth signature from the parameters
            parameters.remove("oauth_signature");

            // Construct the base string
            String baseString = constructBaseString(request, parameters);
            mLog.trace ("Base URL: " + baseString);

            // Generate the signature
            String generatedSignature = generateSignature(baseString);
            mLog.trace("Generated Signature: " + generatedSignature);
            
            // Compare the provided and generated signatures
            return providedSignature.equals(generatedSignature);
        } catch (Exception e) {
            System.err.println("Error validating LTI request: " + e.getMessage());
            return false;
        }
    }

    private Map<String, String> extractParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            parameters.put(name, request.getParameter(name));
        }
        return parameters;
    }

    private String constructBaseString(HttpServletRequest request, Map<String, String> parameters) throws Exception {
        // Construct the base URL
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(request.getScheme()).append("://");
        baseUrl.append(request.getServerName());
        if ((request.getScheme().equals("http") && request.getServerPort() != 80)
                || (request.getScheme().equals("https") && request.getServerPort() != 443)) {
            baseUrl.append(":").append(request.getServerPort());
        }
        baseUrl.append(request.getRequestURI());

        // Sort and percent-encode the parameters
        List<String> sortedKeys = new ArrayList<>(parameters.keySet());
        Collections.sort(sortedKeys);

        StringBuilder parameterString = new StringBuilder();
        for (String key : sortedKeys) {
            if (parameterString.length() > 0) {
                parameterString.append("&");
            }
            parameterString.append(percentEncode(key))
                    .append("=")
                    .append(percentEncode(parameters.get(key)));
        }

        // Construct the base string
        return "POST" + "&" +
                percentEncode(baseUrl.toString()) + "&" +
                percentEncode(parameterString.toString());
    }

    private String generateSignature(String baseString) throws Exception {
        // Create the signing key
        String signingKey = percentEncode(consumerSecret) + "&";

        // Sign the base string using HMAC-SHA1
        SecretKeySpec keySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] rawSignature = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));

        // Base64-encode the signature
        return Base64.getEncoder().encodeToString(rawSignature);
    }
    
    private String percentEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new RuntimeException("Error encoding value: " + value, e);
        }
    }
}