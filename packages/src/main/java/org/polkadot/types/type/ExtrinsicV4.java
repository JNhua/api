package org.polkadot.types.type;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.common.keyring.Types.KeyringPair;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.latest.Calls;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.primitive.U8;
import org.polkadot.utils.Utils;
import org.polkadot.utils.UtilsCrypto;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;


/**
 * Representation of an Extrinsic in the system. It contains the actual call,
 * (optional) signature and encodes with an actual length prefix
 * <p>
 * Can be:
 * - signed, to create a transaction
 * - left as is, to create an inherent
 */
public class ExtrinsicV4 extends Struct implements Types.IExtrinsic {

    static class ExtrinsicValueV4{
        private Method method;
        private ExtrinsicSignatureV4 signature;

        public ExtrinsicValueV4(Method method, ExtrinsicSignatureV4 extrinsicSignature){
            this.method = method;
            this.signature = extrinsicSignature;
        }
    }

    public ExtrinsicV4(Object value) {
        super(new Types.ConstructorDef()
                        .add("version", U8.class)
                        .add("signature", ExtrinsicSignatureV4.class)
                        .add("method", Method.class)
                , decodeExtrinsic(value));
    }

//    public ExtrinsicV4(Object value, boolean isSigned) {
//        super(new Types.ConstructorDef()
//                        .add("signature", ExtrinsicSignatureV4.class)
//                        .add("method", Method.class)
//                , decodeExtrinsic(value, isSigned));
//    }

    static Object decodeExtrinsic(Object value) {
        if(value instanceof ExtrinsicV4){
            return value;
        } else if (value instanceof Method) {
            LinkedHashMap<Object, Object> values = Maps.newLinkedHashMap();
            values.put("version", 4 + 128);
            values.put("method", value);
            return values;
        } else if (Utils.isU8a(value)) {
            ExtrinsicSignatureV4 extrinsicSignature = new ExtrinsicSignatureV4(value);
            Method method = new Method(ArrayUtils.subarray((byte[])value, extrinsicSignature.getEncodedLength(), ((byte[]) value).length));
            return new ExtrinsicValueV4(method, extrinsicSignature);
        }

        return value;
    }

//    static Object decodeExtrinsic(Object value, boolean isSigned) {
//        if(value instanceof ExtrinsicV4){
//            return value;
//        } else if (value instanceof Method) {
//            LinkedHashMap<Object, Object> values = Maps.newLinkedHashMap();
//            values.put("method", value);
//            return values;
//        } else if (Utils.isU8a(value)) {
//            ExtrinsicSignatureV4 extrinsicSignature = new ExtrinsicSignatureV4(value, isSigned);
//            Method method = new Method(ArrayUtils.subarray((byte[])value, extrinsicSignature.getEncodedLength(), ((byte[]) value).length));
//            return new ExtrinsicValueV4(method, extrinsicSignature);
//        }
//
//        return value;
//    }


    /**
     * The arguments passed to for the call, exposes args so it is compatible with {@link org.polkadot.types.primitive.Method}
     */
    @Override
    public List<Codec> getArgs() {
        return this.getMethod().getArgs();
    }

    /**
     * Thge argument defintions, compatible with {@link org.polkadot.types.primitive.Method}
     */
    @Override
    public Types.ConstructorDef getArgsDef() {
        return this.getMethod().getArgsDef();
    }

    /**
     * The actual `[sectionIndex, methodIndex]` as used in the Method
     */
    @Override
    public byte[] getCallIndex() {
        return this.getMethod().getCallIndex();
    }

    /**
     * The actual data for the Method
     */
    @Override
    public byte[] getData() {
        return this.getMethod().getData();
    }

    /**
     * The length of the value when encoded as a Uint8Array
     */
    @Override
    public int getEncodedLength() {
        int length = this.length();
        return length + Utils.compactToU8a(length).length;
    }

    /**
     * Convernience function, encodes the extrinsic and returns the actual hash
     */
    @Override
    public U8a getHash() {
        return new Hash(
                UtilsCrypto.blake2AsU8a(this.toU8a(), 256)
        );
    }

    /**
     * `true` is method has `Origin` argument (compatibility with {@link org.polkadot.types.primitive.Method})
     */
    @Override
    public boolean hasOrigin() {
        return this.getMethod().hasOrigin();
    }

    /**
     * `true` id the extrinsic is signed
     */
    @Override
    public boolean isSigned() {
        return this.getSignature().isSigned();
    }

    /**
     * The length of the encoded value
     */
    public int length() {
        return this.toU8a(true).length;
    }

    /**
     * The FunctionMetadata that describes the extrinsic
     */
    @Override
    public Calls.FunctionMetadataLatest getMeta() {
        return this.getMethod().getMeta();
    }

    /**
     * The {@link org.polkadot.types.primitive.Method} this extrinsic wraps
     */
    @Override
    public Method getMethod() {
        return this.getField("method");
    }

    /**
     * The ExtrinsicSignature
     */
    @Override
    public ExtrinsicSignatureV4 getSignature() {
        return this.getField("signature");
    }

    /**
     * Add an ExtrinsicSignature to the extrinsic (already generated)
     */
    //addSignature(signer:Address|Uint8Array, signature:Uint8Array, nonce:AnyNumber, era?:Uint8Array):Extrinsic
    @Override
    public ExtrinsicV4 addSignature(Object signer, byte[] signature, Object payload) throws Exception {
        this.getSignature().addSignature(signer, signature, (SignaturePayloadV4) payload);
        return this;
    }

    /**
     * Sign the extrinsic with a specific keypair
     */
    //sign(account:KeyringPair, options:SignatureOptions):Extrinsic
    @Override
    public ExtrinsicV4 sign(KeyringPair account, Types.SignatureOptions options) {
        this.getSignature().sign(this.getMethod(), account, options);
        return this;
    }

    /**
     * Returns a hex string representation of the value
     */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.toU8a());
    }

    /**
     * Converts the Object to JSON, typically used for RPC transfers
     */
    @Override
    public Object toJson() {
        return this.toHex();
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     *               Encodes the value as a Uint8Array as per the parity-codec specifications
     */

    @Override
    public byte[] toU8a(Object isBare) {
        byte[] encoded = super.toU8a(false);
        return (Boolean) isBare
                ? encoded
                : Utils.compactAddLength(encoded);
    }

}
