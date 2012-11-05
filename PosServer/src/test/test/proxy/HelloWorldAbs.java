package test.proxy;

import java.io.Serializable;

public abstract class HelloWorldAbs<T extends Serializable,Pk> implements HelloWorld<T,Pk>{
	@SuppressWarnings("unchecked")
    public void sayHelloWorld(T t) {
        System.out.println(t);             
    }
}
