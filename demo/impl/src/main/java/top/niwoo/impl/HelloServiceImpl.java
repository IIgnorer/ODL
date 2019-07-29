/*
 * Copyright © 2017 Joliu and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.niwoo.impl;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.demo.rev170830.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.demo.rev170830.greeting.registry.GreetingRegistryEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.demo.rev170830.greeting.registry.GreetingRegistryEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.demo.rev170830.greeting.registry.GreetingRegistryEntryKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;


public class HelloServiceImpl implements DemoService {      //HelloServiceImpl类继承自DemoService类
    private static final Logger LOG = LoggerFactory.getLogger(HelloServiceImpl.class);
    private final DataBroker dataBroker;

    public HelloServiceImpl(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    public void init() {
        LOG.info("HelloRPC runs！Preparing to initialize the greeting registry");
        WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();//创建根节点
        InstanceIdentifier<GreetingRegistry> iid = InstanceIdentifier.create(GreetingRegistry.class);//根节点id
        GreetingRegistry greetingRegistry = new GreetingRegistryBuilder().build();
        transaction.put(LogicalDatastoreType.OPERATIONAL, iid, greetingRegistry);
        CheckedFuture<Void, TransactionCommitFailedException> future = transaction.submit();//teansactiong中数据写入
        Futures.addCallback(future, new LoggingFutureCallBack<>("Failed to create greeting registry", LOG));
    }

    /**
     * public void close(){
     * LOG.info("HelloRPC close!");
     * }
     */

    //private DataBroker myDataBroker; //设定私有Databroker成员MyDateBroker
    //public void MyRPCImpl(DataBroker dataBroker){
    //    this.myDataBroker = dataBroker;
    // } //私有变量需要由DemoProvider传递
    // final InstanceIdentifier<MyContainer1>MyContainer1_ID = InstanceIdentifier.builder(MyContainer1.class).build(); //有误，编辑yang文件container my-container1
    @Override
    public Future<RpcResult<HelloWorldOutput>> helloWorld(HelloWorldInput input) {   //接口里面的方法helloworldoutput.java
            String name = input.getName();
            HelloWorldOutput output = null;
            if (name != null) {
                output = new HelloWorldOutputBuilder()
                        .setGreeting("Hello:" + name)
                        .build();
            }
            writeToGreetingRegistry(input,output);
        return RpcResultBuilder.success(output).buildFuture();  //要返回output
    }
    private void writeToGreetingRegistry(HelloWorldInput input, HelloWorldOutput output) {
        WriteTransaction writeTransaction = dataBroker.newWriteOnlyTransaction();
        KeyedInstanceIdentifier<GreetingRegistryEntry, GreetingRegistryEntryKey> iid = InstanceIdentifier
                .create(GreetingRegistry.class).child(GreetingRegistryEntry.class, new GreetingRegistryEntryKey(input.getName()));
        GreetingRegistryEntry idData = new GreetingRegistryEntryBuilder().setGreeting(output.getGreeting()).setName(input.getName()).build();
        writeTransaction.put(LogicalDatastoreType.OPERATIONAL, iid, idData);
        CheckedFuture<Void, TransactionCommitFailedException> future = writeTransaction.submit();
        Futures.addCallback(future, new LoggingFutureCallBack<>("Failed to create greeting registry", LOG));
    }
}
    /**
     * This is my first rpc,its name is rpc1.
     *
     */
   /** @CheckReturnValue
    public Future<RpcResult<java.lang.Void>> myRpc1(){
        LOG.info("RPC1:This is my rpc1.It has nothing in it.");
        final ReadWriteTransaction myRwtx = this.myDataBroker.newReadWriteTransaction();
        //读取OPERATIONAL类数据库中的my-container1节点
        CheckedFuture<Optional<MyContainer1>, ReadFailedException> myRWFuture = myRwtx.read(LogicalDatastoreType.OPERATIONAL,MyContainer1_ID);//报错改变
        final Boolean bResultOfCommitFuture = true;  //bResultOfCommitFuture是写事务操作提交的条件，值为true时提交
        final ListenableFuture<Void>commitFuture = Futures.transform(myRWFuture,new AsyncFunction<Optional<MyContainer1>,Void>(){
            @Override
            public  ListenableFuture<Void>apply(Optional<MyContainer1>myData)throws Exception{
                if (myData.isPresent()){
                    System.out.println("MyContainer1's Data Does Exit");
                }else{
                    return null;
                }
                //分别针对情况提交操作或处理失败场景
                if (bResultOfCommitFuture == false){
                    //处理失败场景
                    return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("",RpcResultBuilder.newWarning(RpcError.ErrorType.APPLICATION,"in-use","This happens when CommitFuture fails")));
                }else{
                    myRwtx.put(LogicalDatastoreType.OPERATIONAL,MyContainer1_ID, new MyContainer1Builder().setMyContainer1Leaf1((byte)0).setMyContGrpLeaf1(88888).build());
                    return myRwtx.submit();//设置my-container1节点值为1,有误
                }//出错，无setMycontainerLeaf1
            }//ListenableFuture<Void>apply结束
        }//AsyncFunction<Optional<MyContainer1>,Void>()结束
        );//Future.transform结束
        //添加callback函数，根据实务操作是否成功进行后续处理
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            //若更新data store成功，进行一下操作
            @Override
            public void onSuccess(Void result) {
                LOG.info("ListenableFuture's Commit:Success!",result.toString());
                System.out.println("ListenableFuture's Commit:Success!");
            }//on success结束

            @Override
            public void onFailure(Throwable t) {
                LOG.debug("ListenableFuture's Commit:Fail!",t);
                System.out.println("ListenableFuture's Commit:Fails");
            }//on Fails结束
        }//FutureCallback<Void>结束
        );//Future.addCallback语句结束



        return Futures.immediateFuture(RpcResultBuilder.<Void>success().build());
    }*/

