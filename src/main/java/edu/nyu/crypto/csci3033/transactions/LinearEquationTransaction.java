package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class LinearEquationTransaction extends ScriptTransaction {
    // TODO: Problem 2
    public LinearEquationTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend by two numbers x and y such that x+y=first 4 digits of your suid and |x-y|=last 4 digits of your suid (perhaps +1)
        // first 4 digits of nyu id is 1921; last 4 digits of nyu id is 0466 (I use 0467 for integer solution)
        String idPart1 = "1921", idPart2 = "0467";
        ScriptBuilder sb = new ScriptBuilder();

        //Add Verify
        sb.op(OP_2DUP);
        sb.op(OP_ADD);
        sb.data(encode(new BigInteger(idPart1)));
        // we need to continue to verify the subtraction,
        // so use OP_EQUALVERIFY which returns NOTHING when it is true
        sb.op(OP_EQUALVERIFY);

        //Subtract Verify
        sb.op(OP_SUB);
        sb.op(OP_ABS);
        sb.data(encode(new BigInteger(idPart2)));
        sb.op(OP_EQUAL);

        return sb.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedScript) {
        // TODO: Create a spending script
        // x and y are solution to the two linear equations
        String x = "1194", y = "727";
        ScriptBuilder sb = new ScriptBuilder();

        sb.data(encode(new BigInteger(x)));
        sb.data(encode(new BigInteger(y)));

        return sb.build();
    }

    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
}
