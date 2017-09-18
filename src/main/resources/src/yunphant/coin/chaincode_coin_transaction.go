package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"strconv"
	"encoding/json"
	"bytes"
)

type CoinTransaction struct {
	// transaction sender
	Sender int `json:"sender"`
	// transaction receiver
	Receiver int `json:"receiver"`
	// sender balance
	SenderBalance float64 `json:"senderBalance"`
	// receiver balance
	ReceiverBalance float64 `json:"receiverBalance"`
	// transaction amount
	Amount float64 `json:"amount"`
	// transaction type
	Type string `json:"type"`
	// transaction time
	Timestamp int64 `json:"timestamp"`

	//todo add goods info
}

func (coinTransaction *CoinTransaction) Init(stub shim.ChaincodeStubInterface) peer.Response {
	args := stub.GetArgs()
	fmt.Printf("Starting init . Args : %v", args)
	return shim.Success(nil)
}

func (coinTransaction *CoinTransaction) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	switch function {
	//case "query":
		//return coinTransaction.query(stub, args)
	case "addCoinTransaction":
		var coinTran CoinTransaction
		err := json.Unmarshal([]byte(args[0]), &coinTran)
		if err != nil {
			shim.Error(fmt.Sprintf("Erroring unmarshal string to CoinTransaction : %s ,err : %e",args[0],err))
		}
		return coinTran.addCoinTransaction(stub)
	case "query":
		return  coinTransaction.query(stub,args[0])
	default:
		break
	}
	return shim.Success([]byte(fmt.Sprintf("Invalid invoke function : %s",function)))
}
func (coinTransaction *CoinTransaction) query(stub shim.ChaincodeStubInterface, queryStr string ) peer.Response {
	res, err := stub.GetQueryResult(queryStr)
	if err != nil {
		shim.Error(fmt.Sprintf("Erroring query couchDB by query string : %s , err : %e",queryStr , err))
	}
	bArrayMemberAlreadyWritten := false
	var buffer bytes.Buffer
	buffer.WriteString("[")
	for res.HasNext() {
		queryResponse, err := res.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	fmt.Printf("- queryResult:\n%s\n", buffer.String())
	return shim.Success(buffer.Bytes())
}

func (coinTransaction *CoinTransaction) addCoinTransaction(stub shim.ChaincodeStubInterface) peer.Response {
	key, err := stub.CreateCompositeKey(coinTransaction.Type,
		[]string{strconv.Itoa(coinTransaction.Sender), strconv.Itoa(coinTransaction.Receiver), strconv.FormatInt(coinTransaction.Timestamp, 10)})
	if err != nil {
		shim.Error(fmt.Sprintf("Error generate composite key from CoinTransaction : %v , err : %e", coinTransaction, err))
	}
	jsonByte, err := json.Marshal(coinTransaction)
	if err != nil {
		shim.Error(fmt.Sprintf("Error convert 'CoinTransaction' to json :  %v , err : %e", coinTransaction, err))
	}
	stub.PutState(key, jsonByte)
	return shim.Success([]byte("Success adding coin transaction"))
}


func main() {
	err := shim.Start(new(CoinTransaction))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
