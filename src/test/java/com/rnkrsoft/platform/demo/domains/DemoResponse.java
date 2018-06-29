package com.rnkrsoft.platform.demo.domains;

import lombok.Data;
import lombok.ToString;

import javax.web.doc.AbstractResponse;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
@Data
@ToString(callSuper = true)
public class DemoResponse extends AbstractResponse{
    Integer age;
}
