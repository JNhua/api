package org.polkadot.types.type;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Types;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Tuple;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.primitive.U64;
import org.polkadot.utils.Utils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import static org.polkadot.types.type.ExtrinsicSignatureV4.IMMORTAL_ERA;

/**
 * The era for an extrinsic, indicating either a mortal or immortal extrinsic
 */
public class ExtrinsicEra extends EnumType {

    public static class ImmortalEra extends U8a {
        public ImmortalEra(Object value) {
            super(IMMORTAL_ERA);
        }
    }

    static int getTrailingZeros(Number period) {
        char[] binary = period.toString().toCharArray();
        int index = 0;
        while (binary[binary.length - 1 - index] == '0') {
            index++;
        }
        return index;
    }

    public static class MortalEra extends Tuple {

        public MortalEra(Object value) throws Exception {
            super(new Types.ConstructorDef()
                            .add("period", U64.class)
                            .add("phase", U64.class)
                    , decodeMortalEra(value));
        }

        static Object[] encodeValue(Object v1, Object v2) {
            return Lists.newArrayList(v1, v2).toArray();
        }

        static Object decodeMortalU8a(byte[] value) {
            if (value.length == 0) {
                return encodeValue(new U64(0), new U64(0));
            }

            BigInteger first = Utils.u8aToBn(ArrayUtils.subarray(value, 0, 1), true, false);
            BigInteger second = Utils.u8aToBn(ArrayUtils.subarray(value, 1, 2), true, false);
            BigInteger encoded = first.add(second.shiftLeft(8));
            BigInteger period = BigInteger.valueOf(2).shiftLeft(encoded.mod(BigInteger.valueOf(1 << 4)).intValue());
            int quantizeFactor = Math.max(period.shiftRight(12).intValue(), 1);
            BigInteger phase = encoded.shiftRight(4).multiply(BigInteger.valueOf(quantizeFactor));

            assert period.intValue() >= 4 && phase.compareTo(period) < 0 : "Invalid data passed to Mortal era";

            return encodeValue(new U64(period), new U64(phase));
        }

        static Object decodeMortalObject(Map<String, Object> value) {
            BlockNumber current = (BlockNumber) value.get("current");
            BigDecimal period = (BigDecimal) value.get("period");


            double calPeriod = Math.pow(2, Math.ceil(Math.log(period.doubleValue()) / Math.log(2)));
            calPeriod = Math.min(Math.max(calPeriod, 4), 1 << 16);

            double phase = current.toNumber() % calPeriod;
            double quantizeFactor = Math.max(calPeriod % Math.pow(10, 12), 1);
            double quantizedPhase = phase / quantizeFactor * quantizeFactor;

            return encodeValue(new U64(BigInteger.valueOf(((Double)calPeriod).longValue())), new U64(BigInteger.valueOf(((Double)quantizedPhase).longValue())));
        }

        static Object decodeMortalEra(@Nullable Object value) throws Exception {
            if (value == null) {
                return encodeValue(new U64(0), new U64(0));
            } else if (Utils.isHex(value)) {
                return decodeMortalU8a(Utils.hexToU8a((String) value));
            } else if (value.getClass().isArray()) {
                return decodeMortalU8a(Utils.u8aToU8a(value));
            } else if (Utils.isU8a(value)) {
                return decodeMortalU8a((byte[]) value);
            } else if (value instanceof Map) {
                return decodeMortalObject((Map<String, Object>) value);
            } else if(value instanceof MortalEra){
                return value;
            }

            throw new Exception("Invalid data passed to Mortal era");
        }

        U64 getPeriod() {
            return (U64) this.get(0);
        }

        U64 getPhase() {
            return (U64) this.get(1);
        }

        @Override
        public byte[] toU8a(Object isBare) {
            Number period = getPeriod().toNumber();
            Number phase = getPhase().toNumber();
            Number quantizeFactor = Math.max((long) period >> 12, 1);
            Number trailingZeros = getTrailingZeros(period);
            Number encoded = Math.min(15, Math.max(1, trailingZeros.intValue() - 1)) + ((phase.longValue() / quantizeFactor.longValue()) << 4);
            Number first = encoded.longValue() >> 8;
            Number second = encoded.longValue() & 0xff;

            ArrayList<Number> array = new ArrayList<>();
            array.add(second);
            array.add(first);
            return Utils.u8aToU8a(array.toArray());
        }
    }


    public ExtrinsicEra(Object value, int index) throws Exception {
        super(new Types.ConstructorDef()
                        .add("immortalEra", ImmortalEra.class)
                        .add("mortalEra", MortalEra.class)
                , decodeExtrinsicEra(value), index, null);
    }

    public ExtrinsicEra(Object value) throws Exception {
        this(value, -1);
    }

    static Object decodeExtrinsicEra(Object value) throws Exception {
        if (value == null) {
            return new byte[]{0};
        } else if (Utils.isU8a(value)) {
            if (((byte[]) value).length == 0 || ((byte[]) value)[0] == 0) {
                return new byte[]{0};
            } else {
                return new byte[]{1, ((byte[]) value)[0], ((byte[]) value)[1]};
            }
        } else if (value instanceof ExtrinsicEra) {
            return ExtrinsicEra.decodeExtrinsicEra(((ExtrinsicEra) value).toU8a());
        } else if (Utils.isHex(value)) {
            return ExtrinsicEra.decodeExtrinsicEra(Utils.hexToU8a((String) value));
        } else if (value instanceof Map) {
            if (((Map<String, Object>) value).get("mortalEra") != null) {
                return new ExtrinsicEra(((MortalEra) ((Map<String, Object>) value).get("mortalEra")).toU8a(), 1);
            } else if (((Map<String, Object>) value).get("immortalEra") != null) {
                return new ExtrinsicEra(((ImmortalEra) ((Map<String, Object>) value).get("immortalEra")).toU8a(), 0);
            } else {
                return new MortalEra(value);
            }
        }

        throw new Exception("Invalid data passed to Era");
    }
}
