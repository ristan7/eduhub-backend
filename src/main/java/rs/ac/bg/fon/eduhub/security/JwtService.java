package rs.ac.bg.fon.eduhub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Servis za generisanje i validaciju JWT tokena koji se koriste za
 * stateless autentifikaciju korisnika. Tajni ključ i vreme trajanja
 * tokena konfigurišu se preko {@code application.properties}
 * ({@code jwt.secret}, {@code jwt.expiration-ms}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Kreira potpisni ključ na osnovu konfigurisanog tajnog niza karaktera.
     *
     * @return ključ za potpisivanje i verifikaciju JWT tokena
     */
    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generiše novi JWT token za datog korisnika, sa opcionim dodatnim
     * poljima (claim-ovima).
     *
     * @param userDetails podaci o korisniku za koga se generiše token; njegov username postaje subjekt tokena
     * @param extraClaims dodatna polja koja se uključuju u token (npr. identifikator korisnika, uloga), ili {@code null}
     * @return generisani i potpisani JWT token
     */
    public String generate(UserDetails userDetails, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        Map<String, Object> claims = new HashMap<>(extraClaims == null ? Map.of() : extraClaims);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key())
                .compact();
    }

    /**
     * Izvlači email adresu (subjekt) iz JWT tokena.
     *
     * @param token JWT token
     * @return email adresa korisnika kojem token pripada
     * @throws JwtException ako je token neispravan, oštećen ili istekao
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Proverava da li je token validan za datog korisnika - da li mu
     * odgovara email adresa i da li još nije istekao.
     *
     * @param token JWT token koji se proverava
     * @param userDetails podaci o korisniku sa kojim se token upoređuje
     * @return {@code true} ako je token validan, inače {@code false}
     */
    public boolean isValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Proverava da li je tokenu istekao rok važenja.
     *
     * @param token JWT token
     * @return {@code true} ako je token istekao, inače {@code false}
     */
    private boolean isExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parsira i vraća sva polja (claim-ove) iz JWT tokena.
     *
     * @param token JWT token
     * @return svi claim-ovi tokena
     * @throws JwtException ako je token neispravan, oštećen ili istekao
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}