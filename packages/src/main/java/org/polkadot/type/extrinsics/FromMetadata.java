package org.polkadot.type.extrinsics;

import org.polkadot.types.metadata.latest.MetadataLatest;
import org.polkadot.types.primitive.Method;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class FromMetadata {
    private static final Logger logger = LoggerFactory.getLogger(FromMetadata.class);

    /**
     * Extend a storage object with the storage modules & module functions present
     * in the metadata.
     *
     * @param metadata - The metadata to extend the storage object against.
     */
    public static Method.ModulesWithMethods fromMetadata(MetadataLatest metadata) {
        AtomicInteger sectionIndex = new AtomicInteger(-1);
        AtomicInteger methodIndex = new AtomicInteger(-1);

        metadata.getModules()
                .stream()
                .filter(
                        (moduleMetadata) -> {
                            if (moduleMetadata.getCalls().isNone()) {
                                return false;
                            } else {
                                return moduleMetadata.getCalls().unwrap().length() > 0;
                            }
                        }
                )
                .forEach(
                        (moduleMetadata) -> {
                            String section = Utils.stringCamelCase(moduleMetadata.getName().toString());
                            sectionIndex.getAndIncrement();

                            Method.Methods methods = new Method.Methods();
                            moduleMetadata.getCalls().unwrap().forEach(
                                    (functionMetadata) -> {
                                        methodIndex.getAndIncrement();
                                        // extrinsics.balances.set_balance -> extrinsics.balances.setBalance
                                        String method = Utils.stringCamelCase(functionMetadata.getName().toString());
                                        methods.put(method, CreateUnchecked.createDescriptor(section, sectionIndex.intValue(), method, methodIndex.intValue(), functionMetadata));
                                    }
                            );
                            Index.extrinsics.put(section, methods);
                        }
                );


        return Index.extrinsics;
    }


}
