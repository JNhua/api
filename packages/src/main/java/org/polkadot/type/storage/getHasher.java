package org.polkadot.type.storage;

import com.google.common.collect.Lists;
import org.polkadot.utils.Utils;
import org.polkadot.utils.UtilsCrypto;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/20 10:44 上午
 */
public class getHasher {

    public static byte[] blake2128(byte[] data) {
        return UtilsCrypto.blake2AsU8a(data, 128);
    }

    public static byte[] blake2256(byte[] data) {
        return UtilsCrypto.blake2AsU8a(data);
    }

    public static byte[] blake2128concat(byte[] data) {
        return Utils.u8aConcat(Lists.newArrayList(UtilsCrypto.blake2AsU8a(data, 128), Utils.u8aToU8a(data)));
    }

    public static byte[] twox128(byte[] data) {
        return UtilsCrypto.xxhashAsU8a(data, 128);
    }

    public static byte[] twox256(byte[] data) {
        return UtilsCrypto.xxhashAsU8a(data, 256);
    }

    public static byte[] twox64concat(byte[] data) {
        return Utils.u8aConcat(Lists.newArrayList(UtilsCrypto.xxhashAsU8a(data), Utils.u8aToU8a(data)));
    }

    public static byte[] identity(byte[] data) {
        return Utils.u8aToU8a(data);
    }

    public static byte[] defaultFn(byte[] data){
        return UtilsCrypto.xxhashAsU8a(data, 128);
    }
}
