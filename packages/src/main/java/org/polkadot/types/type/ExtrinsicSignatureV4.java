package org.polkadot.types.type;


import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.common.keyring.Types.SignOptions;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.primitive.U8;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A container for the {@link org.polkadot.types.type.Signature} associated with a specific {@link org.polkadot.type.extrinsics}
 */
public class ExtrinsicSignatureV4 extends Struct implements Types.IExtrinsicSignature {

    public static final byte[] IMMORTAL_ERA = new byte[]{0};
    public static final byte[] EMPTY_U8A = new byte[0];

//    public ExtrinsicSignatureV4(Object value, boolean isSigned) {
//        super(new Types.ConstructorDef()
//                        .add("signer", Address.class)
//                        .add("signature", MultiSignature.class)
//                        .add("tip", BalanceCompact.class)
//                        .add("blockHash", Hash.class)
//                        .add("genesisHash", Hash.class)
//                        .add("nonce", NonceCompact.class)
//                        .add("specVersion", U32.class)
//                        .add("era", ExtrinsicEra.class)
//                , decodeExtrinsicSignature(value, isSigned));
//    }

    public ExtrinsicSignatureV4(Object value) {
        super(new Types.ConstructorDef()
                        .add("signer", Address.class)
                        .add("signature", MultiSignature.class)
                        .add("tip", BalanceCompact.class)
                        .add("blockHash", Hash.class)
                        .add("genesisHash", Hash.class)
                        .add("nonce", NonceCompact.class)
                        .add("specVersion", U32.class)
                        .add("era", ExtrinsicEra.class)
                , decodeExtrinsicSignature(value, false));
    }

    static Object decodeExtrinsicSignature(Object value, boolean isSigned) {
        if (value == null) {
            return EMPTY_U8A;
        } else if (value instanceof ExtrinsicSignatureV4) {
            return value;
        }

        if (isSigned) {
            return value;
        } else {
            return EMPTY_U8A;
        }
    }

    @Override
    public int getEncodedLength() {
        return this.isSigned()
                ? super.getEncodedLength()
                : 0;
    }

    @Override
    public boolean isSigned() {
        return !this.getSignature().isEmpty();
    }

    /**
     * The ExtrinsicEra (mortal or immortal) this signature applies to
     */
    public ExtrinsicEra getEra() {
        return this.getField("era");
    }


    /**
     * The {@link org.polkadot.types.type.Nonce} for the signature
     */
    public NonceCompact getNonce() {
        return this.getField("nonce");
    }

    /**
     * The actuall {@link org.polkadot.types.type.MultiSignature} hash
     */
    public MultiSignature getMultiSignature() {
        return this.getField("signature");
    }

    public Signature.Sr25519Signature getSignature() {
        return (Signature.Sr25519Signature) this.getMultiSignature().value();
    }

    /**
     * The {@link org.polkadot.types.type.Address} that signed
     */
    public Address getSigner() {
        return this.getField("signer");
    }

    private ExtrinsicSignatureV4 injectSignature(MultiSignature signature, Address signer, SignaturePayloadV4 payload) {
        this.put("era", payload.getEra());
        this.put("nonce", payload.getNonce());
        this.put("signer", signer);
        this.put("signature", signature);
        this.put("tip", payload.getTip());
        return this;
    }


    /**
     * Adds a raw signature
     */
    ExtrinsicSignatureV4 addSignature(Object signer, byte[] signature, SignaturePayloadV4 payload) {
        Address signerInstance = new Address(signer);
        MultiSignature signatureInstance = new MultiSignature(signature);

        return this.injectSignature(signatureInstance, signerInstance, payload);
    }


    /**
     * Generate a payload and pplies the signature from a keypair
     */
    ExtrinsicSignatureV4 sign(Method method, KeyringPair account, Types.SignatureOptions signatureOptions) {

        Address signer = new Address(account.publicKey());

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("nonce", signatureOptions.getNonce());
        values.put("method", method.toHex());
        values.put("era", signatureOptions.getEra() == null ? IMMORTAL_ERA : signatureOptions.getEra());
        values.put("blockHash", signatureOptions.getBlockHash());
        values.put("genesisHash", signatureOptions.getGenesisHash());
        values.put("specVersion", signatureOptions.getVersion().getSpecVersion());
        values.put("tip", signatureOptions.getTip() != null ? signatureOptions.getTip() : 0);

        SignaturePayloadV4 signingPayload = new SignaturePayloadV4(values);

        MultiSignature signature = new MultiSignature(signingPayload.sign(account, new SignOptions(true)));

        return this.injectSignature(signature, signer, signingPayload);
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(Object isBare) {
        if (this.isSigned()) {
            return super.toU8a(isBare);
        } else {
            return EMPTY_U8A;
        }
    }
}
