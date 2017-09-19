// Code generated by protoc-gen-go. DO NOT EDIT.
// source: coin_transaction.proto

/*
Package grpclient is a generated protocol buffer package.

It is generated from these files:
	coin_transaction.proto

It has these top-level messages:
	QueryRequest
	WriteRequest
	CoinSDKServiceResponse
*/
package grpclient

import proto "github.com/golang/protobuf/proto"
import fmt "fmt"
import math "math"

import (
	context "golang.org/x/net/context"
	grpc "google.golang.org/grpc"
)

// Reference imports to suppress errors if they are not otherwise used.
var _ = proto.Marshal
var _ = fmt.Errorf
var _ = math.Inf

// This is a compile-time assertion to ensure that this generated file
// is compatible with the proto package it is being compiled against.
// A compilation error at this line likely means your copy of the
// proto package needs to be updated.
const _ = proto.ProtoPackageIsVersion2 // please upgrade the proto package

type CoinSDKServiceResponse_Status int32

const (
	CoinSDKServiceResponse_success CoinSDKServiceResponse_Status = 0
	CoinSDKServiceResponse_other   CoinSDKServiceResponse_Status = 5
	CoinSDKServiceResponse_error   CoinSDKServiceResponse_Status = 9
)

var CoinSDKServiceResponse_Status_name = map[int32]string{
	0: "success",
	5: "other",
	9: "error",
}
var CoinSDKServiceResponse_Status_value = map[string]int32{
	"success": 0,
	"other":   5,
	"error":   9,
}

func (x CoinSDKServiceResponse_Status) String() string {
	return proto.EnumName(CoinSDKServiceResponse_Status_name, int32(x))
}
func (CoinSDKServiceResponse_Status) EnumDescriptor() ([]byte, []int) {
	return fileDescriptor0, []int{2, 0}
}

// The query request
type QueryRequest struct {
	QueryString string `protobuf:"bytes,1,opt,name=query_string,json=queryString" json:"query_string,omitempty"`
	OrgID       string `protobuf:"bytes,2,opt,name=orgID" json:"orgID,omitempty"`
}

func (m *QueryRequest) Reset()                    { *m = QueryRequest{} }
func (m *QueryRequest) String() string            { return proto.CompactTextString(m) }
func (*QueryRequest) ProtoMessage()               {}
func (*QueryRequest) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{0} }

func (m *QueryRequest) GetQueryString() string {
	if m != nil {
		return m.QueryString
	}
	return ""
}

func (m *QueryRequest) GetOrgID() string {
	if m != nil {
		return m.OrgID
	}
	return ""
}

// The write request
type WriteRequest struct {
	// use json to simplified the RPC service level
	JsonStr string `protobuf:"bytes,1,opt,name=jsonStr" json:"jsonStr,omitempty"`
	//    repeated RPCCoinTransaction payload = 1;
	OrgID string `protobuf:"bytes,2,opt,name=orgID" json:"orgID,omitempty"`
}

func (m *WriteRequest) Reset()                    { *m = WriteRequest{} }
func (m *WriteRequest) String() string            { return proto.CompactTextString(m) }
func (*WriteRequest) ProtoMessage()               {}
func (*WriteRequest) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{1} }

func (m *WriteRequest) GetJsonStr() string {
	if m != nil {
		return m.JsonStr
	}
	return ""
}

func (m *WriteRequest) GetOrgID() string {
	if m != nil {
		return m.OrgID
	}
	return ""
}

// The service response
type CoinSDKServiceResponse struct {
	Message string                        `protobuf:"bytes,1,opt,name=message" json:"message,omitempty"`
	Status  CoinSDKServiceResponse_Status `protobuf:"varint,2,opt,name=status,enum=grpclient.CoinSDKServiceResponse_Status" json:"status,omitempty"`
	// if QueryRequest the payload is a json str encoded by go json lib
	Payload string `protobuf:"bytes,3,opt,name=payload" json:"payload,omitempty"`
}

func (m *CoinSDKServiceResponse) Reset()                    { *m = CoinSDKServiceResponse{} }
func (m *CoinSDKServiceResponse) String() string            { return proto.CompactTextString(m) }
func (*CoinSDKServiceResponse) ProtoMessage()               {}
func (*CoinSDKServiceResponse) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{2} }

func (m *CoinSDKServiceResponse) GetMessage() string {
	if m != nil {
		return m.Message
	}
	return ""
}

func (m *CoinSDKServiceResponse) GetStatus() CoinSDKServiceResponse_Status {
	if m != nil {
		return m.Status
	}
	return CoinSDKServiceResponse_success
}

func (m *CoinSDKServiceResponse) GetPayload() string {
	if m != nil {
		return m.Payload
	}
	return ""
}

