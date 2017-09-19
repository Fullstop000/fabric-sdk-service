package main

import (
	"google.golang.org/grpc"
	"log"
	"fmt"
	"context"
	"encoding/json"
	"math/rand"
	"time"
)

const (
	SERVER_ADD = "localhost"
	SERVER_PORT = 8980
)

type CoinTransaction struct {
	// transaction sender
	Sender string `json:"sender"`
	// transaction receiver
	Receiver string `json:"receiver"`
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

var client CoinTransactionClient
var conn *grpc.ClientConn
func init() {
	var opts []grpc.DialOption
	//todo add TLS support
	opts = append(opts,grpc.WithInsecure())
	var err error
	conn , err = grpc.Dial(fmt.Sprintf("%s:%d",SERVER_ADD,SERVER_PORT),opts...)
	if err != nil {
		log.Fatalf("Error dialing %s:%d : %e",SERVER_ADD,SERVER_PORT,err)
	}
	client = NewCoinTransactionClient(conn)
}
func createTransaction() {
	defer conn.Close()
	// test create transaction
	createStr, err := json.Marshal(&CoinTransaction{
		Sender:"testSender",
		Receiver:"testReceiver",
		SenderBalance:rand.Float64(),
		ReceiverBalance:rand.Float64(),
		Amount:rand.Float64(),
		Type:"test",
		Timestamp:time.Now().Unix(),
	})
	if err != nil {
		fmt.Errorf("error marshaling CoinTransaction")
	}
	response,err := client.CreateTransaction(context.Background(),&WriteRequest{string(createStr[:]),"1"})
	if err != nil {
		fmt.Errorf("error creating transaction : %s",err.Error())
	}
	fmt.Printf("Response : %v",response)
}

func queryTransaction()  {
	defer conn.Close()
	// test query transaction
	queryStr := "{ \"selector\": {\"type\":{\"$gt\":null}}}"
	response, err :=client.QueryTransaction(context.Background(),&QueryRequest{queryStr,"1"})
	if err != nil {
		fmt.Errorf("error QueryTransaction : %s" , err.Error())
	}
	fmt.Printf("%v",response)
	var transactions []CoinTransaction
	err = json.Unmarshal([]byte(response.Payload),&transactions)
	if err != nil {
		fmt.Errorf("error unmarshaling response.payload : %s , err : %s",response.Payload,err.Error())
	}
	fmt.Printf("Response : %v",transactions)
}
func main() {

}
