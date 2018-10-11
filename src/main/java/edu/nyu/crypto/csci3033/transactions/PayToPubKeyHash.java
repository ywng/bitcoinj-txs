package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.util.logging.Logger;

import static org.bitcoinj.script.ScriptOpCodes.*;
import static org.bitcoinj.script.ScriptOpCodes.OP_VERIFY;

/**
 * Created by bbuenz on 24.09.15.
 */
public class PayToPubKeyHash extends ScriptTransaction {
    // TODO: Problem 1
    private ECKey key; //for testNet
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PayToPubKey.class);

    public PayToPubKeyHash(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
        // private key of the vanity address I generated externally.
        // private key: 5JeAEtfEKV31GPQjv5JG7ydFyVTCjZtanoRru7B6cbDWRPmoh2P
        // vanity address: 1ngXXkZTo3Faxv4NJa4NFz2Uq9sCBYHfA
        String privateKeyStr = "5JeAEtfEKV31GPQjv5JG7ydFyVTCjZtanoRru7B6cbDWRPmoh2P";

        if (privateKeyStr.length() == 51 || privateKeyStr.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(parameters, privateKeyStr);
            key = dumpedPrivateKey.getKey();
        } else {
            BigInteger privateKey = Base58.decodeToBigInteger(privateKeyStr);
            key = ECKey.fromPrivate(privateKey);
        }

        getWallet().importKey(key);
        LOGGER.info("The vanity address to be sent to: " + key.getPubKeyHash());

    }

    @Override
    public Script createInputScript() {
        // TODO: Create a P2PKH script
        // TODO: be sure to test this script on the mainnet using a vanity address
        ScriptBuilder sb = new ScriptBuilder();
        sb.op(OP_DUP);
        sb.op(OP_HASH160);
        sb.data(key.getPubKeyHash());
        sb.op(OP_EQUALVERIFY);
        sb.op(OP_CHECKSIG);
        return sb.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // TODO: Redeem the P2PKH transaction
        TransactionSignature txSig = sign(unsignedTransaction, key);

        ScriptBuilder sb = new ScriptBuilder();
        sb.data(txSig.encodeToBitcoin());
        sb.data(key.getPubKey());
        return sb.build();
    }
}
