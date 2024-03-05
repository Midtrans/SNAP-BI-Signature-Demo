// import NodeJS built-in Crypto module
var crypto = require("crypto");

/*-- INPUT --*/
var clientSecret = "myclientsecret";
var HTTPMethod = ("POST").toUpperCase(); // ensure all UPPERCASE
var endpointUrl = "/v1.0/qr/qr-mpm-generate";
var accessToken = "myaccesstoken"; // without the `Bearer ` prefix
// ensure requestBody is a valid JSON string format, don't worry about whitespace, we'll clean up later
var requestBody = `{
  "partnerReferenceNo": "2020102900000000000001",
  "amount": {
    "value": "12345678.00",
    "currency": "IDR"
  },
  "feeAmount": {
    "value": "12345678.00",
    "currency": "IDR"
  },
  "merchantId": "00007100010926",
  "terminalId": "213141251124",
  "validityPeriod": "2009-07-03T12:08:56-07:00"
}`; 
var timestampString = (new Date()).toISOString(); // Generate current X-TIMESTAMP in ISO format
// Sample hardcoded timestamp value
// timestampString = "2024-03-05T10:37:16.196Z"

/*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
// minify the request body, simple way: parse then stringify it back
var minifiedRequestBody = JSON.stringify(JSON.parse(requestBody));
console.log("minifiedRequestBody",minifiedRequestBody);
// create sha256 hash in `hex` format, lowercase
var lowerHexSha256MinifiedRequestBody = crypto.createHash("sha256")
  .update(minifiedRequestBody)
  .digest("hex")
  .toLowerCase();

var combinedStringToSign = 
  HTTPMethod.toUpperCase()+":"+
  endpointUrl+":"+
  accessToken+":"+
  lowerHexSha256MinifiedRequestBody+":"+
  timestampString;

/*-- SIGNATURE GENERATION --*/
var xSignatureString = crypto.createHmac("sha512", clientSecret)
  .update(combinedStringToSign)
  .digest("base64"); // convert it to base64 String encoding

/*-- OUTPUT --*/
console.log("xSignatureString:");
console.log(xSignatureString);
// Next, this xSignatureString is the value to be used as `X-SIGNATURE` HTTP headers.

/* Sample signatureString: 
wmzT7yjg1SxxCsxNsFgCR9SYXiZGGbceF6fV8xe97sAMHxZ+7OEBMIBmvb6Sj6GcksGp3iv77DiomgSbcST3XQ==
*/

/* 
// -- Alternative Signature Generation using another module --
// `npm i jsrsasign` to import https://www.npmjs.com/package/jsrsasign
var jsrsasign = require("jsrsasign");

var macObject = new jsrsasign.KJUR.crypto.Mac({ alg: "hmacsha512", "pass": clientSecret });
macObject.updateString(combinedStringToSign);
var macSignatureHex = macObject.doFinal();
// the result was in Hex format, convert it into Base64 String format
var xSignatureString =  Buffer.from(macSignatureHex, "hex").toString("base64");
*/