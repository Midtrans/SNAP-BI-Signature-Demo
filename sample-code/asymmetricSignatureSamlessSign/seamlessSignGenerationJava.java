import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            /*-- INPUT --*/
            // Input or import your Private Key, store into a string variable
            String privateKeyString = "-----BEGIN PRIVATE KEY----- MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAlas/ul92ZfLV8cxf k7kQU0V41Sp9RlDylXTTmUv92scPyVsTRQSN8RpSe/DKvZfdm+mWd2wYhPz14cg2 IhQNJwIDAQABAkAB80dTUGckkPOEwRsFu8WgsCkQ7grP3cIrfGg9eSYHscdCiNsD ZbChg7FGbyUwMhfTRgfsRlE5qBGR9X6p9yKBAiEAyIML9J/JYsoTBeaxbJYg13Kq /FKrBkAzG34tcdYylKECIQC/Fk/a0pWwfKoik+wlZW6f1MDw++n9Sv7X+5D80HAE xwIhALfQ6zTnBwe5mJbgVebl+lWImZeXcZHZaQDbO24Qn24BAiEAs7UGEKMvR7VW RAKdeWX1LbdmZLxliGK5XOInrrtQPg0CICEW4K0j0wYS+kca2yX5y3qbLWE0EttN Rocmtyw5WnLB -----END PRIVATE KEY-----";

            String rawSeamlessData = "mobileNumber=6281234000001&paymentType=gopay";
            String seamlessData = URLEncoder.encode(rawSeamlessData, StandardCharsets.UTF_8.toString());

            // Convert PEM formatted key string to PrivateKey object
            // Remove the "BEGIN" and "END" lines, as well as any whitespace
            privateKeyString = privateKeyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            // Decode the base64 encoded private key
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

            // Generate private key
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            /*-- SIGNATURE GENERATION --*/
            // Create the RSA-SHA256 signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(seamlessData.getBytes(StandardCharsets.UTF_8));

            // Generate the signature and encode it to base64
            byte[] signedData = signature.sign();
            String unencodedSeamlessSign = Base64.getEncoder().encodeToString(signedData);
            String seamlessSign = URLEncoder.encode(unencodedSeamlessSign, StandardCharsets.UTF_8.toString());
            
            /*-- OUTPUT --*/
            System.out.println("seamlessSign:");
            System.out.println(seamlessSign);
            // Next, this seamlessSign is the value to be used as `seamlessSign` URL Query Parameter.
  
            /* Sample seamlessSign: 
Hum8Z%2Byr%2FOTTqGxo4halA8I4LcGySMsANl24opI52Tdp2SBPiYDrO8KOd%2Bz5M5O16Cth21fEvBoph9hilzGtaw%3D%3D
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
