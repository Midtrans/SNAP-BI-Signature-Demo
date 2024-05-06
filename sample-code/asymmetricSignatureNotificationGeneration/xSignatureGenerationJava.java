import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
// import this package https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
  public static void main(String[] args) throws Exception {
    /*-- INPUT --*/
    // Input or import the HTTP Notification/request sender's Private Key,, store into a string variable
    // Doesnt matter it includes white space or not, will clean up later
    String privateKeyString = "-----BEGIN PRIVATE KEY----- MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVZu2WHd4z5tbe fkAWPjbB10Lgvu1CpsuIIVtO/wrrvKeD2LArmYSf5QxeJGG8HYlVYVp8I8/EVqay JUbcyOd4aMLQFiCambDipecCfI7ecJ0K4+2ZneWOEq9JM91ZnGtwuLVdr1X/4/Nv yGuNvmLAyfpJkU14yZliAUqJJ656jpM+WfY+hOpcLOzUe2wzx5RV7wYCyMl7/d1i X33afJc6kbbUaA27yjlPdY6m0+DoZ0NNaRNZS7jzfJFtVXLTg/PgvwMNlMO/5Zyw BWIOfWsD+MHUC6P9QYrHai8ZwEf9r+GivlNsAFs4NTpw9MdPngrexJm29k1Xh4UM HV//jB1/AgMBAAECggEADcXGyitns/4oOauGyeYjUxw+eIxxP88zfRGiIralMZUb Fi7wEpzc2oaZbMZK0jYg1mOanU4J1a4tQMfp7+l/WRzDNL6Nc+MOKN6lXJfR7dSQ zZO0cBBbvIyhZwymb5/ZUbNdWM0UjvnbE6d0rsTpwp77+TMxYpynDJ9U2S70ySxe fGi7x4rK02/8xmj1fY/Ls+uoYhF4IbGJIkFvrlAdWyFvv6sCGBYxl5sPuitgQxPv Yc7dS7HE+6nZ+wWgoIXjC4CfxHBa9u3dfu3XAnPY3xpnVsbxCAH9OOIrZPY0C/Pq gj+EElCBChUzDiedV3XipKUweDA+64sNm5NjqlqU8QKBgQDFf5a9vHVf9r2CTOgm z5wTrK7aTwKwCNC4zpZUxDR8k7AV2gTelabpPWqUMZyRMUPs2hyBvhEWVNKxx3qs 7BAVpmcttuS5/dLNVdhNPUC2IgZqf3jWH2aS3lkci8DATQCphrEXZ3jrNaIRtrSC kflE+yY8YP8b7ObktgvHz39JMQKBgQDBqCsMmOsQAveIQk5FGAMxsFU9G7OIBFHo xpwkKUCjoqGxM+YQqDvcVeXNd/HdLnmR05//eoONemuDyYy8XRoud4tpaRKU/FK7 06WdljztB5rn2+cdUbmcU0fZiRQk+WuXsopZn3GUNvA/fQOW9qs7B5Rm5W71FeGk ZgsvlAUlrwKBgQCUgFFaLWCcXa01UpqkxCp5aLi5EfvVXWuD6mKDLlzA51PZums6 6o/shO+kqoEtczu91mrk64NxpSof3vxRFdcqUEr4xrLJXx+oocnYmhwUVxU38s1r Q4UfHe0nV7YBYmUDE3IJRRZY1aUdaKHmI9iok6e2csCfwMwEYRYOkekFoQKBgCyy Oa1gpfA+Hw+N7i64ShRv1FyURi2Agb8uB9+4vbiG0rbpeZIioh5KnQ19P4+DKH/l zinTBwXiWWpDXH4lJuPOp5ierbFBQ38ibDkg8dLrTG9zK7ZypFpWRmEI6GNYReLv TEs/J6HDxFOC8Q8ow4COUUwmbCOY90lQXAiRK1b1AoGAScK1db9X5Ct+JtnjYyzi GYLwyPhHCQIVmEDfuvbMs74woQsrBm3dBB3b2vf9pqcSZB4WAbTQ3wCdxCJKg6MN 6eXBO1M1lcIpZjCDyiXXi7PozNmkasRDUyDFwxlnyDLdRHL3FD+dh6do32FwGLBQ u256RIyMB13qIuqrc91Z0hM= -----END PRIVATE KEY-----";
    // Clean up the privateKeyString
    String privateKeyPkcs8Pem = privateKeyString
        .replace("-----BEGIN PRIVATE KEY-----", "") // remove header
        .replace("-----END PRIVATE KEY-----", "") // remove footer
        .replaceAll("\\s+", ""); // remove all white spaces
    // Decode private key from PEM string format into byte[]
    byte[] privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyPkcs8Pem.getBytes());
    
    String httpMethod = "POST".toUpperCase(); // ensure all UPPERCASE
    String endpointUrl = "/v1.0/debit/notify";
    // ensure requestBody is a valid JSON string format, dont worry about whitespaces, it will be minified later
    String requestBody = "{\"createdTime\":\"2020-01-01T00:00:00+07:00\",\"latestTransactionStatus\":\"00\",\"originalReferenceNo\":\"gopayOrderId\",\"finishedTime\":\"2020-01-02T00:00:00+07:00\",\"originalPartnerReferenceNo\":\"merchant-order-id\",\"transactionStatusDesc\":\"desc\",\"originalExternalId\":\"merchant-order-id\",\"additionalInfo\":{\"payOptionDetails\":[{\"payMethod\":\"GOPAY\",\"payOption\":\"GOPAY_WALLET\"}],\"fraudStatus\":\"accept\",\"refundHistory\":[{\"refundNo\":\"96194816941239812\",\"partnerReferenceNo\":\"239850918204981205970\",\"refundAmount\":{\"value\":\"12345678.00\",\"currency\":\"IDR\"},\"refundStatus\":\"00\",\"refundDate\":\"2020-12-23T07:44:16+07:00\",\"reason\":\"Customer Complain\"}],\"validUpTo\":\"2021-01-01T00:00:00+07:00\"}}";
    String timestampString = new java.util.Date().toInstant().toString();
    // sample hardcoded timestamp value
    // timestampString = "2024-03-05T10:37:16.196Z";

    /*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
    String minifiedRequestBody = minifyRequestBodyJSON(requestBody);
    String lowerHexSha256MinifiedRequestBody = sha256Hex(minifiedRequestBody).toLowerCase();

    String combinedStringToSign = 
      httpMethod + ":" + 
      endpointUrl + ":" + 
      lowerHexSha256MinifiedRequestBody + ":" + 
      timestampString;

    /*-- SIGNATURE GENERATION --*/
    // Create a PrivateKey object from the decoded bytes
    PrivateKey myPrivateKey = java.security.KeyFactory.getInstance("RSA")
        .generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes));
    // Create a Signature object with the specified algorithm
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(myPrivateKey);
    // Update the signature with the data to be signed
    signature.update(combinedStringToSign.getBytes());
    // Generate the signature bytes
    byte[] signatureBytes = signature.sign();
    // To convert to readable String, base64 encode the bytes
    String xSignatureString = Base64.getEncoder().encodeToString(signatureBytes);

    /*-- OUTPUT --*/
    System.out.println("xSignatureString:");
    System.out.println(xSignatureString);
    // Next, this xSignatureString is the value sent as `X-SIGNATURE` HTTP header on HTTP Notification (Webhook).

    /* Sample xSignatureString: 
eUGsomFLNsjWzehgGsa317MqcVOgscay/Hml8yNmOsFhmoBM7pso+UPDgSe34anXvecSQkf5NOr0fbr8k9qdyyoWiYX7DTq/Wnz8XC0cRCCbNZfyLIyF33HO9cnvxOcRQq9DuWlQtld+uEluuivVdN2TiaWLpUTmfB/H7noZnXWWDj6FYa4n5kSo4o3aTeFYAF9EmLp0Tcsl8CiHSQCrQnIbB8M2+puLwyJ1l2OocvZ5qgjf5f2DqlCbemsSMzYzjN+YMrb0kiDx0TiW5UTk+WZass43xIfb3vdrlX+U2JSk5YsJ8lFpp9F+7FId4cyncElNFEbt3zWuP4iDzXteCg==
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
}
