// import NodeJS built-in Crypto module
var crypto = require("crypto");

/*-- INPUT --*/
// Input or import your Private Key, store into a string variable
var privateKeyString = `-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAlas/ul92ZfLV8cxf
k7kQU0V41Sp9RlDylXTTmUv92scPyVsTRQSN8RpSe/DKvZfdm+mWd2wYhPz14cg2
IhQNJwIDAQABAkAB80dTUGckkPOEwRsFu8WgsCkQ7grP3cIrfGg9eSYHscdCiNsD
ZbChg7FGbyUwMhfTRgfsRlE5qBGR9X6p9yKBAiEAyIML9J/JYsoTBeaxbJYg13Kq
/FKrBkAzG34tcdYylKECIQC/Fk/a0pWwfKoik+wlZW6f1MDw++n9Sv7X+5D80HAE
xwIhALfQ6zTnBwe5mJbgVebl+lWImZeXcZHZaQDbO24Qn24BAiEAs7UGEKMvR7VW
RAKdeWX1LbdmZLxliGK5XOInrrtQPg0CICEW4K0j0wYS+kca2yX5y3qbLWE0EttN
Rocmtyw5WnLB
-----END PRIVATE KEY-----`;
var rawSeamlessData = "mobileNumber=6281234000001&paymentType=gopay";
var seamlessData = encodeURIComponent(rawSeamlessData);

/*-- SIGNATURE GENERATION --*/
var myPrivateKey = crypto.createPrivateKey({ key: privateKeyString, format: 'pem' });
var unencodedSeamlessSign = crypto.createSign('RSA-SHA256')
  .update(seamlessData)
  .sign(myPrivateKey, 'base64');
var seamlessSign = encodeURIComponent(unencodedSeamlessSign);

/*-- OUTPUT --*/
console.log("seamlessSign:");
console.log(seamlessSign);
// Next, this seamlessSign is the value to be used as `seamlessSign` URL Query Parameter.

/* Sample seamlessSign: 
Hum8Z%2Byr%2FOTTqGxo4halA8I4LcGySMsANl24opI52Tdp2SBPiYDrO8KOd%2Bz5M5O16Cth21fEvBoph9hilzGtaw%3D%3D
*/

/* 
// -- Alternative Signature Generation using another module --
// `npm i jsrsasign`
// to import https://www.npmjs.com/package/jsrsasign
var jsrsasign = require("jsrsasign");
var sigObject = new jsrsasign.KJUR.crypto.Signature({ alg: "SHA256withRSA" });
sigObject.init(privateKeyString);
sigObject.updateString(seamlessData);
var signatureHex = sigObject.sign();
// the result was in Hex format, convert it into Base64 String format
var unencodedSeamlessSign = Buffer.from(signatureHex, "hex").toString("base64");
var seamlessSign = encodeURIComponent(unencodedSeamlessSign);
*/