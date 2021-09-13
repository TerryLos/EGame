package runner;

import java.util.concurrent.*;
import env.*;

public class Runner{
	public static void main(String[] args){
		ExecutorService exe = Executors.newFixedThreadPool(1);
		Environnment env = new Environnment();
		exe.execute(env);
	}
}
