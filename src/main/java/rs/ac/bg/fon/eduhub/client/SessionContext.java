package rs.ac.bg.fon.eduhub.client;

/**
 * Čuva podatke o trenutnoj sesiji konzolnog klijenta: JWT token dobijen
 * prilikom prijave i email trenutno ulogovanog korisnika.
 *
 * <p>Klasa se koristi kao jednostavan singleton — postoji samo jedna
 * aktivna sesija tokom izvršavanja konzolne aplikacije.</p>
 */
public class SessionContext {

    private static SessionContext instance;

    private String token;
    private String userEmail;

    private SessionContext() {
    }

    /**
     * Vraća jedinu instancu {@code SessionContext} klase.
     *
     * @return instanca sesije.
     */
    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    /**
     * Postavlja JWT token i email korisnika nakon uspešne prijave.
     *
     * @param token     JWT token dobijen od servera.
     * @param userEmail email prijavljenog korisnika.
     */
    public void login(String token, String userEmail) {
        this.token = token;
        this.userEmail = userEmail;
    }

    /**
     * Briše podatke o sesiji (odjava korisnika).
     */
    public void logout() {
        this.token = null;
        this.userEmail = null;
    }

    /**
     * Proverava da li je neki korisnik trenutno ulogovan.
     *
     * @return {@code true} ako postoji aktivan token.
     */
    public boolean isLoggedIn() {
        return token != null;
    }

    /**
     * Vraća trenutni JWT token.
     *
     * @return JWT token, ili {@code null} ako korisnik nije ulogovan.
     */
    public String getToken() {
        return token;
    }

    /**
     * Vraća email trenutno ulogovanog korisnika.
     *
     * @return email korisnika, ili {@code null} ako korisnik nije ulogovan.
     */
    public String getUserEmail() {
        return userEmail;
    }
}