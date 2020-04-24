package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.U8aFixed;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/22 5:39 下午
 */
public class MultiSignature extends EnumType {

    public static class EcdsaSignature extends U8aFixed {

        public EcdsaSignature(Object value, int bitLength) {
            super(value, bitLength);
        }
    }

    public MultiSignature(Object value, int index) {
        super(new Types.ConstructorDef()
                        .add("Ed25519", Signature.Ed25519Signature.class)
                        .add("Sr26619", Signature.Sr25519Signature.class)
                        .add("Ecdsa", EcdsaSignature.class)
                , value, index, null);
    }

    public MultiSignature(Object value) {
        this(value, -1);
    }

}
