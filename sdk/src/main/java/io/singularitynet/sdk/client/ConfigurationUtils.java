package io.singularitynet.sdk.client;

import java.math.BigInteger;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;

import io.singularitynet.sdk.common.Utils;
import io.singularitynet.sdk.ethereum.Address;

/**
 * Configuraiton related helper methods.
 */
public class ConfigurationUtils {

    private ConfigurationUtils() {
    }

    /**
     * Load configuration from properties.
     * @param props properties containing configuration values.
     * @return static configuration instance.
     */
    public static Configuration fromProperties(Properties props) {
        Configuration.Builder builder = Configuration.newBuilder()
            .setEthereumJsonRpcEndpoint(props.getProperty("ethereum.json.rpc.endpoint"))
            .setIdentityType(Enum.valueOf(Configuration.IdentityType.class, props.getProperty("identity.type").toUpperCase()));

        Optional.ofNullable(props.getProperty("ipfs.endpoint")).ifPresent(builder::setIpfsEndpoint);
        Optional.ofNullable(props.getProperty("identity.mnemonic")).ifPresent(builder::setIdentityMnemonic);
        Optional.ofNullable(props.getProperty("identity.private.key.hex")).map(Utils::hexToBytes).ifPresent(builder::setIdentityPrivateKey);
        Optional.ofNullable(props.getProperty("registry.address")).map(Address::new).ifPresent(builder::setRegistryAddress);
        Optional.ofNullable(props.getProperty("multi.party.escrow.address")).map(Address::new).ifPresent(builder::setMultiPartyEscrowAddress);
        Optional.ofNullable(props.getProperty("gas.price")).map(BigInteger::new).ifPresent(builder::setGasPrice);
        Optional.ofNullable(props.getProperty("gas.limit")).map(BigInteger::new).ifPresent(builder::setGasLimit);

        return builder.build();
    }

    private static final class JsonConfiguration {
        URL ethereumJsonRpcEndpoint;
        URL ipfsEndpoint;
        String identityType;
        String identityMnemonic;
        String identityPrivateKeyHex;
        String registryAddress;
        String multiPartyEscrowAddress;
        String gasLimit;
        String gasPrice;
    }

    /**
     * Load configuration from JSON.
     * @param json string containing JSON with values.
     * @return static configuration instance.
     */
    public static Configuration fromJson(String json) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        JsonConfiguration config = gson.fromJson(json, JsonConfiguration.class);

        Configuration.Builder builder = Configuration.newBuilder()
            .setEthereumJsonRpcEndpoint(config.ethereumJsonRpcEndpoint)
            .setIdentityType(Enum.valueOf(Configuration.IdentityType.class, config.identityType.toUpperCase()));

        Optional.ofNullable(config.ipfsEndpoint).ifPresent(builder::setIpfsEndpoint);
        Optional.ofNullable(config.identityMnemonic).ifPresent(builder::setIdentityMnemonic);
        Optional.ofNullable(config.identityPrivateKeyHex).map(Utils::hexToBytes).ifPresent(builder::setIdentityPrivateKey);
        Optional.ofNullable(config.registryAddress).map(Address::new).ifPresent(builder::setRegistryAddress);
        Optional.ofNullable(config.multiPartyEscrowAddress).map(Address::new).ifPresent(builder::setMultiPartyEscrowAddress);
        Optional.ofNullable(config.gasPrice).map(BigInteger::new).ifPresent(builder::setGasPrice);
        Optional.ofNullable(config.gasLimit).map(BigInteger::new).ifPresent(builder::setGasLimit);

        return builder.build();
    }

}
