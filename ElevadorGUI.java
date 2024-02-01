import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Elevador {
    private int andarAtual;

    public Elevador() {
        this.andarAtual = 0;
    }

    public Elevador(int andarInicial) {
        this.andarAtual = andarInicial;
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public void moverPara(int novoAndar) {
        this.andarAtual = novoAndar;
    }
}

public class ElevadorGUI extends JFrame {
    private Elevador elevador1, elevador2;
    private JButton[] botoes;
    private JLabel[] lblAndarAtual;

    public ElevadorGUI() {
        elevador1 = new Elevador();
        elevador2 = new Elevador(1); // Elevador 2 começa no andar 1

        // Configurações da janela
        setTitle("Elevadores");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuração do layout com GridLayout
        setLayout(new GridLayout(1, 2));

        // Painel para os elevadores à esquerda
        JPanel elevadoresPanel = new JPanel(new GridLayout(2, 1));
        lblAndarAtual = new JLabel[2];

        for (int i = 0; i < 2; i++) {
            lblAndarAtual[i] = new JLabel("Elevador " + (i + 1) + ": " + (i == 0 ? elevador1.getAndarAtual() : elevador2.getAndarAtual()));
            elevadoresPanel.add(lblAndarAtual[i]);
            lblAndarAtual[i].setFont(new Font("Arial", Font.BOLD, 34));
        }
        add(elevadoresPanel);

        // Painel para os botões à direita
        JPanel botoesPanel = new JPanel(new GridLayout(9, 1));
        botoes = new JButton[9];

        for (int i = 8; i >= 0; i--) {
            final int andar = i - 2;
            botoes[i] = new JButton(String.valueOf(andar));
            botoes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chamarElevador(andar);
                }
            });
            botoesPanel.add(botoes[i]);
        }
        add(botoesPanel);

        // Definindo a cor de fundo inicial dos botões
        atualizarCoresBotoes();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Exibindo a janela
        setVisible(true);
    }

    
    boolean ocupado1 = false;
    boolean ocupado2 = false;
    
    private void chamarElevador(int andarChamado) {
        int distanciaElevador1 = Math.abs(andarChamado - elevador1.getAndarAtual());
        int distanciaElevador2 = Math.abs(andarChamado - elevador2.getAndarAtual());

        Elevador elevadorSelecionado;
        int indiceElevador;

        if (distanciaElevador1 <= distanciaElevador2) {
            elevadorSelecionado = elevador1;
            indiceElevador = 0;
            ocupado1 = true;
        } else {
            elevadorSelecionado = elevador2;
            indiceElevador = 1;
            ocupado2 = true;
        }

        // Iniciar animação
        animarElevador(elevadorSelecionado, indiceElevador, andarChamado);

        // Atualizando as cores dos botões após o movimento do elevador
        atualizarCoresBotoes();
    }

    private void animarElevador(Elevador elevador, int indice, int andarDestino) {
        Timer timer = new Timer(1000, new ActionListener() {
            int andarAtual = elevador.getAndarAtual();
            int direcao = Integer.compare(andarDestino, andarAtual);
    
            @Override
            public void actionPerformed(ActionEvent e) {
                andarAtual += direcao;
    
                // Apaga a luz do andar anterior
                if (andarAtual - direcao >= -2 && andarAtual - direcao <= 6) {
                    botoes[andarAtual - direcao + 2].setBackground(UIManager.getColor("Button.background"));
                }
    
                // Atualiza o rótulo do andar atual
                lblAndarAtual[indice].setText("Elevador " + (indice + 1) + ": " + andarAtual);
    
                if (andarAtual == andarDestino) {
                    ((Timer) e.getSource()).stop();
                    elevador.moverPara(andarAtual);
                    atualizarAndarAtual(elevador, indice);
    
                    // Acende a luz verde no andar de destino, mas verifica se o outro elevador também está no mesmo andar
                    if (elevador == elevador1 && elevador2.getAndarAtual() != andarDestino) {
                        botoes[andarAtual + 2].setBackground(Color.GREEN);
                    } else if (elevador == elevador2 && elevador1.getAndarAtual() != andarDestino) {
                        botoes[andarAtual + 2].setBackground(Color.GREEN);
                    }
    
                    // Verifica se existe um novo destino
                    String[] andares = {"-2", "-1", "0", "1", "2", "3", "4", "5", "6"};
                    String novoAndarDestinoStr = (String) JOptionPane.showInputDialog(
                            null,
                            "Elevador " + (indice + 1) + " chegou ao destino.\nEscolha um novo andar:",
                            "Novo Destino",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            andares,
                            andares[2] // andar 0 é o padrão
                    );
    
                    if (novoAndarDestinoStr != null) {
                        int novoAndarDestino = Integer.parseInt(novoAndarDestinoStr);
    
                        // Iniciar animação para o novo destino
                        animarElevador(elevador, indice, novoAndarDestino);
                    }
    
                    // Atualizando as cores dos botões após o movimento do elevador
                    atualizarCoresBotoes();
                } else {
                    // Acende a luz amarela no andar atual
                    botoes[andarAtual + 2].setBackground(Color.YELLOW);
                }
            }
        });
    
        timer.start();
    }
    
    private void atualizarAndarAtual(Elevador elevador, int indice) {
        lblAndarAtual[indice].setText("Elevador " + (indice + 1) + ": " + elevador.getAndarAtual());
    }

    private void atualizarCoresBotoes() {
        for (int i = 0; i < 9; i++) {
            int andarBotao = i - 2;
            for (int j = 0; j < 2; j++) {
                if (andarBotao == elevador1.getAndarAtual() || andarBotao == elevador2.getAndarAtual()) {
                    // Se pelo menos um elevador estiver no mesmo andar, define a cor verde
                    botoes[i].setBackground(Color.GREEN);
                } else {
                    // Se não, mantém a cor padrão
                    botoes[i].setBackground(UIManager.getColor("Button.background"));
                }
            }
        }
    }
}
