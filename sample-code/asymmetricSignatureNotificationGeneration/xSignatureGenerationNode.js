// import NodeJS built-in Crypto module
var crypto = require("crypto");

/*-- INPUT --*/
// Input or import the HTTP Notification/request sender's Private Key, store into a string variable
var privateKeyString = `-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVZu2WHd4z5tbe
fkAWPjbB10Lgvu1CpsuIIVtO/wrrvKeD2LArmYSf5QxeJGG8HYlVYVp8I8/EVqay
JUbcyOd4aMLQFiCambDipecCfI7ecJ0K4+2ZneWOEq9JM91ZnGtwuLVdr1X/4/Nv
yGuNvmLAyfpJkU14yZliAUqJJ656jpM+WfY+hOpcLOzUe2wzx5RV7wYCyMl7/d1i
X33afJc6kbbUaA27yjlPdY6m0+DoZ0NNaRNZS7jzfJFtVXLTg/PgvwMNlMO/5Zyw
BWIOfWsD+MHUC6P9QYrHai8ZwEf9r+GivlNsAFs4NTpw9MdPngrexJm29k1Xh4UM
HV//jB1/AgMBAAECggEADcXGyitns/4oOauGyeYjUxw+eIxxP88zfRGiIralMZUb
Fi7wEpzc2oaZbMZK0jYg1mOanU4J1a4tQMfp7+l/WRzDNL6Nc+MOKN6lXJfR7dSQ
zZO0cBBbvIyhZwymb5/ZUbNdWM0UjvnbE6d0rsTpwp77+TMxYpynDJ9U2S70ySxe
fGi7x4rK02/8xmj1fY/Ls+uoYhF4IbGJIkFvrlAdWyFvv6sCGBYxl5sPuitgQxPv
Yc7dS7HE+6nZ+wWgoIXjC4CfxHBa9u3dfu3XAnPY3xpnVsbxCAH9OOIrZPY0C/Pq
gj+EElCBChUzDiedV3XipKUweDA+64sNm5NjqlqU8QKBgQDFf5a9vHVf9r2CTOgm
z5wTrK7aTwKwCNC4zpZUxDR8k7AV2gTelabpPWqUMZyRMUPs2hyBvhEWVNKxx3qs
7BAVpmcttuS5/dLNVdhNPUC2IgZqf3jWH2aS3lkci8DATQCphrEXZ3jrNaIRtrSC
kflE+yY8YP8b7ObktgvHz39JMQKBgQDBqCsMmOsQAveIQk5FGAMxsFU9G7OIBFHo
xpwkKUCjoqGxM+YQqDvcVeXNd/HdLnmR05//eoONemuDyYy8XRoud4tpaRKU/FK7
06WdljztB5rn2+cdUbmcU0fZiRQk+WuXsopZn3GUNvA/fQOW9qs7B5Rm5W71FeGk
ZgsvlAUlrwKBgQCUgFFaLWCcXa01UpqkxCp5aLi5EfvVXWuD6mKDLlzA51PZums6
6o/shO+kqoEtczu91mrk64NxpSof3vxRFdcqUEr4xrLJXx+oocnYmhwUVxU38s1r
Q4UfHe0nV7YBYmUDE3IJRRZY1aUdaKHmI9iok6e2csCfwMwEYRYOkekFoQKBgCyy
Oa1gpfA+Hw+N7i64ShRv1FyURi2Agb8uB9+4vbiG0rbpeZIioh5KnQ19P4+DKH/l
zinTBwXiWWpDXH4lJuPOp5ierbFBQ38ibDkg8dLrTG9zK7ZypFpWRmEI6GNYReLv
TEs/J6HDxFOC8Q8ow4COUUwmbCOY90lQXAiRK1b1AoGAScK1db9X5Ct+JtnjYyzi
GYLwyPhHCQIVmEDfuvbMs74woQsrBm3dBB3b2vf9pqcSZB4WAbTQ3wCdxCJKg6MN
6eXBO1M1lcIpZjCDyiXXi7PozNmkasRDUyDFwxlnyDLdRHL3FD+dh6do32FwGLBQ
u256RIyMB13qIuqrc91Z0hM=
-----END PRIVATE KEY-----`;
var httpMethod = "POST".toUpperCase(); // ensure all UPPERCASE
var endpointUrl = "/v1.0/debit/notify";
var requestBody = `{
    "createdTime": "2020-01-01T00:00:00+07:00",
    "latestTransactionStatus": "00",
    "originalReferenceNo": "gopayOrderId",
    "finishedTime": "2020-01-02T00:00:00+07:00",
    "originalPartnerReferenceNo": "merchant-order-id",
    "transactionStatusDesc": "desc",
    "originalExternalId": "merchant-order-id",
    "additionalInfo": {
        "payOptionDetails": [{
            "payMethod": "GOPAY",
            "payOption": "GOPAY_WALLET"
        }],
        "fraudStatus": "accept",
        "refundHistory": [{
            "refundNo": "96194816941239812",
            "partnerReferenceNo": "239850918204981205970",
            "refundAmount": {
                "value": "12345678.00",
                "currency": "IDR"
            },
            "refundStatus": "00",
            "refundDate": "2020-12-23T07:44:16+07:00",
            "reason": "Customer Complain"
        }],
        "validUpTo": "2021-01-01T00:00:00+07:00"
    }
}`; 
var timestampString = (new Date()).toISOString(); // Generate current X-TIMESTAMP in ISO format
// Sample hardcoded timestamp value
// timestampString = "2024-03-05T10:37:16.196Z"

