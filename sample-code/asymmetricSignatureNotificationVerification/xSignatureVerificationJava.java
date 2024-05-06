import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
// import this package https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

  public static void main(String[] args) throws Exception {
    /*-- INPUT --*/
    // Input or import the notification/request sender's (Midtrans' provided) Public
    // Key, store into a string variable
    String publicKeyString = "-----BEGIN PUBLIC KEY----- MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWbtlh3eM+bW3n5AFj42 wddC4L7tQqbLiCFbTv8K67yng9iwK5mEn+UMXiRhvB2JVWFafCPPxFamsiVG3Mjn eGjC0BYgmpmw4qXnAnyO3nCdCuPtmZ3ljhKvSTPdWZxrcLi1Xa9V/+Pzb8hrjb5i wMn6SZFNeMmZYgFKiSeueo6TPln2PoTqXCzs1HtsM8eUVe8GAsjJe/3dYl992nyX OpG21GgNu8o5T3WOptPg6GdDTWkTWUu483yRbVVy04Pz4L8DDZTDv+WcsAViDn1r A/jB1Auj/UGKx2ovGcBH/a/hor5TbABbODU6cPTHT54K3sSZtvZNV4eFDB1f/4wd fwIDAQAB -----END PUBLIC KEY-----";
    /*-- Below values are from the received HTTP notification/request --*/
    String receivedSignatureStringToVerify = "RoJnP2tH/YiOhHM/lMVBMSAuzRmS8VrWdIy04Qqyb56daV7oWFMFoMMzqnjQ+q0MIUalYgU094GWQnCx2c29xb1kkqHhv2+iJ9xl6NjGmFGYqyvcKvUDAV83Y1Mw9JnsEcjcupdGw9/MRv/mm2GMrQ+BCZGfc4a46JDyPZbcY294vDGqs5rFBN6iYer5ro4cAQGo9hET2G82Y+j50vCyO/79GFE4vB1rvtu6PK2Bxi+vTYV8k7P7PS8tOPWM2O+kjiVWjwvLR99Botou+a8sxlQqZaihfWMKcByzV+Lgkr9cptpjys+1NIRWT1ad/sJBSLHyldzC3q2oRn5z5oZmyg==";
    // Sample raw string data to verify against signature
    String notificationHttpMethod = "POST";
    String notificationUrlPath = "/v1.0/debit/notify";
    String notificationBodyJsonString = "{\"originalPartnerReferenceNo\":\"GP24043015193402809\",\"originalReferenceNo\":\"A120240430081940S9vu8gSjaRID\",\"merchantId\":\"G099333790\",\"amount\":{\"value\":\"102800.00\",\"currency\":\"IDR\"},\"latestTransactionStatus\":\"00\",\"transactionStatusDesc\":\"SUCCESS\",\"additionalInfo\":{\"refundHistory\":[]}}"; // Sample notification body, replace with actual data you receive from Midtrans
    String notificationHeaderXTimestamp = "2024-05-02T14:43:08+07:00";
    /*-- end of values from the received HTTP notification/request --*/

    // minify the JSON notification body
    String minifiedNotificationBodyJsonString = minifyRequestBodyJSON(notificationBodyJsonString);
    // calculate the SHA-256 hash of the minified notification body
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashedNotificationBodyJsonString = digest.digest(minifiedNotificationBodyJsonString.getBytes());
    String hashedNotificationBodyJsonStringHex = bytesToHex(hashedNotificationBodyJsonString)
        // ensure the hash string is all in lowercase
        .toLowerCase();

    String rawStringDataToVerifyAgainstSignature = notificationHttpMethod +
        ":" +
        notificationUrlPath +
        ":" +
        hashedNotificationBodyJsonStringHex +
        ":" +
        notificationHeaderXTimestamp;

    /*-- SIGNATURE VERIFICATION --*/
    Signature verifier = Signature.getInstance("SHA256withRSA");
    verifier.initVerify(getPublicKey(publicKeyString));
    verifier.update(rawStringDataToVerifyAgainstSignature.getBytes("UTF-8"));
    boolean isSignatureVerified = verifier.verify(Base64.getDecoder().decode(receivedSignatureStringToVerify));

    /*-- OUTPUT --*/
    System.out.println("originalStringBeforeSigned:");
    System.out.println(rawStringDataToVerifyAgainstSignature);

    System.out.println("signatureStringToVerify:");
    System.out.println(receivedSignatureStringToVerify);
    System.out.println("\r\n");

    System.out.println("isSignatureVerified:");
    System.out.println(isSignatureVerified);
  }

  private static String minifyRequestBodyJSON(String requestBody) throws JsonProcessingException {
    String minifiedRequestBody = ""; // default to empty string, e.g. when there is no requestBody
    if (requestBody.length() > 0) {
      minifiedRequestBody = (new ObjectMapper())
          .readTree(requestBody)
          .toString();
    }
    // simple way: map JSON string as JSON Object then convert back to string, it
    // will remove unnecessary whitespaces
    return minifiedRequestBody;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xFF & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  // Convert PEM formatted public key string to PublicKey object
  private static PublicKey getPublicKey(String publicKeyString) throws Exception {
    String publicKeyPEM = publicKeyString
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);

    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }
}