func init() {
	proto.RegisterType((*QueryRequest)(nil), "grpclient.QueryRequest")
	proto.RegisterType((*WriteRequest)(nil), "grpclient.WriteRequest")
	proto.RegisterType((*CoinSDKServiceResponse)(nil), "grpclient.CoinSDKServiceResponse")
	proto.RegisterEnum("grpclient.CoinSDKServiceResponse_Status", CoinSDKServiceResponse_Status_name, CoinSDKServiceResponse_Status_value)
}

// Reference imports to suppress errors if they are not otherwise used.
var _ context.Context
var _ grpc.ClientConn

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
const _ = grpc.SupportPackageIsVersion4

// Client API for CoinTransaction service

type CoinTransactionClient interface {
	// Create a coin transaction record in fabric network
	CreateTransaction(ctx context.Context, in *WriteRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error)
	// Query the transaction info from fabric
	QueryTransaction(ctx context.Context, in *QueryRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error)
	// Create a account for certain user
	CreateAccount(ctx context.Context, in *WriteRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error)
	// Query a account for certain user
	QueryAccount(ctx context.Context, in *QueryRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error)
}

type coinTransactionClient struct {
	cc *grpc.ClientConn
}

func NewCoinTransactionClient(cc *grpc.ClientConn) CoinTransactionClient {
	return &coinTransactionClient{cc}
}

