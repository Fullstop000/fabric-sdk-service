package com.yunphant.grpc.cointransaction;

import com.yunphant.coin.TestHFClient;
import com.yunphant.coin.sample.SampleOrg;
import com.yunphant.coin.services.CoinTransactionService;
import com.yunphant.coin.services.impl.CoinTransactionServiceImpl;
import com.yunphant.coin.utils.SDKUtils;
import com.yunphant.cointransaction.grpc.cointransaction.*;
import com.yunphant.grpc.CommonRPCServer;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.yunphant.coin.common.CommonUtils.logError;

/**
 * The type Sdkrpc server.
 */
public class SDKRPCServer extends CommonRPCServer{

    private static final Logger logger = LoggerFactory.getLogger(SDKRPCServer.class);
    private SDKRPCServer(int port, BindableService service) {
        super(port, service);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        SDKRPCServer server = new SDKRPCServer(8980, new RPCCoinTransactionService());
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * private service RPCCoinTransactionService
     */
    private static class RPCCoinTransactionService extends CoinTransactionGrpc.CoinTransactionImplBase {

        private CoinTransactionService service = new CoinTransactionServiceImpl();
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
            HFClient client =  TestHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                service.createTransaction(request.getJsonStr(), constructChannel(client));
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
            HFClient client =  TestHFClient.getInstance().getClient();
            CoinSDKServiceResponse.Builder responseBuilder = CoinSDKServiceResponse.newBuilder();
            try {
                String result = service.queryTransactionInfo(request.getQueryString(), constructChannel(client));
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
    }

    /**
     * Construct channel
     * @param client  client
     * @return Channel
     */
    // TODO: 2017/9/18 refactor this
    private static Channel constructChannel(HFClient client) {
        SampleOrg org = SDKUtils.getSampleOrg(client);
        Channel channel = null;
        try {
            channel = SDKUtils.constructChannel(true,"mychannel",client,org);
            for (Peer peer : org.getPeers()) {
                channel.addPeer(peer);
            }
        } catch (TransactionException | InvalidArgumentException | ProposalException | IOException e) {
            logError(logger,"Error constructing channel : "+"mychannel",e);
        }
        return channel;
    }
}
