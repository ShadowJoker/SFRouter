package com.sfexpress.sfrouter.demo.testservice;

import com.sfexpress.sfrouter.template.IProvider;

public interface HelloService extends IProvider {
    void sayHello(String name);
}
