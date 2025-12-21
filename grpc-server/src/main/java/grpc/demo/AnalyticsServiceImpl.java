package grpc.demo;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Random;

@GrpcService
public class AnalyticsServiceImpl extends AnalyticsServiceGrpc.AnalyticsServiceImplBase {

    @Override
    public void calculateGameDiscount(GameDiscountRequest request, StreamObserver<GameDiscountResponse> responseObserver) {
        Random random = new Random();
        int discount = random.nextInt(91) ;
        double doublePrice = request.getGamePrice() - (request.getGamePrice() * discount / 100.0);
        long finalPrice = Math.round(doublePrice);

        GameDiscountResponse response = GameDiscountResponse.newBuilder()
                .setGameId(request.getGameId())
                .setGamePercentDiscount(discount)
                .setGameFinalPrice(finalPrice)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
