package io.singularitynet.sdk.mpe;

import java.math.BigInteger;
import java.util.Arrays;

import io.singularitynet.sdk.ethereum.Signer;
import io.singularitynet.sdk.registry.Utils;
import io.singularitynet.sdk.client.ServiceClient;
import io.singularitynet.sdk.registry.*;

public class FixedPaymentChannelPaymentStrategy implements PaymentStrategy {
        
    private final BigInteger channelId;
    private final Signer signer;

    public FixedPaymentChannelPaymentStrategy(BigInteger channelId, Signer signer) {
        this.channelId = channelId;
        this.signer = signer;
    }

    @Override
    public <ReqT, RespT> Payment getPayment(GrpcCallParameters<ReqT, RespT> callParams,
            ServiceClient serviceClient) {
        PaymentChannel channel = serviceClient.getPaymentChannelProvider()
            .getChannelById(channelId);
        BigInteger price = getPrice(channel, serviceClient);
        BigInteger newAmount = channel.getValue().add(price);
        return EscrowPayment.newBuilder()
            .setPaymentChannel(channel)
            .setAmount(BigInteger.valueOf(11))
            .setSigner(signer)
            .build();
    }

    private BigInteger getPrice(PaymentChannel channel, ServiceClient serviceClient) {
        ServiceMetadata serviceMetadata = serviceClient.getMetadataProvider().getServiceMetadata();
        // TODO: this can contradict to failover strategy do we need
        // GroupSelectionStrategy?
        EndpointGroup group = serviceMetadata.getEndpointGroups().stream()
            .filter(grp -> Arrays.equals(channel.getPaymentGroupId(), grp.getPaymentGroupId()))
            .findFirst().get();
        Pricing price = group.getPricing().stream()
            .filter(pr -> PriceModel.FIXED_PRICE.equals(pr.getPriceModel()))
            .findFirst().get();
        return price.getPriceInCogs();
    }

}
