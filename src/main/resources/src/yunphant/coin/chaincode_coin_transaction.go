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
	// transaction amount
	Amount int64 `json:"amount"`
	// transaction type
	Type string `json:"type"`
	// transaction time
	Timestamp int64 `json:"timestamp"`

	//todo add goods info
}

type UserBalance struct {
	// user id
	UserID int `json:"userID"`

	// user balance
	Balance int64 `json:"balance"`
}

type YunphantCoinCC struct {
// 	just for chaincode
}
func (c *YunphantCoinCC) Init(stub shim.ChaincodeStubInterface) peer.Response {
	// Init a root user : userID 1
	err := stub.PutState("1",[]byte("0"))
	if err != nil {
		shim.Error(fmt.Sprintf("Error creating root user . err : %s",err.Error()))
	}
	return shim.Success(nil)
}

func (c *YunphantCoinCC) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	switch function {
	case "createAccount":
		var userBalance UserBalance
		err := json.Unmarshal([]byte(args[0]), &userBalance)
		if err != nil {
			return shim.Error(fmt.Sprintf("Error unmarshaling string to UserBalance : %s ,err : %s",args[0],err.Error()))
		}
		return c.createAccount(stub, userBalance)
	case "addCoinTransaction":
		var coinTran CoinTransaction
		err := json.Unmarshal([]byte(args[0]), &coinTran)
		if err != nil {
			return shim.Error(fmt.Sprintf("Error unmarshaling string to CoinTransaction : %s ,err : %s",args[0],err.Error()))
		}
		return c.addCoinTransaction(stub,&coinTran)
	case "queryTransactionRecord":
		return  c.queryTransactionRecord(stub,args[0])
	case "queryBalance":
		return c.queryBalance(stub,args[0])
	default:
		break
	}
	return shim.Error(fmt.Sprintf("Invalid invoke function : %s",function))
}

func (c *YunphantCoinCC) queryBalance(stub shim.ChaincodeStubInterface, userID string) peer.Response {
	queryRes, err :=stub.GetState(userID)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error getting balance by userID: %s",userID))
	}
	if queryRes == nil {
		return shim.Error(fmt.Sprintf("No such user : %s",userID))
	}
	userId , _ := strconv.Atoi(userID)
	balance , _ := strconv.ParseInt(string(queryRes[:]),10,64)
	userBalance :=&UserBalance{
		UserID:userId,
		Balance:balance,
	}
	res,err  := json.Marshal(userBalance)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error convert UserBalance to json :%v",userBalance))
	}
	return shim.Success(res)
}
func (c *YunphantCoinCC) queryTransactionRecord(stub shim.ChaincodeStubInterface, queryStr string ) peer.Response {
	res, err := stub.GetQueryResult(queryStr)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error querying couchDB by query string : %s , err : %s",queryStr , err.Error()))
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

// createAccount
// this func will create a new pair <userID , 0> and put it into couchDB
func (c *YunphantCoinCC) createAccount(stub shim.ChaincodeStubInterface , balance UserBalance) peer.Response {
	userId := strconv.Itoa(balance.UserID)
	res , err := stub.GetState(userId)
	if err != nil  {
		return shim.Error(fmt.Sprintf("Error checking user's balance by userID : %s , err : %s",userId,err.Error()))
	}
	if res != nil {
		return shim.Error(fmt.Sprint("The user has already bean created"))
	}
	data,err := json.Marshal(balance.Balance)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error marshaling UserBalance to string , err : %s",err.Error()))
	}
	err = stub.PutState(userId,data)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error putting new userId into DB : %s",err.Error()))
	}
	return shim.Success([]byte("Success create Account for user : " + userId))
}

// addCoinTransaction
// this func will update both receiver's and sender's balance and create a transaction record
func (c *YunphantCoinCC) addCoinTransaction(stub shim.ChaincodeStubInterface,coinTransaction *CoinTransaction) peer.Response {
	senderId := strconv.Itoa(coinTransaction.Sender)
	receiverId := strconv.Itoa(coinTransaction.Receiver)
	amount := coinTransaction.Amount

	// update the sender balance and calculate
	if coinTransaction.Type != "issue" {
		// The type `issue` is mapping to the grant operation of admin
		senderBalanceBytes,err := stub.GetState(senderId)
		if err !=nil {
			return shim.Error(fmt.Sprintf("Error getting sender balance by id : %s , err : %s",senderId,err.Error()))
		}
		if senderBalanceBytes == nil {
			return shim.Error(fmt.Sprintf("No account found for sender %s",senderId))
		}
		senderBalance, err := strconv.ParseInt(string(senderBalanceBytes[:]),10,64)
		if err != nil {
			return shim.Error(fmt.Sprintf("Error parsing sender balance to int64 number : %s",err.Error()))
		}

		if senderBalance=senderBalance - amount ; senderBalance < 0  {
			return shim.Error(fmt.Sprint("Do not have enough balance !"))
		}
		err = stub.PutState(senderId, []byte(strconv.FormatInt(senderBalance,10)))
		if err != nil {
			return shim.Error(fmt.Sprintf("Error update sender balance : %s",err.Error()))
		}
	}


	// update the receiver balance
	receiverBalanceBytes, err := stub.GetState(receiverId)
	if err!= nil {
		return shim.Error(fmt.Sprintf("Error getting receiver balance by id : %s , err : %s",senderId,err.Error()))
	}
	if receiverBalanceBytes == nil {
		return shim.Error(fmt.Sprintf("No account found for receiver %s",receiverId))
	}
	receiverBalance, err := strconv.ParseInt(string(receiverBalanceBytes[:]),10,64)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error parsing receiver balance to int64 number : %s",err.Error()))
	}
	receiverBalance += amount
	err = stub.PutState(receiverId,[]byte(strconv.FormatInt(receiverBalance,10)))
	if err != nil {
		return shim.Error(fmt.Sprintf("Error update receiver balance : %s",err.Error()))
	}


	// create composite key for transaction record
	key, err := stub.CreateCompositeKey(coinTransaction.Type,
		[]string{strconv.Itoa(coinTransaction.Sender), strconv.Itoa(coinTransaction.Receiver), strconv.FormatInt(coinTransaction.Timestamp, 10)})
	if err != nil {
		return shim.Error(fmt.Sprintf("Error generating composite key from CoinTransaction : %v , err : %s", c, err.Error()))
	}
	jsonByte, err := json.Marshal(coinTransaction)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error converting 'CoinTransaction' to json :  %v , err : %s", c, err.Error()))
	}
	err = stub.PutState(key, jsonByte)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error creating transaction record : %s",err.Error()))
	}
	return shim.Success([]byte("Success adding coin transaction"))
}


func main() {
	err := shim.Start(new(YunphantCoinCC))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err.Error())
	}
}
