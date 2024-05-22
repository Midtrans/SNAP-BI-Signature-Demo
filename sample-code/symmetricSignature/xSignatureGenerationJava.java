import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
// import this package https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
  public static void main(String[] args) throws Exception {
    /*-- INPUT --*/
    String clientSecret = "myclientsecret";
    String httpMethod = "POST".toUpperCase(); // ensure all UPPERCASE
    String endpointUrl = "/v1.0/qr/qr-mpm-generate";
    String accessToken = "myaccesstoken";
    // ensure requestBody is a valid JSON string format, dont worry about whitespaces, it will be minified later. Fill it with "" empty string for GET request.
    String requestBody = "{\"partnerReferenceNo\":\"2020102900000000000001\",\"amount\":{\"value\":\"12345678.00\",\"currency\":\"IDR\"},\"feeAmount\":{\"value\":\"12345678.00\",\"currency\":\"IDR\"},\"merchantId\":\"00007100010926\",\"terminalId\":\"213141251124\",\"validityPeriod\":\"2009-07-03T12:08:56-07:00\"}";
    String timestampString = new java.util.Date().toInstant().toString();
    // sample hardcoded timestamp value
    // timestampString = "2024-03-05T10:37:16.196Z";

    /*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
    String minifiedRequestBody = minifyRequestBodyJSON(requestBody);
    String lowerHexSha256MinifiedRequestBody = sha256Hex(minifiedRequestBody).toLowerCase();

    String combinedStringToSign = 
      httpMethod + ":" + 
      endpointUrl + ":" + 
      accessToken + ":" + 
      lowerHexSha256MinifiedRequestBody + ":" + 
      timestampString;

    /*-- SIGNATURE GENERATION --*/
    String xSignatureString = hmacSha512(clientSecret, combinedStringToSign);

    /*-- OUTPUT --*/
    System.out.println("xSignatureString:");
    System.out.println(xSignatureString);
    // Next, this xSignatureString is the value to be used as `X-SIGNATURE` HTTP headers.

    /* Sample xSignatureString: 
wmzT7yjg1SxxCsxNsFgCR9SYXiZGGbceF6fV8xe97sAMHxZ+7OEBMIBmvb6Sj6GcksGp3iv77DiomgSbcST3XQ==
    */
  }

  private static String minifyRequestBodyJSON(String requestBody) throws JsonProcessingException {
    String minifiedRequestBody = ""; // default to empty string, e.g. when there is no requestBody
    if(requestBody.length() > 0){
      minifiedRequestBody = (new ObjectMapper())
        .readTree(requestBody)
        .toString();
    }
    // simple way: map JSON string as JSON Object then convert back to string, it will remove unnecessary whitespaces
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

  private static String sha256Hex(String input) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(input.getBytes());
    return bytesToHex(hash);
  }

  private static String hmacSha512(String key, String input) throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
    Mac mac = Mac.getInstance("HmacSHA512");
    mac.init(secretKey);
    byte[] hash = mac.doFinal(input.getBytes());
    return Base64.getEncoder().encodeToString(hash);
  }
}
