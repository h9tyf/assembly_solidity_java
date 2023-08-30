package org.web3j.generated.contracts;

import org.web3j.EVMTest;
import java.lang.Exception;
import java.lang.String;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.transaction.type.ITransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Ethereum;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EVMTest
public class ExecutorTest {
    private static Executor executor;
    private static final String nodeUrl = System.getenv().getOrDefault("WEB3J_NODE_URL", "http://localhost:8545");

    private String WETH = "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2";
    private String DAI = "0x6B175474E89094C44Da98b954EedeAC495271d0F";


    String recipient = "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266";
    @BeforeAll
    static void deploy() throws Exception {
        Credentials credentials = Credentials.create("0x42c72fd9b5e5fa468a7841d887b83c15e06ea8269c185ba41728b9b952a13ef9");
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
        executor = Executor.deploy(web3j, credentials, new DefaultGasProvider()).send();
    }
    public byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
    public byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    @Test
    public void simple() throws Exception {
        Executor.Exchange exchange1 = new Executor.Exchange(
                new Uint8(1),
                new Address("0xD8dEC118e1215F02e10DB846DCbBfE27d477aC19"),
                new DynamicBytes(decodeHexString("0x0000000000000000000000000000000000000000000000000000000000000064"))
        );
        Executor.Exchange exchange2 = new Executor.Exchange(
                new Uint8(2),
                new Address("0x60594a405d53811d3BC4766596EFD80fd545A270"),
                new DynamicBytes(decodeHexString("0x00000000000000000000000000000000000000000000000000000000000001f400000000000000000000000000000000000000000000000000000000000001f4"))
        );

        TransactionReceipt res = executor.execute_init(
                        BigInteger.valueOf(100),
                        recipient,
                        Arrays.asList(WETH, DAI, WETH),
                        Arrays.asList(exchange1, exchange2)).send();
        System.out.println(res);
    }

    @Test
    public void encodeParams(){
        Executor.Exchange exchange1 = new Executor.Exchange(
                new Uint8(1),
                new Address("0xD8dEC118e1215F02e10DB846DCbBfE27d477aC19"),
                new DynamicBytes(decodeHexString("0000000000000000000000000000000000000000000000000000000000000064"))
        );
        Executor.Exchange exchange2 = new Executor.Exchange(
                new Uint8(2),
                new Address("0x60594a405d53811d3BC4766596EFD80fd545A270"),
                new DynamicBytes(decodeHexString("00000000000000000000000000000000000000000000000000000000000001f400000000000000000000000000000000000000000000000000000000000001f4"))
        );
        String result = "0x" +
                TypeEncoder.encode(new DynamicStruct(
                        new Address(recipient),
                        new DynamicArray(exchange1, exchange2),
                        new DynamicArray(new Address(WETH), new Address(DAI), new Address(WETH))

                ));


        String result_1 = "0x" +
                TypeEncoder.encode(new DynamicStruct(
                        new Utf8String("\u0019Ethereum Signed Message:\n"),
                        new Address("0xbfF89Fe7598f162ACC86CfC3267Eb132F69B7e2B"),
                        new Uint(BigInteger.valueOf(10))));
        assertEquals("0x"
                +"000000000000000000000000f39fd6e51aad88f6f4ce6ab8827279cfffb92266"
                +"0000000000000000000000000000000000000000000000000000000000000060"
                +"0000000000000000000000000000000000000000000000000000000000000220"
                +"00000000000000000000000000000000000000000000000000000000000000020"
                +"000000000000000000000000000000000000000000000000000000000000040"
                +"00000000000000000000000000000000000000000000000000000000000000e0"
                +"0000000000000000000000000000000000000000000000000000000000000001"
                +"000000000000000000000000d8dec118e1215f02e10db846dcbbfe27d477ac19"
                +"0000000000000000000000000000000000000000000000000000000000000060"
                +"0000000000000000000000000000000000000000000000000000000000000020"
                +"0000000000000000000000000000000000000000000000000000000000000064"
                +"0000000000000000000000000000000000000000000000000000000000000002"
                +"00000000000000000000000060594a405d53811d3bc4766596efd80fd545a270"
                +"0000000000000000000000000000000000000000000000000000000000000060"
                +"0000000000000000000000000000000000000000000000000000000000000040"
                +"00000000000000000000000000000000000000000000000000000000000001f4"
                +"00000000000000000000000000000000000000000000000000000000000001f4"
                +"0000000000000000000000000000000000000000000000000000000000000003"
                +"000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
                +"0000000000000000000000006b175474e89094c44da98b954eedeac495271d0f"
                +"000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2", result);
    }
}