/*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
var minifiedRequestBody = ""; // default to empty string, e.g. when there is no requestBody
if(requestBody.length > 0){
  // minify the request body, simple way: parse then stringify it back
  var minifiedRequestBody = JSON.stringify(JSON.parse(requestBody));
}
// create sha256 hash in `hex` format, lowercase
var lowerHexSha256MinifiedRequestBody = crypto.createHash("sha256")
  .update(minifiedRequestBody)
  .digest("hex")
  .toLowerCase();

var combinedStringToSign = 
  httpMethod+":"+
  endpointUrl+":"+
  lowerHexSha256MinifiedRequestBody+":"+
  timestampString;

/*-- SIGNATURE GENERATION --*/
var myPrivateKey = crypto.createPrivateKey({ key: privateKeyString, format: 'pem' });
var xSignatureString = crypto.createSign('RSA-SHA256')
  .update(combinedStringToSign)
  .sign(myPrivateKey, 'base64');

/*-- OUTPUT --*/
console.log("xSignatureString:");
console.log(xSignatureString);
// Next, this xSignatureString is the value sent as `X-SIGNATURE` HTTP header on HTTP Notification (Webhook).

/* Sample xSignatureString: 
eUGsomFLNsjWzehgGsa317MqcVOgscay/Hml8yNmOsFhmoBM7pso+UPDgSe34anXvecSQkf5NOr0fbr8k9qdyyoWiYX7DTq/Wnz8XC0cRCCbNZfyLIyF33HO9cnvxOcRQq9DuWlQtld+uEluuivVdN2TiaWLpUTmfB/H7noZnXWWDj6FYa4n5kSo4o3aTeFYAF9EmLp0Tcsl8CiHSQCrQnIbB8M2+puLwyJ1l2OocvZ5qgjf5f2DqlCbemsSMzYzjN+YMrb0kiDx0TiW5UTk+WZass43xIfb3vdrlX+U2JSk5YsJ8lFpp9F+7FId4cyncElNFEbt3zWuP4iDzXteCg==
*/

/* 
// -- Alternative Signature Generation using another module --
// `npm i jsrsasign`
// to import https://www.npmjs.com/package/jsrsasign
var jsrsasign = require("jsrsasign");
var sigObject = new jsrsasign.KJUR.crypto.Signature({ alg: "SHA256withRSA" });
sigObject.init(privateKeyString);
sigObject.updateString(combinedStringToSign);
var signatureHex = sigObject.sign();
// the result was in Hex format, convert it into Base64 String format
var xSignatureString = Buffer.from(signatureHex, "hex").toString("base64");
*/
