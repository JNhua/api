package org.polkadot.example.promise;

import com.onehilltech.promises.Promise;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.rpc.provider.ws.WsProvider;
import org.polkadot.types.type.Header;

public class E02_ListenToBlocks {

    //static String endPoint = "wss://poc3-rpc.polkadot.io/";
    //static String endPoint = "wss://substrate-rpc.parity.io/";
    //static String endPoint = "ws://45.76.157.229:9944/";
    static String endPoint = "ws://127.0.0.1:9944";

    static void initEndPoint(String[] args) {
        if (args != null && args.length >= 1) {
            endPoint = args[0];
            System.out.println(" connect to endpoint [" + endPoint + "]");
        } else {
            System.out.println(" connect to default endpoint [" + endPoint + "]");
        }
    }

    public static void main(String[] args) {
        // Create an await for the API
        //Promise<ApiPromise> ready = ApiPromise.create();
        initEndPoint(args);

        WsProvider wsProvider = new WsProvider(endPoint);

        Promise<ApiPromise> ready = ApiPromise.create(wsProvider);

        ready.then(api -> {
            Promise<IRpcFunction.Unsubscribe<Promise>> invoke = api.rpc().chain().function("subscribeNewHead").invoke(
                    (IRpcFunction.SubscribeCallback<Header>) (Header header) ->
                    {
                        System.out.println("Chain is at block: " + header.getBlockNumber());
                    });
            return invoke;
        }).then((IRpcFunction.Unsubscribe<Promise> result) -> {
            return null;
        })._catch((err) -> {
            err.printStackTrace();
            return Promise.value(err);
        });


        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
