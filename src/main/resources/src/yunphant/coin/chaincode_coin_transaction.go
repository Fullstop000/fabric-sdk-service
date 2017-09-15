package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"fmt"
)

type CoinTransaction struct {
	// transaction sender
	Sender string		`json:"sender"`
	// transaction receiver
	Receiver string		`json:"receiver"`
	// sender balance
	SenderBalance uint64 `json:"sender_balance"`
	// receiver balance
	ReceiverBalance uint64 `json:"receiver_balance"`
	// transaction amount
	Amount int64 `json:"amount"`
	// transaction type
	Type string `json:"type"`
	// transaction time
	Timestamp int64 `json:"timestamp"`

	//todo add goods info
}

func (coinTransaction *CoinTransaction) Init(stub shim.ChaincodeStubInterface) peer.Response {
	fmt.Println("Start init ")
	return shim.Success(nil)
}
