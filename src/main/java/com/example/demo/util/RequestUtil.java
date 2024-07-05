package com.example.demo.util;

import jakarta.servlet.http.HttpServletRequest;
/**
 * Utility class helping to extract user ip address.
 */
public class RequestUtil {
	
	public static String getRemoteAddress(HttpServletRequest request) {
		// if the service resides behind an application load balancer
		String address = request.getHeader("X-Forwarded-For");
		
		if (address == null) {
			address = request.getRemoteAddr();
		}
		return address;
	}

}
