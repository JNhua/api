package org.polkadot.type.storage;

import org.polkadot.types.metadata.Metadata;
import org.polkadot.types.metadata.latest.Modules;
import org.polkadot.types.metadata.latest.Storage;
import org.polkadot.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FromMetadata {
    /**
     * Extend a storage object with the storage modules & module functions present
     * in the metadata.
     *
     * @param metadata - The metadata to extend the storage object against.
     */
    public static Types.Storage fromMetadata(Metadata metadata) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Map<String, Types.ModuleStorage> storageModules = new HashMap<>();

        for (Modules.ModuleMetadataLatest moduleMetadata : metadata.asLatest().getModules()) {
            if (moduleMetadata.getStorage().isNone()) {
                continue;
            }

            final String section = Utils.stringCamelCase(moduleMetadata.getName().toString());
            final Storage.StorageMetadataLatest unwrapped = moduleMetadata.getStorage().unwrap();
            final String prefix = unwrapped.getPrefix().toString();

            Types.ModuleStorage newModule = new Types.ModuleStorage();
            // For access, we change the index names, i.e. Balances.FreeBalance -> balances.freeBalance
            for (Storage.StorageEntryMetadataLatest func : unwrapped.getItems()) {
                final String method = func.getName().toString();
                newModule.addFunction(
                        Utils.stringLowerFirst(method),
                        CreateFunction.createFunction(new CreateFunction.CreateItemFn(func, method, prefix, section), new CreateFunction.CreateItemOptions(null, metadata.getVersion(), false))
                );
            }

            storageModules.put(Utils.stringLowerFirst(prefix), newModule);
        }

        return new Types.Storage() {
            @Override
            public Types.ModuleStorage substrate() {
                return Substrate.substrate;
            }

            Map<String, Types.ModuleStorage> modules = storageModules;

            @Override
            public Types.ModuleStorage section(String section) {
                return modules.get(section);
            }

            @Override
            public Set<String> sectionNames() {
                return modules.keySet();
            }
        };
    }
}
