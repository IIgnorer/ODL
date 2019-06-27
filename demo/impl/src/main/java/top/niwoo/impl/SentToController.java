/*
 * Copyright © 2017 Joliu and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.niwoo.impl;


import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.MeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.MeterKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.OutputPortValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.MeterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.AddMeterInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.SalMeterService;

import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.band.type.band.type.DropBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.MeterBandHeadersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeaderBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeaderKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.meter.band.header.MeterBandTypesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


public class SentToController {
    private static final Logger LOG = LoggerFactory.getLogger(SentToController.class);//SentToController调用时打印日志
    private  final SalFlowService salFlowService;//为了调用salFlowService方法下发流表而引入变量
    private  final SalMeterService salMeterService;//为了调用salMeterService方法下发计量表而引入变量
    private final DataBroker dataBroker;//为了读取交换机信息存入nodeII

    private static final short TABLE_ID = (short)0;//设置流表的参数
    private static final String SENT_TO_CONTROLLER= "SENT_TO_CONTROLLER";//设置流表的参数
    private static final String DEFAULT_FLOW_ID = "42";//设置流表的参数


    public SentToController(SalFlowService salFlowService, SalMeterService salMeterService, DataBroker dataBroker){
        LOG.info("SentToController Service is Start!");//调用SentoController类时打印日志：SentToController Service is Start!
        this.salMeterService = salMeterService;//将外部接口SaMeterService引入
        this.salFlowService = salFlowService;//将外部接口SaFlowService引入
        this.dataBroker = dataBroker;//
    }

    public void init() {
        LOG.info("I'M SENTING!");
            //获取交换机NODE
        new Thread(new Runnable() {
            //加入线程以测试流表和计量表下发是否成功
            @Override
            public void run() {

            int i=1;//
            final InstanceIdentifier<Node> nodeII
                    = InstanceIdentifier.builder(Nodes.class)
                    .child(Node.class, new NodeKey(new NodeId("openflow:" + i)))
                    .build();//创建一个根节点
            NodeRef nodeRef = new NodeRef(nodeII); //nodeRef存放着目的交换机的信息

            AddFlowInputBuilder addFlowInputBuilder = new AddFlowInputBuilder(createFlow());//创建一个创建流表的方法，将createFlow方法创建的流表输入进去
            addFlowInputBuilder.setNode(new NodeRef(nodeII));//将交换机的值给流表写入

            AddMeterInputBuilder addMeterInputBuilder = new AddMeterInputBuilder(createMeter());//创建一个创建计量表的方法，将createMeter方法创建的流表输入

            Future<RpcResult<AddFlowOutput>> resultFuture = salFlowService
                    .addFlow(addFlowInputBuilder.build());//下发流表
                LOG.info("Flow-table has been sent!");
            salMeterService.addMeter(addMeterInputBuilder.build());//下发计量表
                LOG.info("Meter-table has been sent!");

            }
        }).start();
    }
    public void close(){
        LOG.info("this progress has closed");
    }


    private Flow createFlow(){
        LOG.info("The flow-table is being created!");//打印日志证明开始创建流表
        FlowBuilder flowBuilder = new FlowBuilder();//构建一个流表

        MatchBuilder matchBuilder = new MatchBuilder();//Flow表中包含一个match匹配，因此构建一个匹配
        matchBuilder.setInPort(new NodeConnectorId("1"));//设置数据流入端口


        /*final EthernetMatchBuilder ethernetMatchBuilder = new EthernetMatchBuilder().setEthernetType(new EthernetTypeBuilder().setType(new EtherType(35020L)).build())
                .setEthernetDestination(new EthernetDestinationBuilder().build())
                .setEthernetSource(new EthernetSourceBuilder().build());//由下面步骤得出创建
        matchBuilder.setEthernetMatch(ethernetMatchBuilder.build());//设置matchBuilder信息发现需要一个ethernetMatchBuilderbuilder()信息，因此需要在上面创建一个*/


        //flow中包含match与instruction
        flowBuilder.setMatch(matchBuilder.build());//将match信息放进Flow表中
        flowBuilder.setFlowName("MXC")
                .setFlags(new FlowModFlags(false,false,false,false,false))
                .setCookieMask(new FlowCookie(BigInteger.valueOf(10L)))
                .setCookie(new FlowCookie(BigInteger.valueOf(10L)))
                .setContainerName(null)
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setBarrier(Boolean.FALSE)
                .setHardTimeout(0)
                .setId(new FlowId("MXC"))
                .setIdleTimeout(0)
                .setInstallHw(false)
                .setInstructions(createSentToControllerInstructions().build())//流表中的操作项Instructions，包含数据包转发的方向以及与meter表关联的两个instruction
                .setKey(new FlowKey(new FlowId(DEFAULT_FLOW_ID)))
                .setPriority(0)
                .setStrict(false)
                .setTableId(TABLE_ID);//设置流表各指标

        LOG.info("The flow-table has been created!");//打印日志证明创建流表完成

        return flowBuilder.build();//返回这个刚刚创建的流表内容
    }
    public static InstructionsBuilder createSentToControllerInstructions(){
        LOG.info("The instruction is being created");//打印日志证明开始进行Flow表的Instructions的书写
        final List<Action> actions = new ArrayList<>();//用来存放第一个instruction里面数据包转发的动作

        //构建ACTION
        final ActionBuilder actionBuilder = new ActionBuilder();
        final OutputActionBuilder outputActionBuilder = new OutputActionBuilder();//output到控制器

        Uri uri = new Uri(OutputPortValues.CONTROLLER.toString());//设置Uri型变量用来指明转发的发送端口
        outputActionBuilder.setMaxLength(64)
                .setOutputNodeConnector(uri);//设置output动作发现设置发送端口名的时候需要一个Uri型变量，在上面进行设置
        //******************************************此处设置数据出端口，但是数据类型为uri，如何设置成为固定端口，方便测试**********************************************

        actionBuilder.setAction(new OutputActionCaseBuilder().setOutputAction(outputActionBuilder.build()).build());//向actiongBuilder中设置这个动作的发送端口
        actions.add(actionBuilder.build());//将上一个actionBuilder体放入actions动作列表

        //instruction
        final InstructionBuilder instructionBuilder = new InstructionBuilder();//先设置一个用来存放这条流表一个Instruction的变量instructionBuilder
        final ApplyActionsBuilder applyActionsBuilder = new ApplyActionsBuilder().setAction(actions);//InstructionBuilder中包含的设置ApplyActionBuilder需要设置，将actions在前面设置后在这里输入
        instructionBuilder.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(applyActionsBuilder.build()).build());//设置这个instructionBuilder变量中的流表具体Instruction

        //构建meter与flow联系的instruction
        final InstructionBuilder instructionBuilder1 = new InstructionBuilder();//为了与meter表关联而设置第二个Instruction，变量名为instructionBuilder1
        final InstructionBuilder applyMeterInstruction = new InstructionBuilder().setOrder(1)
                .setInstruction(new MeterCaseBuilder()
                        .setMeter(new org.opendaylight.yang.gen.v1.urn.opendaylight
                                .flow.types.rev131026.instruction.instruction
                                .meter._case.MeterBuilder()
                                .setMeterId(new MeterId(1L))//在Instruction里设置了Flow表与Meter表的关联
                                .build())
                        .build());//缺少一个setInstructionKey

        //创建一个InstructionsBuilder放两个Instruction
        InstructionsBuilder instructionsBuilder = new InstructionsBuilder();//设置一个instructionsBuilder来存放两个Instruction
        List<Instruction> instructions = new ArrayList<>();//表instructions用来存放两个instructionBuilder的体
        instructions.add(instructionBuilder.build());//将instructionBuilder的体输入给表instructions
        instructions.add(instructionBuilder1.build());//将instructionBuilder1的体输入给表instructions
        instructionsBuilder.setInstruction(instructions);//将表instructions输入给变量instructionsBuilder

        LOG.info("The instruction has been created");//打印日志证明Flow表的Instructions的书写完成

        return instructionsBuilder;//返回变量instructiongsBuilder
        /*final List<Action> actionList = new ArrayList<>();
        ActionBuilder ab = new ActionBuilder();
        OutputActionBuilder output = new OutputActionBuilder();*/

    }
    private Meter createMeter(){
        LOG.info("The meter-table is being created!");//打印日证明开始创建计量表
        ArrayList<MeterBandHeader> meterBandHeaders = new ArrayList<>();//设置一个表meterBandHeaders用来存放计量表的操作
        MeterBandHeader meterBandHeader = new MeterBandHeaderBuilder()//创建一个meterBandHeader来设置计量表的操作的具体值
                .setMeterBandTypes(new MeterBandTypesBuilder().setFlags(new MeterBandType(true,false,false)).build())
                .setBandBurstSize(7000L)
                .setBandId(new BandId(1L))
                .setBandRate(7000L)
                .setBandType(new DropBuilder().setDropRate(7000L).setDropBurstSize(200L).build())
                .setKey(new MeterBandHeaderKey(new BandId(1L)))
                .build();
        meterBandHeaders.add(meterBandHeader);//将上述设置的计量表的操作输入到meterBandHeaders表中
        MeterBuilder meterBuilder = new MeterBuilder();//此步设置了一个计量表meterBuilder，接下来进行赋值
        meterBuilder.setMeterName("MXC")
                .setMeterId(new MeterId(1L))
                .setMeterBandHeaders(new MeterBandHeadersBuilder().setMeterBandHeader(meterBandHeaders).build())//发现计量表需要一个meterBandHeaders也就是计量表的操作项，在上面进行设置↑
                .setKey(new MeterKey(new MeterId(1L)))
                .setFlags(new MeterFlags(false,false,false,false))
                .setContainerName("MXC")
                .setBarrier(Boolean.TRUE);//计量表的赋值

        LOG.info("The meter-table has been created!");//打印日证明计量表创建完成

        return meterBuilder.build();
    }
}
