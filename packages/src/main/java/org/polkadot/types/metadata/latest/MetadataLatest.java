package org.polkadot.types.metadata.latest;

import com.google.common.collect.Lists;
import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.v11.MetadataV11;
import org.polkadot.types.primitive.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/13 10:35 上午
 */
public class MetadataLatest extends Struct implements org.polkadot.types.metadata.Types.MetadataInterface {

    public static class MetadataExtrinsicLatest extends MetadataV11.MetadataExtrinsicV11 {

        public MetadataExtrinsicLatest(Object value) {
            super(value);
        }
    }

    public MetadataLatest(Object value) {
        super(new Types.ConstructorDef()
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(Modules.ModuleMetadataLatest.class)))
                        .add("extrinsic", TypesUtils.getConstructorCodec(MetadataExtrinsicLatest.class))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    public Vector<Modules.ModuleMetadataLatest> getModules() {
        return this.getField("modules");
    }

    protected List getCallNames() {
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

    protected List getEventNames() {
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

    protected List getStorageNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getStorage().isNone()
                            ? Lists.newArrayList()
                            : mod.getStorage().unwrap().getItems().stream().map(
                            (fn) -> {
                                return fn.getType().isMap()
                                        ? Lists.newArrayList(fn.getType().asMap().getKey().toString(), fn.getType().asMap().getValue().toString())
                                        : Lists.newArrayList(fn.getType().asPlain().toString());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    protected List getConstantNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getConstants().stream().map(
                            (constant) -> constant.getType().toString()
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {

        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(
                this.getStorageNames(),
                this.getCallNames(),
                this.getEventNames(),
                this.getConstantNames()
        ));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return null;
    }
}
