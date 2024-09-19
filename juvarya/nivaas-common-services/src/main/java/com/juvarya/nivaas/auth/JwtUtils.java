package com.juvarya.nivaas.auth;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.enums.ERole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
    private static final String ROLES = "roles";

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${nivaas.secret.key}")
    private String jwtSecret;
	
    private final long jwtExpirationMs =  60L * 60 * 1000; // 1 hour

    private final long systemUserJWTExpirationMs = 60L * 60 * 1000;
    private final long refreshTokeExpirationInDays = 30L * 24 * 60 * 60 * 1000; // 30 days
    
    public String generateJwtToken(LoggedInUser loggedInUserDetails) throws JsonProcessingException {
        String json = getJsonFromLoggedInUser(loggedInUserDetails);

        Set<String> roleNames = loggedInUserDetails.getRoles().stream()
                .map(role -> role.toString()) // Use toString() to get the name
                .collect(Collectors.toSet());


        return Jwts.builder()
                .setSubject(json)
                .claim("version", loggedInUserDetails.getVersion())
                .addClaims(Map.of(ROLES, roleNames))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getSystemUserToken() {
        String systemUser = "{"
                + "\"id\": 1111111111111111,"
                + "\"password\": \"password123\","
                + "\"fullName\": \"Nivaas\","
                + "\"roles\": [\"ROLE_SYSTEM_USER\"],"
                + "\"primaryContact\": \"9491839431\""
                + "}";
        return generateSystemUserJwtToken(systemUser);
    }

    public boolean isTokenVersionValid(String token, int userVersion) {
        int tokenVersion = getTokenVersion(token);
        return tokenVersion == userVersion;
    }

    public int getTokenVersion(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return (int) claims.get("version");
    }

    private String generateSystemUserJwtToken(String systemUser) {
        return Jwts.builder()
                .setSubject(systemUser)
                .addClaims(Map.of(ROLES, Set.of(ERole.ROLE_SYSTEM_USER)))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + systemUserJWTExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private long getExpiration(int expiryInDays) {
        return new Date().toInstant()
                .plus(expiryInDays, ChronoUnit.DAYS)
                .toEpochMilli();
    }

    public String generateRefreshToken(LoggedInUser loggedInUserDetails) throws JsonProcessingException {
    	String json = getJsonFromLoggedInUser(loggedInUserDetails);
    	Set<String> roleNames = loggedInUserDetails.getRoles().stream()
                .map(role -> role.toString()) // Use toString() to get the name
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(json)
                .claim("version", loggedInUserDetails.getVersion())
                .setIssuedAt(new Date())
                .addClaims(Map.of(ROLES, roleNames))
                .setExpiration(new Date((new Date()).getTime() + refreshTokeExpirationInDays))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

	private String getJsonFromLoggedInUser(LoggedInUser loggedInUserDetails) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	return ow.writeValueAsString(loggedInUserDetails);
	}


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
	public LoggedInUser getUserFromToken(String token) throws JsonProcessingException {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token);
        List<String> roles = (List<String>) claimsJws.getBody().get(ROLES);
        LoggedInUser loggedInUser = new LoggedInUser();
        if(null != claimsJws.getBody().getSubject()) {
        	ObjectMapper mapper = new ObjectMapper();
        	loggedInUser=mapper.readValue(claimsJws.getBody().getSubject(), LoggedInUser.class);
        }
        loggedInUser.setRoles(new HashSet<>(roles));
        return loggedInUser;
    }
}
