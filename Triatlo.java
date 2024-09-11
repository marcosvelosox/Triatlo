package triatlo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Triatlo {
    private static final int NUMERO_ATLETAS = 25;
    private static final int PONTUACAO_BASE = 250;
    private static final int NUMERO_ARMAS = 5;
    private Semaphore semaforoArmas = new Semaphore(NUMERO_ARMAS, true);
    private List<Atleta> atletas = new ArrayList<>();

    public void iniciar(View view) throws InterruptedException {
        
        for (int i = 0; i < NUMERO_ATLETAS; i++) {
            String nome = "Atleta " + (i + 1);
            atletas.add(new Atleta(nome, view));
        }

        
        for (Atleta atleta : atletas) {
            atleta.start();
        }

        // Aguarda os atletas terminarem a corrida e gerencia as armas
        List<Atleta> filaTiros = new ArrayList<>();
        for (Atleta atleta : atletas) {
            atleta.join(); // Espera cada atleta terminar a corrida
            filaTiros.add(atleta);
        }

        
        for (Atleta atleta : filaTiros) {
            semaforoArmas.acquire(); // Verifica se há arma disponível
            synchronized (atleta) {
                atleta.notify(); // Libera o atleta para atirar
            }
            atleta.join(); // Aguarda o atleta terminar os tiros e ciclismo
            semaforoArmas.release(); // Libera a arma para outro atleta
        }

        
        calcularPontuacao(view);
    }

    private void calcularPontuacao(View view) {
        atletas.sort(Comparator.comparingInt(Atleta::getPontuacao).reversed());

        int pontuacao = PONTUACAO_BASE;
        for (int i = 0; i < atletas.size(); i++) {
            Atleta atleta = atletas.get(i);
            atleta.setPontuacao(pontuacao);
            pontuacao = Math.max(10, pontuacao - 10); // Reduz 10 pontos para cada posição
        }

        // Exibe o resultado final
        view.exibirResultadoFinal(atletas);
    }
}
