    package com.example.store_rating_app.service;

    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.function.Function;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;

    import com.example.store_rating_app.entity.User;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.security.Keys;

    @Service
    public class JwtService {

        @Value("${jwt.secret}")
        private String SECRET_KEY;
        private final long EXPIRATION_TIME = 1000L * 60 * 60 * 24; // 24 hours in ms

        private java.security.Key getSigningKey() {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String generateToken(User user) {
            Map<String, Object> claims = new HashMap<>();

            // Put authorities as a list
            claims.put("authorities", List.of("ROLE_" + user.getRole()));

            return createToken(claims, user.getEmail());
        }

        private String createToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis())) // correct
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // correct
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                return claimsResolver.apply(claims);
            } catch (Exception e) {
                return null;
            }
        }

        public boolean validateToken(String token, User user) {
            String username = extractUsername(token);
            return (username != null
                    && username.equals(user.getEmail())
                    && !isTokenExpired(token));
        }

        private boolean isTokenExpired(String token) {
            Date exp = extractExpiration(token);
            return exp == null || exp.before(new Date());
        }
    }
