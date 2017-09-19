package com.yunphant.coin;

import com.yunphant.coin.common.FabricConfigHelper;
import com.yunphant.coin.common.configbeans.FabricConfig;
import com.yunphant.coin.sample.SampleOrg;
import com.yunphant.coin.services.CoinTransactionService;
import com.yunphant.coin.services.impl.CoinTransactionServiceImpl;
import com.yunphant.coin.utils.SDKUtils;
import com.yunphant.cointransaction.grpc.cointransaction.CoinSDKServiceResponse;
import com.yunphant.cointransaction.grpc.cointransaction.CoinTransactionGrpc;
import com.yunphant.cointransaction.grpc.cointransaction.QueryRequest;
import com.yunphant.cointransaction.grpc.cointransaction.WriteRequest;
import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * The type SDK rpc server.
 */
@SpringBootApplication
public class SDKRPCServer{

    private static final Logger logger = LoggerFactory.getLogger(SDKRPCServer.class);


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SDKRPCServer.class,args);
    }

    /**
     * private service RPCCoinTransactionService
     */
    @GRpcService
    private static class RPCCoinTransactionService extends CoinTransactionGrpc.CoinTransactionImplBase {

        private CoinTransactionService service = new CoinTransactionServiceImpl();
        private FabricConfig config = FabricConfigHelper.getInstance().getConfig();
        /**
         * <pre>
         * Create a coin transaction record in fabric network
         * </pre>
         *
         * @param request               Query request
         * @param responseObserver      response observer
         */
        @Override
        public void createTransaction(WriteRequest request, StreamObserver<CoinSDKServiceResponse> responseObserver) {

            HFClient client = YunphantHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                service.createTransaction(request.getJsonStr(), constructChannel(client,request.getOrgID()));
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(0))
                        .setMessage("Success creating transaction")
                        .setPayload("")
                        .build());
            } catch (Throwable t){
                logger.error("Fail to create transaction ",t);
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(9))
                        .setMessage("Fail to create transaction")
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        /**
         * <pre>
         * Query the transaction info from fabric
         * </pre>
         *
         * @param request               Query request
         * @param responseObserver      response observer
         */
        @Override
        public void queryTransaction(QueryRequest request, StreamObserver<CoinSDKServiceResponse> responseObserver) {
            HFClient client =  YunphantHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                String result = service.queryTransactionInfo(request.getQueryString(), constructChannel(client,request.getOrgID()));
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(0))
                        .setMessage("Success Query")
                        .setPayload(result)
                        .build());
            }catch (Throwable t) {
                logger.error("Fail to query transaction",t);
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(9))
                        .setMessage("Fail to query transaction")
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }


        /**
         * createAccount
         *
         * @param request               write request
         * @param responseObserver      responseObserver
         */
        @Override
        public void createAccount(WriteRequest request, StreamObserver<CoinSDKServiceResponse> responseObserver) {
            HFClient client =  YunphantHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                service.createAccount(request.getJsonStr(), constructChannel(client,request.getOrgID()));
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(0))
                        .setMessage("Success creating account for user")
                        .build());
            }catch (Throwable t) {
                logger.error("Fail to query transaction",t);
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(9))
                        .setMessage("Fail to query transaction")
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void queryAccount(QueryRequest request, StreamObserver<CoinSDKServiceResponse> responseObserver) {
            HFClient client =  YunphantHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                service.queryBalance(request.getQueryString(), constructChannel(client,request.getOrgID()));
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(0))
                        .setMessage("Success creating account for user")
                        .build());
            }catch (Throwable t) {
                logger.error("Fail to query transaction by userId",t);
                responseObserver.onNext(responseBuilder
                        .setStatus(CoinSDKServiceResponse.Status.forNumber(9))
                        .setMessage("Fail to query balance by userId "+request.getQueryString())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        private Channel constructChannel(HFClient client, String orgID) {
            Map<String,SampleOrg> orgMap = SDKUtils.getOrgsFromConfig(client);
            SampleOrg org;
            // TODO: 2017/9/19 not support identification for now
            switch (orgID){
                case "1":
                    org = orgMap.get("Org0");
                    break;
                case "2":
                    org = orgMap.get("Org1");
                    break;
                case "3":
                    org = orgMap.get("Org2");
                    break;
                case "4":
                    org = orgMap.get("Org3");
                    break;
                default:
                    org = orgMap.get("Org0");
                    break;
            }
            return SDKUtils.constructChannel(true,config.getChannelName(),client,org);
        }

    }
}
