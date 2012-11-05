package test.proxy;

import java.io.Serializable;

public interface  HelloWorld<T extends Serializable,Pk> {
	void sayHelloWorld(T t) ;
}
