import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PuenteMonitor {
	
    private Lock bloquear = new ReentrantLock();
    private Condition nortePuede = bloquear.newCondition();
    private Condition surPuede = bloquear.newCondition();
    
    public boolean norteCruzandoAhora = false;
    public boolean surCruzandoAhora = false;

    public void surQuiereCruzar() throws InterruptedException { // 
        bloquear.lock();
        try {
            while (norteCruzandoAhora) {
                surPuede.await();
            }
            surCruzandoAhora = true;
        } finally {
            bloquear.unlock();
        }
    }

    public void surTerminaDeCruzar() {
        bloquear.lock();
        try {
        	surCruzandoAhora = false;
            nortePuede.signalAll();
        } finally {
            bloquear.unlock();
        }
    }

    public void norteQuiereCruzar() throws InterruptedException {
        bloquear.lock();
        try {
            while (surCruzandoAhora) {
                nortePuede.await();
            }
            norteCruzandoAhora = true;
        } finally {
            bloquear.unlock();
        }
    }

    public void norteTerminaDeCruzar() {
        bloquear.lock();
        try {
        	norteCruzandoAhora = false;
            surPuede.signalAll();
        } finally {
            bloquear.unlock();
        }
    }

    public static void main(String[] args) { //Creamos dos hilos, uno para los coches del norte y otro para los del sur.
    	
        PuenteMonitor puente = new PuenteMonitor();
        boolean surCruzandoAhora = false; 
        boolean norteCruzandoAhora = false; //variables booleanas para condicionar la entrada al proceso del coche norte o sur 


        Thread norteThread = new Thread(() -> { //hilo para los coches del norte
        	while (!surCruzandoAhora) {
	            try {
	                puente.norteQuiereCruzar();
	                System.out.println("Un coche en el norte empieza a cruzar el puente.");
	                Thread.sleep(2000); //Tiempo de simulación de lo que tarda en cruzar un choche.
	                puente.norteTerminaDeCruzar();//Indicamos que el coche ha terminado de cruzar el puente.
	                System.out.println("El coche del norte ha terminado de cruzar el puente, pueden pasar otros coches.");
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
        	}
        });
        
        Thread surThread = new Thread(() -> { //hilo para los coches del sur
            while (!norteCruzandoAhora) {
	        	try {
	                puente.surQuiereCruzar();
	                System.out.println("Un coche en el sur empieza a cruzar el puente.");
	                Thread.sleep(3000);//Tiempo de simulación de lo que tarda en cruzar un choche.
	                puente.surTerminaDeCruzar();//Indicamos que el coche ha terminado de cruzar el puente.
	                System.out.println("El coche del sur ha terminado de cruzar el puente, pueden pasar otros coches.");
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
            }
        });

        surThread.start(); // iniciamos los hilos tanto del sur como del norte
        norteThread.start();
    }
}

