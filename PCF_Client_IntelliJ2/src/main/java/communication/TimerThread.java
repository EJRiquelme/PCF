package communication;

public class TimerThread extends Thread {

    private ConnectionToServer comunication;
    private SharedStore sharedStore;

    public TimerThread(ConnectionToServer comunication) {
        this.comunication = comunication;
        this.sharedStore = SharedStore.getInstance();
    }

    public void chronometer() {
        try {
            sleep(3000);
            if (!sharedStore.isAnswered() && sharedStore.getTimers().size() == 1) { // Si en "3 segundos" el Servidor no ha respondido se notifica del problema al Humano mediante el hilo Gráfico
                sharedStore.waitUntilResponse(false);
            }
            sharedStore.getTimers().remove(this);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        chronometer();
    }
}
