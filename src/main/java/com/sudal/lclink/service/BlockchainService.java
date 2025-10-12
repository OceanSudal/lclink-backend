package com.sudal.lclink.service;

import com.google.gson.Gson;
import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class BlockchainService {

    private final Gateway gateway;
    private final Gson gson = new Gson();

    @Value("${fabric.channel.name}")
    private String channelName;

    @Value("${fabric.chaincode.name}")
    private String chaincodeName;

    @Autowired
    public BlockchainService(Gateway gateway) {
        this.gateway = gateway;
    }

    private Contract getContract() {
        Network network = gateway.getNetwork(channelName);
        return network.getContract(chaincodeName);
    }

    public String createContract(String onchainContractId, String shipperCompanyId,
                                 String forwarderCompanyId, double totalPrice, String currency,
                                 String originPortCode, String destinationPortCode,
                                 String incotermsCode) throws Exception {
        Contract contract = getContract();

        byte[] result = contract.submitTransaction("CreateContract",
                onchainContractId,
                String.valueOf(shipperCompanyId),
                String.valueOf(forwarderCompanyId),
                String.valueOf(totalPrice),
                currency,
                originPortCode,
                destinationPortCode,
                incotermsCode);

        return new String(result, StandardCharsets.UTF_8);
    }

    public String queryContract(String onchainContractId) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("QueryContract", onchainContractId);
        return new String(result, StandardCharsets.UTF_8);
    }

    public boolean contractExists(String onchainContractId) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("ContractExists", onchainContractId);
        return Boolean.parseBoolean(new String(result, StandardCharsets.UTF_8));
    }

    public String updateShipmentStatus(String onchainContractId, String newStatus) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.submitTransaction("UpdateShipmentStatus",
                onchainContractId, newStatus);
        return new String(result, StandardCharsets.UTF_8);
    }

    public String queryContractHistory(String onchainContractId) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("QueryContractHistory", onchainContractId);
        return new String(result, StandardCharsets.UTF_8);
    }

    public String addDocumentHash(String onchainContractId, String documentType,
                                  String documentHash) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.submitTransaction("AddDocumentHash",
                onchainContractId, documentType, documentHash);
        return new String(result, StandardCharsets.UTF_8);
    }
}