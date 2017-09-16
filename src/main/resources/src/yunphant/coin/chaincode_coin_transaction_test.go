package main

import (
	"testing"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"math/rand"
	"time"
	"encoding/json"
)
const letterBytes = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"

func TestCoinTransaction_Init(t *testing.T) {
	stub:= shim.NewMockStub("coinTransactionCC",new(CoinTransaction))
	res := stub.MockInit("1",[][]byte{[]byte("init"),[]byte("test"),[]byte("1")})
	if res.Status != shim.OK {
		t.Fatalf("Init failed")
	}
}
func TestCoinTransaction_Invoke_addCoinTransaction(t *testing.T) {
	stub:= shim.NewMockStub("coinTransactionCC",new(CoinTransaction))
	jsonCT,err := json.Marshal(generateRandomCoinTransaction())
	if err !=nil {
		t.Fatalf("Json marshal failed : %e",err)
	}
	res := stub.MockInvoke("1",[][]byte{[]byte("addCoinTransaction"),jsonCT})
	if res.Status != shim.OK {
		t.Fatal("Invoke addCoinTransaction "+res.GetMessage())
	}
}

func generateRandomCoinTransaction() *CoinTransaction {
	return &CoinTransaction{
		Sender:genRandomStr(10),
		Receiver:genRandomStr(10),
		SenderBalance:rand.Float64(),
		ReceiverBalance:rand.Float64(),
		Amount:rand.Float64(),
		Timestamp:time.Now().UnixNano(),
		Type:"transfer",
	}
}

func genRandomStr(n int) string {
	b := make([]byte, n)
	for i := range b {
		b[i] = letterBytes[rand.Int63() % int64(len(letterBytes))]
	}
	return string(b)
}