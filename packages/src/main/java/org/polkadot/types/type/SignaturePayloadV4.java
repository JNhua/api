package org.polkadot.types.type;

import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U32;
import org.polkadot.utils.UtilsCrypto;

import java.util.HashMap;
import java.util.Map;

/**
 * A signing payload for an {@link org.polkadot.type.extrinsics}. For the final encoding, it is variable length based
 * on the conetnts included
 * <p>
 * 8 bytes The Transaction Index/Nonce as provided in the transaction itself.
 * 2+ bytes The Function Descriptor as provided in the transaction itself.
 * 2 bytes The Transaction Era as provided in the transaction itself.
 * 32 bytes The hash of the authoring block implied by the Transaction Era and the current block.
 */
public class SignaturePayloadV4 extends Struct {

    public static class SignaturePayloadValue {
        Object nonce;
        Method method;
        ExtrinsicEra era;
        Hash blockHash;
        BalanceCompact tip;
        U32 specVersion;
        Hash genesisHash;
    }


    protected byte[] _signature;

    public SignaturePayloadV4(Object value) {
        super(new Types.ConstructorDef()
                        .add("nonce", NonceCompact.class)
                        .add("method", Bytes.class)
                        .add("era", ExtrinsicEra.class)
                        .add("blockHash", Hash.class)
                        .add("tip", BalanceCompact.class)
                        .add("specVersion", U32.class)
                        .add("genesisHash", Hash.class),
                value);
    }

    public BalanceCompact getTip(){
        return this.getField("tip");
    }

    /**
     * `true` if the payload refers to a valid signature
     */
    public boolean isSigned() {
        return this._signature != null && this._signature.length == 64;
    }


    /**
     * The block {@link org.polkadot.types.type.Hash} the signature applies to (mortal/immortal)
     */
    public Hash getBlockHash() {
        return this.getField("blockHash");
    }

    /**
     * The {@link org.polkadot.types.primitive.Bytes} contained in the payload
     */
    public Bytes getMethod() {
        return this.getField("method");
    }

    /**
     * The ExtrinsicEra
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }

    /**
     * The {@link org.polkadot.types.type.Nonce}
     */
    public NonceCompact getNonce() {
        return this.getField("nonce");
    }

    /**
     * The raw signature as a `Uint8Array`
     */
    public byte[] getSignature() {
        if (!this.isSigned()) {
            throw new RuntimeException("Transaction is not signed");
        }

        return this._signature;
    }


    /**
     * Sign the payload with the keypair
     */
    public byte[] sign(KeyringPair signerPair, org.polkadot.common.keyring.Types.SignOptions signOptions) {

        byte[] u8a = this.toU8a(new HashMap<String, Boolean>(1) {{
            put("method", true);
        }});
        byte[] encoded = u8a.length > 256
                ? UtilsCrypto.blake2AsU8a(u8a)
                : u8a;
        this._signature = signerPair.sign(encoded, signOptions);
        return this._signature;
    }

}
