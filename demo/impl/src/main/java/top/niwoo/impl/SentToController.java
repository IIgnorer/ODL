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
    private static final Logger LOG = LoggerFactory.getLogger(SentToController.class);
    private  final SalFlowService salFlowService;
    private  final SalMeterService salMeterService;
    private final DataBroker dataBroker;

    private static final short TABLE_ID = (short)0;
    private static final String SENT_TO_CONTROLLER= "SENT_TO_CONTROLLER";
    private static final String DEFAULT_FLOW_ID = "42";


    public SentToController(SalFlowService salFlowService, SalMeterService salMeterService, DataBroker dataBroker){
        LOG.info("SentToController Service is Start!");
        this.salMeterService = salMeterService;
        this.salFlowService = salFlowService;
        this.dataBroker = dataBroker;
    }

    public void start() {
        LOG.info("I'M SENTING!");
        new Thread(()-> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                LOG.warn("Thread interrupted,", e);
            }
            //获取交换机NODE
            int i=1;
            final InstanceIdentifier<Node> nodeII
                    = InstanceIdentifier.builder(Nodes.class)
                    .child(Node.class, new NodeKey(new NodeId("openflow:" + i)))
                    .build();
            NodeRef nodeRef = new NodeRef(nodeII); //nodeRef存放着目的交换机的信息

            AddFlowInputBuilder addFlowInputBuilder = new AddFlowInputBuilder(createFlow());
            addFlowInputBuilder.setNode(new NodeRef(nodeII));//将交换机的值给流表写入

            AddMeterInputBuilder addMeterInputBuilder = new AddMeterInputBuilder(createMeter());

            Future<RpcResult<AddFlowOutput>> resultFuture = salFlowService
                    .addFlow(addFlowInputBuilder.build());//下发流表
            salMeterService.addMeter(addMeterInputBuilder.build());//下发计量表

        }).start();
    }


    private Flow createFlow(){
        LOG.info("The flow-table is being created!");
        FlowBuilder flowBuilder = new FlowBuilder();//构建一个流表

        MatchBuilder matchBuilder = new MatchBuilder();//构建一个匹配
        final EthernetMatchBuilder ethernetMatchBuilder = new EthernetMatchBuilder().setEthernetType(new EthernetTypeBuilder().setType(new EtherType(35020L)).build())
                .setEthernetDestination(new EthernetDestinationBuilder().build())
                .setEthernetSource(new EthernetSourceBuilder().build());
        matchBuilder.setEthernetMatch(ethernetMatchBuilder.build());

        //flow中包含match与instruction
        flowBuilder.setMatch(matchBuilder.build());
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
                .setInstructions(createSentToControllerInstructions().build())
                .setKey(new FlowKey(new FlowId(DEFAULT_FLOW_ID)))
                .setPriority(0)
                .setStrict(false)
                .setTableId(TABLE_ID);

        return null;
    }
    public static InstructionsBuilder createSentToControllerInstructions(){
        LOG.info("The instruction is being created");
        final List<Action> actions = new ArrayList<>();
        //构建ACTION
        final ActionBuilder actionBuilder = new ActionBuilder();
        final OutputActionBuilder outputActionBuilder = new OutputActionBuilder();
        //output到控制器
        Uri uri = new Uri(OutputPortValues.CONTROLLER.toString());
        outputActionBuilder.setMaxLength(64)
                .setOutputNodeConnector(uri);

        actionBuilder.setAction(new OutputActionCaseBuilder().setOutputAction(outputActionBuilder.build()).build());
        actions.add(actionBuilder.build());

        //instruction
        final InstructionBuilder instructionBuilder = new InstructionBuilder();
        final ApplyActionsBuilder applyActionsBuilder = new ApplyActionsBuilder().setAction(actions);
        instructionBuilder.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(applyActionsBuilder.build()).build());

        //构建meter与flow联系的instruction
        final InstructionBuilder instructionBuilder1 = new InstructionBuilder();
        final InstructionBuilder applyMeterInstruction = new InstructionBuilder().setOrder(1)
                .setInstruction(new MeterCaseBuilder()
                        .setMeter(new org.opendaylight.yang.gen.v1.urn.opendaylight
                                .flow.types.rev131026.instruction.instruction
                                .meter._case.MeterBuilder()
                                .setMeterId(new MeterId(1L))
                                .build())
                        .build());//缺少一个setInstructionKey

        //创建一个InstructionsBuilder放两个Instruction
        InstructionsBuilder instructionsBuilder = new InstructionsBuilder();
        List<Instruction> instructions = new ArrayList<>();
        instructions.add(instructionBuilder.build());
        instructions.add(instructionBuilder1.build());
        instructionsBuilder.setInstruction(instructions);
        return instructionsBuilder;
        /*final List<Action> actionList = new ArrayList<>();
        ActionBuilder ab = new ActionBuilder();
        OutputActionBuilder output = new OutputActionBuilder();*/

    }
    private Meter createMeter(){
        LOG.info("The meter-table is being created!");
        ArrayList<MeterBandHeader> meterBandHeaders = new ArrayList<>();
        MeterBandHeader meterBandHeader = new MeterBandHeaderBuilder()
                .setMeterBandTypes(new MeterBandTypesBuilder().setFlags(new MeterBandType(true,false,false)).build())
                .setBandBurstSize(7000L)
                .setBandId(new BandId(1L))
                .setBandRate(7000L)
                .setBandType(new DropBuilder().setDropRate(7000L).setDropBurstSize(200L).build())
                .setKey(new MeterBandHeaderKey(new BandId(1L)))
                .build();
        meterBandHeaders.add(meterBandHeader);
        MeterBuilder meterBuilder = new MeterBuilder();
        meterBuilder.setMeterName("MXC")
                .setMeterId(new MeterId(1L))
                .setMeterBandHeaders(new MeterBandHeadersBuilder().setMeterBandHeader(meterBandHeaders).build())
                .setKey(new MeterKey(new MeterId(1L)))
                .setFlags(new MeterFlags(false,false,false,false))
                .setContainerName("MXC")
                .setBarrier(Boolean.TRUE);
        return meterBuilder.build();
    }
}
