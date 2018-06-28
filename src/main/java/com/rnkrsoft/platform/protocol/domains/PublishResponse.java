package com.rnkrsoft.platform.protocol.domains;

import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by woate on 2018/6/27.
 */
public class PublishResponse extends AbstractResponse{
    @ApidocElement("接口列表")
    final List<InterfaceDefinition> interfaces = new ArrayList();

    public List<InterfaceDefinition> getInterfaces() {
        return interfaces;
    }
}
