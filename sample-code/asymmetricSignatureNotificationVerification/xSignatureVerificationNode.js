// import NodeJS built-in Crypto module
var crypto = require("crypto");

/*-- INPUT --*/
// Input or import the notification/request sender's (Midtrans' provided) Public Key, store into a string variable
var publicKeyString = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWbtlh3eM+bW3n5AFj42
wddC4L7tQqbLiCFbTv8K67yng9iwK5mEn+UMXiRhvB2JVWFafCPPxFamsiVG3Mjn
eGjC0BYgmpmw4qXnAnyO3nCdCuPtmZ3ljhKvSTPdWZxrcLi1Xa9V/+Pzb8hrjb5i
wMn6SZFNeMmZYgFKiSeueo6TPln2PoTqXCzs1HtsM8eUVe8GAsjJe/3dYl992nyX
OpG21GgNu8o5T3WOptPg6GdDTWkTWUu483yRbVVy04Pz4L8DDZTDv+WcsAViDn1r
A/jB1Auj/UGKx2ovGcBH/a/hor5TbABbODU6cPTHT54K3sSZtvZNV4eFDB1f/4wd
fwIDAQAB
-----END PUBLIC KEY-----`;

// Sample raw string data to verify against signature
var notificationHttpMethod = "POST";
var notificationUrlPath = "/v1.0/debit/notify";
var notificationBodyJsonString =
  '{"originalPartnerReferenceNo":"GP24043015193402809","originalReferenceNo":"A120240430081940S9vu8gSjaRID","merchantId":"G099333790","amount":{"value":"102800.00","currency":"IDR"},"latestTransactionStatus":"00","transactionStatusDesc":"SUCCESS","additionalInfo":{"refundHistory":[]}}'; // Sample notification body, replace with actual data you receive from Midtrans
// minify the JSON notification body
var minifiedNotificationBodyJsonString = JSON.stringify(
  JSON.parse(notificationBodyJsonString),
);
// calculate the SHA-256 hash of the minified notification body
var hashedNotificationBodyJsonString = crypto
  .createHash("sha256")
  .update(minifiedNotificationBodyJsonString)
  .digest("hex");

var NotificationHeaderXTimestamp = "2024-05-02T14:43:08+07:00";

var rawStringDataToVerifyAgainstSignature =
  notificationHttpMethod +
  ":" +
  notificationUrlPath +
  ":" +
  hashedNotificationBodyJsonString +
  ":" +
  NotificationHeaderXTimestamp;
// Sample value:
// rawStringDataToVerifyAgainstSignature = "POST:/v1.0/debit/notify:79ebbe6a6b695262dd686d0dedafc57c94e3b3dededf8d63971f8a95699ace85:2024-05-02T14:43:08+07:00";

var receivedSignatureStringToVerify =
  "RoJnP2tH/YiOhHM/lMVBMSAuzRmS8VrWdIy04Qqyb56daV7oWFMFoMMzqnjQ+q0MIUalYgU094GWQnCx2c29xb1kkqHhv2+iJ9xl6NjGmFGYqyvcKvUDAV83Y1Mw9JnsEcjcupdGw9/MRv/mm2GMrQ+BCZGfc4a46JDyPZbcY294vDGqs5rFBN6iYer5ro4cAQGo9hET2G82Y+j50vCyO/79GFE4vB1rvtu6PK2Bxi+vTYV8k7P7PS8tOPWM2O+kjiVWjwvLR99Botou+a8sxlQqZaihfWMKcByzV+Lgkr9cptpjys+1NIRWT1ad/sJBSLHyldzC3q2oRn5z5oZmyg==";

/*-- SIGNATURE VERIFICATION --*/
// create verifier object
var verifier = crypto.createVerify("SHA256");
// input the originalStringBeforeSigned into the verifier object
verifier.update(rawStringDataToVerifyAgainstSignature, "utf8");
// Verify signature against original data and public key
var isSignatureVerified = verifier.verify(
  publicKeyString,
  receivedSignatureStringToVerify,
  "base64",
);

/*-- OUTPUT --*/
console.log("originalStringBeforeSigned:");
console.log(rawStringDataToVerifyAgainstSignature);

console.log("signatureStringToVerify:");
console.log(receivedSignatureStringToVerify);
console.log("\r\n");

console.log("isSignatureVerified:");
console.log(isSignatureVerified);
