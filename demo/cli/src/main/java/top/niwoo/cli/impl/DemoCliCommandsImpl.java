/*
 * Copyright © 2017 Joliu and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.niwoo.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.niwoo.cli.api.DemoCliCommands;


public class DemoCliCommandsImpl implements DemoCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(DemoCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public DemoCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("DemoCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}
