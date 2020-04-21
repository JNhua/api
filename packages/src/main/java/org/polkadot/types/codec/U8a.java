package org.polkadot.types.codec;

import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Codec;
import org.polkadot.utils.Utils;

import java.util.Arrays;


/**
 * A basic wrapper around Uint8Array, with no frills and no fuss. It does differ
 * from other implementations wher it will consume the full Uint8Array as passed to
 * it. As such it is meant to be subclassed where the wrapper takes care of the
 * actual lengths instead of used directly.
 * @noInheritDoc
 */
//public class U8a extends ArrayList<UByte> implements Codec {
public class U8a implements Codec {

    public byte[] raw = null;

    public U8a(Object value) {
        raw = decodeU8a(value);
    }

    public U8a(){
    }

    private static byte[] decodeU8a(Object value) {
        if (Utils.isU8a(value)) {
            return (byte[]) value;
        }

        return Utils.u8aToU8a(value);
    }

  /**
   * The length of the value when encoded as a Uint8Array
   */
    @Override
    public int getEncodedLength() {
        return this.raw.length;
    }


    /**
     * Returns true if the type wraps an empty/default all-0 value
     */
    @Override
    public boolean isEmpty() {
        if (this.raw.length <= 0) {
            return true;
        }

        for (int i = 0; i < this.raw.length; i++) {
            if (this.raw[i] != 0) {
                return false;
            }
        }
        return true;
    }

  /**
   * The length of the value
   */
    public int length() {
        return this.raw.length;
    }

  /**
   * Compares the value of the input to see if there is a match
   */
    @Override
    public boolean eq(Object other) {
        if (other instanceof U8a) {
            return Arrays.equals(this.raw, ((U8a) other).raw);
        }
        return false;
    }

    /**
     * @param begin The position to start at
     * @param end   The position to end at
     * Create a new subarray from the actual buffer. This is needed for compat reasons since a new Uint8Array gets returned here
     */
    public U8a subarray(int begin, int end) {
        byte[] subarray = ArrayUtils.subarray(this.raw, begin, end);
        return new U8a(subarray);
    }

  /**
   * Returns a hex string representation of the value
   */
    @Override
    public String toHex() {
        return Utils.u8aToHex(this.raw);
    }

  /**
   * Converts the Object to JSON, typically used for RPC transfers
   */
    @Override
    public Object toJson() {
        return this.toHex();
    }

  /**
   * Returns the string representation of the value
   */
    @Override
    public String toString() {
        return this.toHex();
    }

    /**
     * @param isBare true when the value has none of the type-specific prefixes (internal)
     * Encodes the value as a Uint8Array as per the parity-codec specifications
     */
    @Override
    public byte[] toU8a(boolean isBare) {
        return this.raw;
    }

}
