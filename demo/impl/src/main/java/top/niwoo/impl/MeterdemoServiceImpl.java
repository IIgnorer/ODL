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
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.MeterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.MeterKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.OutputPortValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.MeterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.AddMeterInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.AddMeterOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.SalMeterService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.BandId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.MeterBandType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.MeterFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.MeterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.band.type.band.type.DropBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.MeterBandHeadersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeaderBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeaderKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.meter.band.header.MeterBandTypesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.meterdemo.rev160821.MeterdemoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.meterdemo.rev160821.ProcessMeterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.meterdemo.rev160821.ProcessMeterOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.meterdemo.rev160821.ProcessMeterOutputBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.xml.soap.SAAJMetaFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class MeterdemoServiceImpl implements MeterdemoService{


    private static final Logger LOG = LoggerFactory.getLogger(MeterdemoServiceImpl.class);
    private final SalFlowService salFlowService;
    private final SalMeterService salMeterService;
    private final DataBroker dataBroker;//为了读取交换机信息存入nodeII


    public MeterdemoServiceImpl(SalFlowService salFlowService, SalMeterService salMeterService, DataBroker dataBroker){
        this.salFlowService = salFlowService;
        this.salMeterService = salMeterService;
        this.dataBroker = dataBroker;
    }
        public void init(){
        LOG.info("MeterRPC is Running!");
        }
        public void close(){
        LOG.info("MeterRPC is stop!");
    }

    @Override
    public Future<RpcResult<ProcessMeterOutput>> processMeter(ProcessMeterInput input){
            ProcessMeterOutputBuilder outputBuilder = new ProcessMeterOutputBuilder();
            Long meterID = 1L;

        int i=1;//
        final InstanceIdentifier<Node> nodeII = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(new NodeId("openflow:" + i)))
                .build();
        NodeRef nodeRef = new NodeRef(nodeII);

            addMeter(input.getSwitchId(), input.getLimitedRate(), input.getBurstSize(),meterID);
            addFlow(input.getSwitchId(), input.getSrcPort(), input.getDstPort(), meterID);

            ProcessMeterOutput output = null;
            output = new ProcessMeterOutputBuilder().setResult("Success!").build();

            return RpcResultBuilder.success(output).buildFuture();//2019.7.4增加返回值
        }

    private Future<RpcResult<AddMeterOutput>> addMeter(String switchId, String rate, String burstSize, long meterId){
        //下发计量表
        LOG.info("The meter-table is being created!");//打印日证明开始创建计量表

        Long switchIdd = Long.valueOf(switchId);
        Long ratee = Long.valueOf(rate);
        Long burstSizee = Long.valueOf(burstSize);

        ArrayList<MeterBandHeader> meterBandHeaders = new ArrayList<>();//设置一个表meterBandHeaders用来存放计量表的操作
        MeterBandHeader meterBandHeader = new MeterBandHeaderBuilder()//创建一个meterBandHeader来设置计量表的操作的具体值
                .setMeterBandTypes(new MeterBandTypesBuilder().setFlags(new MeterBandType(true,false,false)).build())
                .setBandBurstSize(burstSizee)
                .setBandId(new BandId(switchIdd))
                .setBandRate(ratee)
                .setBandType(new DropBuilder().setDropRate(7000L).setDropBurstSize(200L).build())
                .setKey(new MeterBandHeaderKey(new BandId(1L)))
                .build();
        meterBandHeaders.add(meterBandHeader);//将上述设置的计量表的操作输入到meterBandHeaders表中
        MeterBuilder meterBuilder = new MeterBuilder();//此步设置了一个计量表meterBuilder，接下来进行赋值
        meterBuilder.setMeterName("MXC")
                .setMeterId(new MeterId(meterId))
                .setMeterBandHeaders(new MeterBandHeadersBuilder().setMeterBandHeader(meterBandHeaders).build())//发现计量表需要一个meterBandHeaders也就是计量表的操作项，在上面进行设置↑
                .setKey(new MeterKey(new MeterId(1L)))
                .setFlags(new MeterFlags(false,false,false,false))
                .setContainerName("MXC")
                .setBarrier(Boolean.TRUE);//计量表的赋值

        LOG.info("The meter-table has been created!");

        AddMeterInputBuilder addMeterInputBuilder = new AddMeterInputBuilder(meterBuilder.build());
        salMeterService.addMeter(addMeterInputBuilder.build());//下发计量表
        LOG.info("Meter-table has been sent!");
        return null;
    }



    private Future<RpcResult<AddFlowOutput>> addFlow(String switchId, String inPort, String outPort, long meterId){
        //下发流表
        LOG.info("The flow-table is being created!");//打印日志证明开始创建流表

        FlowBuilder flowBuilder = new FlowBuilder();//构建一个流表

        MatchBuilder matchBuilder = new MatchBuilder();//Flow表中包含一个match匹配，因此构建一个匹配
        matchBuilder.setInPort(new NodeConnectorId(inPort));//设置数据流入端口
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
                .setInstructions(createSentToControllerInstructions(outPort).build())//流表中的操作项Instructions，包含数据包转发的方向以及与meter表关联的两个instruction
                .setKey(new FlowKey(new FlowId("MXC")))
                .setPriority(0)
                .setStrict(false)
                .setTableId((short)0);//设置流表各指标

        LOG.info("The flow-table has been created!");//打印日志证明创建流表完成

        AddFlowInputBuilder addFlowInputBuilder = new AddFlowInputBuilder(flowBuilder.build());
        //*******************************************交换机名未赋，nodeII未传入***************************************************
        //addFlowInputBuilder.setNode();//交换机名
        Future<RpcResult<AddFlowOutput>> resultFuture = salFlowService
                .addFlow(addFlowInputBuilder.build());//下发流表

        //***************************************************第二个流表***********************************************************
        FlowBuilder flowBuilder1 = new FlowBuilder();//构建一个流表

        MatchBuilder matchBuilder1 = new MatchBuilder();//Flow表中包含一个match匹配，因此构建一个匹配
        matchBuilder1.setInPort(new NodeConnectorId(outPort));//设置数据流入端口
        flowBuilder1.setMatch(matchBuilder1.build());//将match信息放进Flow表中
        flowBuilder1.setFlowName("MXC")
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
                .setInstructions(createSentToControllerInstructions(inPort).build())//流表中的操作项Instructions，包含数据包转发的方向以及与meter表关联的两个instruction
                .setKey(new FlowKey(new FlowId("MXC")))
                .setPriority(0)
                .setStrict(false)
                .setTableId((short)0);//设置流表各指标

        LOG.info("The flow-table has been created!");//打印日志证明创建流表完成

        AddFlowInputBuilder addFlowInputBuilder1 = new AddFlowInputBuilder(flowBuilder1.build());
        Future<RpcResult<AddFlowOutput>> resultFuture1 = salFlowService
                .addFlow(addFlowInputBuilder1.build());//下发流表

        LOG.info("Flow-table has been sent!");

        return null;
    }



    public static InstructionsBuilder createSentToControllerInstructions(String outPort){
        LOG.info("The instruction is being created");//打印日志证明开始进行Flow表的Instructions的书写
        final List<Action> actions = new ArrayList<>();//用来存放第一个instruction里面数据包转发的动作

        //构建ACTION
        final ActionBuilder actionBuilder = new ActionBuilder();
        final OutputActionBuilder outputActionBuilder = new OutputActionBuilder();//output到控制器

        Uri uri = new Uri(outPort);
        //Uri uri = new Uri(OutputPortValues.CONTROLLER.toString());//设置Uri型变量用来指明转发的发送端口
        outputActionBuilder.setMaxLength(64)
                .setOutputNodeConnector(uri);//设置output动作发现设置发送端口名的时候需要一个Uri型变量，在上面进行设置

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
        //final List<Action> actionList = new ArrayList<>();
        //ActionBuilder ab = new ActionBuilder();
        //OutputActionBuilder output = new OutputActionBuilder();

    }

}
