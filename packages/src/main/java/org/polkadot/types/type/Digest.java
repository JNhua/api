package org.polkadot.types.type;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Bytes;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.primitive.U64;

/**
 * A Header Digest
 */
public class Digest extends Struct {
    public Digest(Object value) {
        super(new Types.ConstructorDef()
                        .add("logs", Vector.with(TypesUtils.getConstructorCodec(DigestItem.class)))
                , value);
    }

    /**
     * The DigestItem logs
     */
    public Vector<DigestItem> getLogs() {
        return this.getField("logs");
    }


    /**
     * Log item that is just a stream of {@link org.polkadot.types.primitive.Bytes}
     */
    public static class Other extends Bytes {

        public Other(Object value) {
            super(value);
        }
    }

    /**
     * Log for Authories changed
     */
    public static class AuthoritiesChange extends Vector<AuthorityId> {
        public AuthoritiesChange(Object value) {
            super(TypesUtils.getConstructorCodec(AuthorityId.class), value);
        }
    }

    /**
     * Log for changes to the Trie root
     */
    public static class ChangesTrieRoot extends Hash {

        public ChangesTrieRoot(Object value) {
            super(value);
        }
    }

    /**
     * Log item indicating a sealing event
     */
    public static class SealV0 extends Tuple {
        public SealV0(Object value) {
            super(
                    new Types.ConstructorDef()
                            .add("U64", U64.class)
                            .add("Signature", Signature.class),
                    value
            );
        }

        /**
         * The wrapped {@link org.polkadot.types.type.Signature}
         */
        public Signature getSignature() {
            return this.getFiled(1);
        }

        /**
         * The wrapped {@link org.polkadot.types.primitive.U64} slot
         */
        public U64 slot() {
            return this.getFiled(0);
        }
    }

    /**
     * Log item indicating consensus
     */
    public static class Consensus extends Tuple {
        public Consensus(Object value) {
            super(
                    new Types.ConstructorDef()
                            .add("U32", U32.class) // actually a [u8; 4]
                            .add("Bytes", Bytes.class)
                    , value
            );
        }

        /**
         * `true` if the engine matches aura
         */
        public boolean isAura() {
            return this.getEngine().eq(0x61727561);// ['a', 'u', 'r', 'a']
        }

        /**
         * The wrapped engine {@link org.polkadot.types.primitive.U32}
         */
        public U32 getEngine() {
            return this.getFiled(0);
        }

        /**
         * The wrapped {@link org.polkadot.types.primitive.Bytes}
         */
        public Bytes getData() {
            return getFiled(1);
        }

        /**
         * The slot and signature extracted from the raw data (assuming Aura)
         */
        public Pair<U64, Signature> asAura() {
            byte[] raw = this.getData().toU8a(true);
            byte[] subarray1 = ArrayUtils.subarray(raw, 0, 4);
            byte[] subarray2 = ArrayUtils.subarray(raw, 64, raw.length);
            return Pair.of(new U64(subarray1), new Signature(subarray2));
        }
    }

    public static class Seal extends Tuple{
        public Seal(Object value){
            super(
                    new Types.ConstructorDef()
                            .add("U32", U32.class)
                            .add("Bytes", Bytes.class)
                    , value
            );
        }
    }

    public static class PreRuntime extends Tuple{
        public PreRuntime(Object value){
            super(
                    new Types.ConstructorDef()
                            .add("U32", U32.class)
                            .add("Bytes", Bytes.class)
                    , value
            );
        }
    }

    /**
     * A {@link org.polkadot.types.codec.EnumType} the specifies the specific item in the logs of a Digest
     */
    //export class DigestItem extends EnumType<AuthoritiesChange | ChangesTrieRoot | Other| Seal> {
    public static class DigestItem extends EnumType {
        public DigestItem(Object value) {
            super(
                    new Types.ConstructorDef()
                            .add("Other", Other.class)// Position 0, as per Rust (encoding control)
                            .add("AuthoritiesChange", Vector.with(TypesUtils.getConstructorCodec(AuthorityId.class)))
                            .add("ChangesTrieRoot", ChangesTrieRoot.class)
                            .add("SealV0", SealV0.class)
                            .add("Consensus", Consensus.class)
                            .add("Seal", Seal.class)
                            .add("PreRuntime", PreRuntime.class)
                    , value, -1, null
            );
        }


        /**
         * Returns the item as a AuthoritiesChange
         */
        public Vector<AuthorityId> getAsAuthoritiesChange() {
            return (Vector<AuthorityId>) this.value();
        }

        /**
         * Returns the item as a ChangesTrieRoot
         */
        public ChangesTrieRoot getAsChangesTrieRoot() {
            return (ChangesTrieRoot) this.value();
        }

        /**
         * @desciption Retuns the item as a Consensus
         */
        public Consensus getAsConsensus() {
            return (Consensus) this.value();
        }

        /**
         * Returns the item as a Other
         */
        public Other getAsOther() {
            return (Other) this.value();
        }

        /**
         * Returns the item as a Seal
         */
        public SealV0 getAsSeal() {
            return (SealV0) this.value();
        }

        /**
         * Returns true on Consensus
         */
        public boolean isConsensus() {
            return "Consensus".equals(this.getType());
        }

        /**
         * Returns true on Seal
         */
        public boolean isSeal() {
            return "Seal".equals(this.getType());
        }

    }

}

