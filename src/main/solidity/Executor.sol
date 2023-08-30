// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.9;
//import "console.sol";

contract Executor {
    struct Exchange {
        uint8 platform;
        address pool;
        bytes data;
    }

    uint256 constant ADDRESS_LENGTH = 0x14;
    uint256 constant UINT24_LENGTH = 0x03;
    uint256 constant UINT256_LENGTH = 0x20;
    uint256 constant UINT8_LENGTH = 0x01;

    //bytes public res;

    constructor () {
        //res = "0000000000000000000000000000000000000000000000000000000000000001";
    }

    /*function Test() public view returns (string memory){
        return "ATTESTED";
    }
    function getRes() public view returns (string memory)  {
        //bytes memory test = res;
        //res = "0x";
        string memory tmp = string(res);
        return tmp;
    }*/

    function execute_init (
        uint inAmount,
        address recipient,
        address[] calldata tokens,
        Exchange[] calldata exchanges
    ) external {
        bytes memory params = abi.encode(recipient, exchanges, tokens);
        //console.log("right params");
        //console.logBytes(params);
        //console.log(params);
        emit newParams(params);
    }

    function execute_assembly() external {
        /* uint inAmount
        address recipient
        uint len
        address[] token (size = length+1)
        Exchange[] exchanges (size = length)
        */
        uint input;
        uint inAmount;
        address recipient;
        uint len;
        address[] memory tokens;
        uint256[] memory exchangesSize;
        bytes memory exchanges;

        assembly {
            let calldata_len := calldatasize()
            let input_len := sub(calldata_len, 4)
            let fixed_len := add(mul(UINT256_LENGTH, 2), ADDRESS_LENGTH)

            input := mload(0x40)
            mstore(input, input_len)

            let input_data_fixed := add(input, 0x20)
            calldatacopy(input_data_fixed, 4, fixed_len)

            inAmount := mload(add(input, UINT256_LENGTH))
            recipient := mload(add(input, add(UINT256_LENGTH, ADDRESS_LENGTH)))
            len := mload(add(input, add(mul(UINT256_LENGTH, 2), ADDRESS_LENGTH)))

            //tokens_len := mul(ADDRESS_LENGTH, add(len, 1))
            let tokens_len := mul(UINT256_LENGTH, add(len, 1)) // = exchangesSize.len
            let exchangesSize_len := mul(UINT256_LENGTH, len)
            let exchanges_len := sub(input_len, add(add(fixed_len, tokens_len), exchangesSize_len))

            tokens := add(input_data_fixed, fixed_len)
            mstore(tokens, add(len, 1)) //size of length?
            let tokens_data := add(tokens, 0x20)
            calldatacopy(tokens_data, add(4, fixed_len), tokens_len)

            exchangesSize := add(tokens_data, tokens_len)
            mstore(exchangesSize, len)
            let exchangesSize_data := add(exchangesSize, 0x20)
            calldatacopy(exchangesSize_data, add(4, add(fixed_len, tokens_len)), exchangesSize_len)


            exchanges := add(exchangesSize_data, exchangesSize_len)
            mstore(exchanges, exchanges_len)
            let exchanges_data := add(exchanges, 0x20)
            calldatacopy(exchanges_data, add(4, add(add(fixed_len, tokens_len), exchangesSize_len)), exchanges_len)

            let free := add(exchanges_data, exchanges_len)
            let free_round := and(add(free, 31), not(31))
            mstore(0x40, free_round)
        }
        bytes memory params = abi.encodePacked(uint256(uint160(recipient)), uint256(0x60), 192 + exchanges.length, len);
        uint256 exchangeOffset = (0x20) * len;
        for(uint i = 0; i < exchangesSize.length; i++){
            params = abi.encodePacked(params, exchangeOffset);
            exchangeOffset += exchangesSize[i];
        }
        params = abi.encodePacked(params, exchanges, tokens.length, tokens);
        //console.log("my params = ");
        //console.logBytes(params);
        //console.log(params);
    }

    function execute_assembly_bytes() external {
        /* uint inAmount
        address recipient
        uint len
        address[] token
        bytes exchanges
        */
        uint input;

        uint inAmount;
        address recipient;
        uint len;
        address[] memory tokens;
        bytes memory exchanges;

        assembly {
            let calldata_len := calldatasize()
            let input_len := sub(calldata_len, 4)
            let fixed_len := add(mul(UINT256_LENGTH, 2), ADDRESS_LENGTH)

            input := mload(0x40)
            mstore(input, input_len)

            let input_data_fixed := add(input, 0x20)
            calldatacopy(input_data_fixed, 4, fixed_len)

            inAmount := mload(add(input, UINT256_LENGTH))
            recipient := mload(add(input, add(UINT256_LENGTH, ADDRESS_LENGTH)))
            len := mload(add(input, add(mul(UINT256_LENGTH, 2), ADDRESS_LENGTH)))

            let tokens_len := mul(UINT256_LENGTH, len)
            let exchanges_len := sub(input_len, add(fixed_len, tokens_len))

            tokens := add(input_data_fixed, fixed_len)
            mstore(tokens, len)
            let tokens_data := add(tokens, 0x20)
            calldatacopy(tokens_data, add(4, fixed_len), tokens_len)

            exchanges := add(tokens_data, tokens_len)
            mstore(exchanges, exchanges_len)
            let exchanges_data := add(exchanges, 0x20)
            calldatacopy(exchanges_data, add(4, add(fixed_len, tokens_len)), exchanges_len)

            let free := add(exchanges_data, exchanges_len)
            let free_round := and(add(free, 31), not(31))
            mstore(0x40, free_round)
        }
        bytes memory params = abi.encodePacked(uint256(uint160(recipient)), exchanges, len, tokens);
        //console.log("my params = ");
        //console.logBytes(params);
        //console.log(params);
    }

    event newParams(bytes params);
}
