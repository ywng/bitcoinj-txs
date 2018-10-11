package edu.nyu.crypto.csci3033.transactions;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class MultiSigTransaction extends ScriptTransaction {
    // TODO: Problem 3

    private DeterministicKey bankKey;
    private List<DeterministicKey> customerKeys = new ArrayList<>();
    private DeterministicKey adversaryKey;
    private final int M = 1, N = 3; //M-of-N multisig tx

    public MultiSigTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);

        bankKey = getWallet().freshReceiveKey();
        for(int i=0; i<N; i++) {
            customerKeys.add(getWallet().freshReceiveKey());
        }

        // it is used to test if other customer can redeem the tx
        adversaryKey = getWallet().freshReceiveKey();
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend using signatures from the bank and one of the customers
        ScriptBuilder sb = new ScriptBuilder();

        sb.data(bankKey.getPubKey());
        sb.op(OP_CHECKSIGVERIFY);

        // number of signature (the M)
        sb.smallNum(M);
        for (int i = 0; i < N; i++) {
            sb.data((customerKeys.get(i)).getPubKey());
        }
        // number of public key (the N)
        sb.smallNum(N);
        sb.op(OP_CHECKMULTISIG);

        return sb.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // Please be aware of the CHECK_MULTISIG bug!
        // TODO: Create a spending script

        // Customer 3 should be able to redeem it
        TransactionSignature txSigCustomer = sign(unsignedTransaction, customerKeys.get(0));
        TransactionSignature txSigBank = sign(unsignedTransaction, bankKey);
        byte[] zero = {0x00};

        ScriptBuilder sb = new ScriptBuilder();
        sb.data(zero); //sb.op(OP_0); //to avoid CHECK_MULTISIG bug
        sb.data(txSigCustomer.encodeToBitcoin());
        sb.data(txSigBank.encodeToBitcoin());
        return sb.build();
    }

}
