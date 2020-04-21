package org.polkadot.types.metadata.v2;

import com.google.common.collect.Lists;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.primitive.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataV2  extends Struct implements Types.MetadataInterface {

    public MetadataV2(Object value) {
        super(new org.polkadot.types.Types.ConstructorDef()
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(Modules.ModuleMetadataV2.class)))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    Vector<Modules.ModuleMetadataV2> getModules() {
        return this.getField("modules");
    }

    private List getCallNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getCalls().isNone()
                            ? Lists.newArrayList()
                            : mod.getCalls().unwrap().stream().map(
                            (fn) -> {
                                return fn.getArgs().stream().map(
                                        (arg) -> {
                                            return arg.getType().toString();
                                        }
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    private List getEventNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getEvents().isNone()
                            ? Lists.newArrayList()
                            : mod.getEvents().unwrap().stream().map(
                            (event) -> {
                                return event.getArguments().stream().map(
                                        Text::toString
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    private List getStorageNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getStorage().isNone()
                            ? Lists.newArrayList()
                            : mod.getStorage().unwrap().stream().map(
                            (fn) -> {
                                return fn.getType().isMap()
                                        ? Lists.newArrayList(fn.getType().asMap().getKey().toString(), fn.getType().asMap().getValue().toString())
                                        : Lists.newArrayList(fn.getType().asType().toString());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {

        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(this.getCallNames(), this.getEventNames(), this.getStorageNames()));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return null;
    }
}
