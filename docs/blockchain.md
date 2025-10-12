## 블록체인 연동 설정

### 1. 인증서 복사
```bash
scp -i ~/sudal-dev-key.pem -r \
    ubuntu@ec2-0-0-0-0.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/fabric/fabric-samples/lclink-network/organizations/peerOrganizations/shipper.lclink.com/users/User1@shipper.lclink.com \
    ~/lclink-crypto/
```

### 2. src/main/resources/application.properties 설정
```properties
fabric.network.name=lclink-network
fabric.channel.name=lcchannel
fabric.chaincode.name=lclink_chaincode
fabric.connection.profile=classpath:connection-profile.json

# 경로 수정
fabric.crypto.path=/Users/username/lclink-crypto/User1@shipper.lclink.com/msp

fabric.identity.label=appUser
fabric.msp.id=ShipperMSP
```

### 3. src/main/resources/connection-profile.json 설정
```json
{
  "name": "lclink-network",
  "version": "1.0.0",
  "client": {
    "organization": "Shipper"
  },
  "organizations": {
    "Shipper": {
      "mspid": "ShipperMSP",
      "peers": ["peer0.shipper.lclink.com"]
    }
  },
  "peers": {
    "peer0.shipper.lclink.com": {
      "url": "grpc://[shipper 퍼블릭 IPv4 주소]:7051"
    }
  },
  "orderers": {
    "orderer.lclink.com": {
      "url": "grpc://[forwarder 퍼블릭 IPv4 주소]:7050"
    }
  },
  "channels": {
    "lcchannel": {
      "orderers": ["orderer.lclink.com"],
      "peers": {
        "peer0.shipper.lclink.com": {}
      }
    }
  }
}
```

### 4. /etc/hosts 설정
```bash
sudo vi /etc/hosts
```
```bash
[orderer 퍼블릭 IPv4 주소]     orderer.lclink.com
[shipper 퍼블릭 IPv4 주소]     peer0.shipper.lclink.com
[forwarder 퍼블릭 IPv4 주소]   peer0.forwarder.lclink.com
```