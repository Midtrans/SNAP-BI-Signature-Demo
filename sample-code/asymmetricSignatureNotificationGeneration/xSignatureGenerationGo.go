package main

import (
	"bytes"
	"crypto"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"io"
	"strings"
	"time"
)

func main() {
	/*-- INPUT --*/
	// Input or import the HTTP Notification/request sender's Private Key, store into a string variable
	// NOTE: make sure there is a new line after 1st BEGIN PRIVATE KEY line, and a new line before last END PRIVATE KEY line to avoid issue
	privateKeyPEM :=
		`-----BEGIN PRIVATE KEY-----
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
-----END PRIVATE KEY-----`
	httpMethod := strings.ToUpper("POST") // ensure all UPPERCASE
	endpointUrl := "/v1.0/debit/notify"
	// ensure requestBody is a valid JSON string format, dont worry about whitespaces, it will be minified later
	requestBody := `{
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
}`
	timestampString := time.Now().UTC().Format(time.RFC3339Nano)
	// sample hardcoded timestamp value
	// timestampString = "2024-03-05T10:37:16.196Z"

	/*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
	minifiedRequestBody := minifyRequestBody(requestBody)
	lowerHexSha256MinifiedRequestBody := strings.ToLower(sha256Hex(minifiedRequestBody))

	combinedStringToSign :=
		httpMethod + ":" +
			endpointUrl + ":" +
			lowerHexSha256MinifiedRequestBody + ":" +
			timestampString

	/*-- SIGNATURE GENERATION --*/
	// Parse the private key
	r := strings.NewReader(privateKeyPEM)
	pemBytes, err := io.ReadAll(r)
	if err != nil {
		fmt.Println("Error reading private key:", err)
		return
	}
	privateKeyDER, _ := pem.Decode(pemBytes)
	if privateKeyDER == nil {
		fmt.Println("Error decoding private key")
		return
	}
	privateKey, err := x509.ParsePKCS8PrivateKey(privateKeyDER.Bytes)
	if err != nil {
		fmt.Println("Error parsing private key:", err)
		return
	}
	// Hash the combined string
	hasher := sha256.New()
	hasher.Write([]byte(combinedStringToSign))
	hashedDataToSign := hasher.Sum(nil)
	// Sign the hashed combined string
	signature, err := rsa.SignPKCS1v15(nil, privateKey.(*rsa.PrivateKey), crypto.SHA256, hashedDataToSign)
	if err != nil {
		fmt.Println("Error generating signature:", err)
		return
	}
	// To convert to readable String, base64 encode the bytes
	xSignatureString := base64.StdEncoding.EncodeToString(signature)

	/*-- OUTPUT --*/
	fmt.Println("xSignatureString:")
	fmt.Println(xSignatureString)
	// Next, this xSignatureString is the value sent as `X-SIGNATURE` HTTP header on HTTP Notification (Webhook).

	/* Sample xSignatureString:	eUGsomFLNsjWzehgGsa317MqcVOgscay/Hml8yNmOsFhmoBM7pso+UPDgSe34anXvecSQkf5NOr0fbr8k9qdyyoWiYX7DTq/Wnz8XC0cRCCbNZfyLIyF33HO9cnvxOcRQq9DuWlQtld+uEluuivVdN2TiaWLpUTmfB/H7noZnXWWDj6FYa4n5kSo4o3aTeFYAF9EmLp0Tcsl8CiHSQCrQnIbB8M2+puLwyJ1l2OocvZ5qgjf5f2DqlCbemsSMzYzjN+YMrb0kiDx0TiW5UTk+WZass43xIfb3vdrlX+U2JSk5YsJ8lFpp9F+7FId4cyncElNFEbt3zWuP4iDzXteCg==
	 */
}

func minifyRequestBody(jsonString string) string {
	minifiedRequestBody := "" // default to empty string, e.g. when there is no requestBody
	if len(jsonString) > 0 {
		// minify using https://pkg.go.dev/encoding/json#Compact
		dst := &bytes.Buffer{}
		if err := json.Compact(dst, []byte(jsonString)); err != nil {
			panic(err)
		}
		minifiedRequestBody = dst.String()
	}
	return minifiedRequestBody
}

func sha256Hex(input string) string {
	hash := sha256.Sum256([]byte(input))
	return hex.EncodeToString(hash[:])
}
