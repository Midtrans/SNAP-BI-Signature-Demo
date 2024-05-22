package main

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"net/url"
)

// main function
func main() {
	/*-- INPUT --*/
	// Input or import your Private Key, store into a string variable
	privateKeyString := `-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAlas/ul92ZfLV8cxf
k7kQU0V41Sp9RlDylXTTmUv92scPyVsTRQSN8RpSe/DKvZfdm+mWd2wYhPz14cg2
IhQNJwIDAQABAkAB80dTUGckkPOEwRsFu8WgsCkQ7grP3cIrfGg9eSYHscdCiNsD
ZbChg7FGbyUwMhfTRgfsRlE5qBGR9X6p9yKBAiEAyIML9J/JYsoTBeaxbJYg13Kq
/FKrBkAzG34tcdYylKECIQC/Fk/a0pWwfKoik+wlZW6f1MDw++n9Sv7X+5D80HAE
xwIhALfQ6zTnBwe5mJbgVebl+lWImZeXcZHZaQDbO24Qn24BAiEAs7UGEKMvR7VW
RAKdeWX1LbdmZLxliGK5XOInrrtQPg0CICEW4K0j0wYS+kca2yX5y3qbLWE0EttN
Rocmtyw5WnLB
-----END PRIVATE KEY-----`
	rawSeamlessData := "mobileNumber=6281234000001&paymentType=gopay"
	seamlessData := url.QueryEscape(rawSeamlessData)

	/*-- SIGNATURE GENERATION --*/
	block, _ := pem.Decode([]byte(privateKeyString))
	if block == nil || block.Type != "PRIVATE KEY" {
		fmt.Println("Failed to decode PEM block containing private key")
		return
	}

	privateKey, err := x509.ParsePKCS8PrivateKey(block.Bytes)
	if err != nil {
		fmt.Println("Failed to parse private key:", err)
		return
	}

	rsaPrivateKey, ok := privateKey.(*rsa.PrivateKey)
	if !ok {
		fmt.Println("Not an RSA private key")
		return
	}

	hashed := sha256.Sum256([]byte(seamlessData))
	signature, err := rsa.SignPKCS1v15(rand.Reader, rsaPrivateKey, crypto.SHA256, hashed[:])
	if err != nil {
		fmt.Println("Error signing data:", err)
		return
	}

	unencodedSeamlessSign := base64.StdEncoding.EncodeToString(signature)
	seamlessSign := url.QueryEscape(unencodedSeamlessSign)

	/*-- OUTPUT --*/
	fmt.Println("seamlessSign:")
	fmt.Println(seamlessSign)
	// Next, this seamlessSign is the value to be used as `seamlessSign` URL Query Parameter.

	/* Sample seamlessSign: 
Hum8Z%2Byr%2FOTTqGxo4halA8I4LcGySMsANl24opI52Tdp2SBPiYDrO8KOd%2Bz5M5O16Cth21fEvBoph9hilzGtaw%3D%3D
	*/
}
