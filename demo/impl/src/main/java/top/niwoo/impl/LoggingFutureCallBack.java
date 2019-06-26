/*
 * Copyright © 2017 Joliu and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package top.niwoo.impl;

import com.google.common.util.concurrent.FutureCallback;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.slf4j.Logger;


public class LoggingFutureCallBack<V> implements FutureCallback<V> {
    private final Logger LOG;
    private String message;
    public LoggingFutureCallBack(String message , Logger LOG){
        this.LOG = LOG;
        this.message = message;
    }


    @Override
    public void onSuccess(@NullableDecl Object o) {
    LOG.info("initial result []",o);
    }

    @Override
    public void onFailure(Throwable throwable) {
    LOG.warn("initial error []",throwable);
    }
}