func (c *coinTransactionClient) CreateTransaction(ctx context.Context, in *WriteRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error) {
	out := new(CoinSDKServiceResponse)
	err := grpc.Invoke(ctx, "/grpclient.CoinTransaction/CreateTransaction", in, out, c.cc, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *coinTransactionClient) QueryTransaction(ctx context.Context, in *QueryRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error) {
	out := new(CoinSDKServiceResponse)
	err := grpc.Invoke(ctx, "/grpclient.CoinTransaction/QueryTransaction", in, out, c.cc, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *coinTransactionClient) CreateAccount(ctx context.Context, in *WriteRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error) {
	out := new(CoinSDKServiceResponse)
	err := grpc.Invoke(ctx, "/grpclient.CoinTransaction/CreateAccount", in, out, c.cc, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *coinTransactionClient) QueryAccount(ctx context.Context, in *QueryRequest, opts ...grpc.CallOption) (*CoinSDKServiceResponse, error) {
	out := new(CoinSDKServiceResponse)
	err := grpc.Invoke(ctx, "/grpclient.CoinTransaction/QueryAccount", in, out, c.cc, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// Server API for CoinTransaction service

type CoinTransactionServer interface {
	// Create a coin transaction record in fabric network
	CreateTransaction(context.Context, *WriteRequest) (*CoinSDKServiceResponse, error)
	// Query the transaction info from fabric
	QueryTransaction(context.Context, *QueryRequest) (*CoinSDKServiceResponse, error)
	// Create a account for certain user
	CreateAccount(context.Context, *WriteRequest) (*CoinSDKServiceResponse, error)
	// Query a account for certain user
	QueryAccount(context.Context, *QueryRequest) (*CoinSDKServiceResponse, error)
}

func RegisterCoinTransactionServer(s *grpc.Server, srv CoinTransactionServer) {
	s.RegisterService(&_CoinTransaction_serviceDesc, srv)
}

func _CoinTransaction_CreateTransaction_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(WriteRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(CoinTransactionServer).CreateTransaction(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/grpclient.CoinTransaction/CreateTransaction",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(CoinTransactionServer).CreateTransaction(ctx, req.(*WriteRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _CoinTransaction_QueryTransaction_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(QueryRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(CoinTransactionServer).QueryTransaction(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/grpclient.CoinTransaction/QueryTransaction",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(CoinTransactionServer).QueryTransaction(ctx, req.(*QueryRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _CoinTransaction_CreateAccount_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(WriteRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(CoinTransactionServer).CreateAccount(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/grpclient.CoinTransaction/CreateAccount",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(CoinTransactionServer).CreateAccount(ctx, req.(*WriteRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _CoinTransaction_QueryAccount_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(QueryRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(CoinTransactionServer).QueryAccount(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/grpclient.CoinTransaction/QueryAccount",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(CoinTransactionServer).QueryAccount(ctx, req.(*QueryRequest))
	}
	return interceptor(ctx, in, info, handler)
}

var _CoinTransaction_serviceDesc = grpc.ServiceDesc{
	ServiceName: "grpclient.CoinTransaction",
	HandlerType: (*CoinTransactionServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "CreateTransaction",
			Handler:    _CoinTransaction_CreateTransaction_Handler,
		},
		{
			MethodName: "QueryTransaction",
			Handler:    _CoinTransaction_QueryTransaction_Handler,
		},
		{
			MethodName: "CreateAccount",
			Handler:    _CoinTransaction_CreateAccount_Handler,
		},
		{
			MethodName: "QueryAccount",
			Handler:    _CoinTransaction_QueryAccount_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "coin_transaction.proto",
}

func init() { proto.RegisterFile("coin_transaction.proto", fileDescriptor0) }

var fileDescriptor0 = []byte{
	// 368 bytes of a gzipped FileDescriptorProto
	0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0xff, 0xa4, 0x93, 0xd1, 0x6a, 0xe2, 0x40,
	0x18, 0x85, 0x8d, 0xa2, 0x92, 0x5f, 0x77, 0x37, 0x3b, 0x88, 0x1b, 0xf6, 0x6a, 0xcd, 0x95, 0xb0,
	0x10, 0xa8, 0xbd, 0x2f, 0xad, 0x0a, 0x52, 0xda, 0x82, 0x26, 0x42, 0x2f, 0x65, 0x3a, 0xfd, 0x89,
	0x29, 0x3a, 0x13, 0x67, 0x26, 0x05, 0x5f, 0xa7, 0xaf, 0xd1, 0xc7, 0xe8, 0x0b, 0x95, 0x49, 0x62,
	0x1b, 0x44, 0x69, 0x4b, 0xef, 0x72, 0x4e, 0x66, 0xbe, 0x9c, 0xcc, 0x7f, 0x06, 0xba, 0x4c, 0xc4,
	0x7c, 0xa1, 0x25, 0xe5, 0x8a, 0x32, 0x1d, 0x0b, 0xee, 0x27, 0x52, 0x68, 0x41, 0xec, 0x48, 0x26,
	0x6c, 0x15, 0x23, 0xd7, 0xde, 0x04, 0xda, 0xb3, 0x14, 0xe5, 0x36, 0xc0, 0x4d, 0x8a, 0x4a, 0x93,
	0x1e, 0xb4, 0x37, 0x46, 0x2f, 0x94, 0x96, 0x31, 0x8f, 0x5c, 0xeb, 0x9f, 0xd5, 0xb7, 0x83, 0x56,
	0xe6, 0x85, 0x99, 0x45, 0x3a, 0x50, 0x17, 0x32, 0xba, 0x1c, 0xbb, 0xd5, 0xec, 0x5d, 0x2e, 0xbc,
	0x33, 0x68, 0xdf, 0xca, 0x58, 0xe3, 0x0e, 0xe4, 0x42, 0xf3, 0x41, 0x09, 0x1e, 0x6a, 0x59, 0x30,
	0x76, 0xf2, 0xc8, 0xfe, 0x67, 0x0b, 0xba, 0x23, 0x11, 0xf3, 0x70, 0x7c, 0x15, 0xa2, 0x7c, 0x8c,
	0x19, 0x06, 0xa8, 0x12, 0xc1, 0x15, 0x1a, 0xd4, 0x1a, 0x95, 0xa2, 0x11, 0xee, 0x50, 0x85, 0x24,
	0xe7, 0xd0, 0x50, 0x9a, 0xea, 0x54, 0x65, 0xac, 0x9f, 0x83, 0xbe, 0xff, 0xf6, 0x67, 0xfe, 0x61,
	0x98, 0x1f, 0x66, 0xeb, 0x83, 0x62, 0x9f, 0x61, 0x27, 0x74, 0xbb, 0x12, 0xf4, 0xde, 0xad, 0xe5,
	0xec, 0x42, 0x7a, 0xff, 0xa1, 0x91, 0xaf, 0x25, 0x2d, 0x68, 0xaa, 0x94, 0x31, 0x54, 0xca, 0xa9,
	0x10, 0x1b, 0xea, 0x42, 0x2f, 0x51, 0x3a, 0x75, 0xf3, 0x88, 0x52, 0x0a, 0xe9, 0xd8, 0x83, 0x97,
	0x2a, 0xfc, 0x32, 0x1f, 0x9c, 0xbf, 0x9f, 0x35, 0x99, 0xc1, 0xef, 0x91, 0x44, 0xaa, 0xb1, 0x6c,
	0xfe, 0x29, 0x25, 0x2c, 0x9f, 0xd7, 0xdf, 0xde, 0x87, 0xd1, 0xbd, 0x0a, 0x99, 0x82, 0x93, 0x4d,
	0xeb, 0x18, 0xb1, 0x3c, 0xca, 0xcf, 0x11, 0x6f, 0xe0, 0x47, 0x1e, 0xf2, 0x82, 0x31, 0x91, 0x72,
	0xfd, 0xcd, 0x80, 0xd7, 0x45, 0x9d, 0x0e, 0xd1, 0xbe, 0x1c, 0x6e, 0x38, 0x83, 0x13, 0x26, 0xd6,
	0xfe, 0x36, 0xe5, 0xc9, 0x92, 0x72, 0xed, 0x9b, 0x3a, 0x97, 0xdb, 0x6c, 0x10, 0xfb, 0xe6, 0xb0,
	0xb3, 0x37, 0x87, 0xa9, 0xa9, 0xfc, 0xd4, 0x7a, 0xaa, 0xd6, 0x82, 0xf9, 0xe4, 0xae, 0x91, 0xdd,
	0x80, 0xd3, 0xd7, 0x00, 0x00, 0x00, 0xff, 0xff, 0x7d, 0x33, 0x32, 0xea, 0x1b, 0x03, 0x00, 0x00,
}
