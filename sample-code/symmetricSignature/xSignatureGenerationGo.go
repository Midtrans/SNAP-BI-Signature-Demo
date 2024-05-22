package main

import (
	"bytes"
	"crypto/hmac"
	"crypto/sha256"
	"crypto/sha512"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"strings"
	"time"
)

func main() {
	/*-- INPUT --*/
	clientSecret := "myclientsecret"
	httpMethod := strings.ToUpper( "POST" ) // ensure all UPPERCASE
	endpointUrl := "/v1.0/qr/qr-mpm-generate"
	accessToken := "myaccesstoken"
	// ensure requestBody is a valid JSON string format, dont worry about whitespaces, it will be minified later. Fill it with "" empty string for GET request.
	requestBody := `{
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
	}`
	timestampString := time.Now().UTC().Format(time.RFC3339Nano)
	// sample hardcoded timestamp value
	// timestampString = "2024-03-05T10:37:16.196Z"

	/*-- PERFORM: Lowercase(HexEncode(SHA-256(minify(RequestBody)))) --*/
	minifiedRequestBody := minifyRequestBody(requestBody)
	lowerHexSha256MinifiedRequestBody := strings.ToLower( sha256Hex(minifiedRequestBody) )

	combinedStringToSign :=
		httpMethod + ":" +
		endpointUrl + ":" +
		accessToken + ":" +
		lowerHexSha256MinifiedRequestBody + ":" +
		timestampString

	/*-- SIGNATURE GENERATION --*/
	xSignatureString := hmacSha512(clientSecret, combinedStringToSign)

	/*-- OUTPUT --*/
	fmt.Println("xSignatureString:")
	fmt.Println(xSignatureString)
	// Next, this xSignatureString is the value to be used as `X-SIGNATURE` HTTP headers.

	/* Sample xSignatureString:
	wmzT7yjg1SxxCsxNsFgCR9SYXiZGGbceF6fV8xe97sAMHxZ+7OEBMIBmvb6Sj6GcksGp3iv77DiomgSbcST3XQ==
	*/
}

func minifyRequestBody(jsonString string) string {
	minifiedRequestBody := ""  // default to empty string, e.g. when there is no requestBody
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

func hmacSha512(key string, input string) string {
	mac := hmac.New(sha512.New, []byte(key))
	mac.Write([]byte(input))
	return base64.StdEncoding.EncodeToString(mac.Sum(nil))
}
