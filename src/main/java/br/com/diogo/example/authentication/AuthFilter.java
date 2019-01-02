package br.com.diogo.example.authentication;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.xml.bind.DatatypeConverter;

public class AuthFilter implements ContainerRequestFilter {
	
	private static final String ACCESS_UNAUTHORIZED = "You do not have permission to access this resource";
	
	

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub

	}
	
	private String[] decode(String auth) {
		
		auth = auth.replaceFirst("[B|b]asic ", "");
		
		//extracts the string after the value "Basic", decoding on the Base64 format returning a byte array
		byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);
		
		if(decodedBytes == null || decodedBytes.length == 0) {
			return null;
		}
		
		//if the decoded String is not null|0 an array containing two string is returned: "user:password"
		return new String(decodedBytes).split(":", 2);
	}

}
