package com.example.projecttc.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class ConversorAFNService {
    private ArrayList<Integer> label;
    public Automato conversor(Automato automato){
            EstadosInfo estadosInfo;
            ArrayList<EstadoAFN> lstEstadosProcessados= new ArrayList<EstadoAFN>();
            ArrayList<Transicao> lstTransicoesProcessadas = new ArrayList<Transicao>();

            estadosInfo = getEstadoInicialEFinais(automato);
    
            setarInicial(automato,estadosInfo,lstEstadosProcessados);
    
            carregarEstadosConvertidos(automato,lstEstadosProcessados);
    
            renomearEstados((ArrayList<Estado>)automato.getEstados(),lstEstadosProcessados);
    
            gerarNovosFinais(lstEstadosProcessados,estadosInfo);
    
            carregarTransicoesConvertidas(lstEstadosProcessados, lstTransicoesProcessadas);
    
            transferirTransicoes((ArrayList<Transicao>)automato.getTransicoes(),lstTransicoesProcessadas);

            return new Automato("conversor", new ArrayList<Estado>(lstEstadosProcessados), new ArrayList<Transicao>(lstTransicoesProcessadas));
        }
    
        /**
         * Este método serve pegar o novo estado inicial do AFD, se o inicial do AFN tiver
         * transições vazias para outros estados, vai juntar todos que ele alcançar com
         * transição vazia e fazer um novo estado, senão, ele continua a ser o mesmo inicial
         * do AFN.
         */
        private void setarInicial(Automato automato,EstadosInfo estadosInfo,ArrayList<EstadoAFN> lstEstadosProcessados){
            EstadoAFN novoInicial = new EstadoAFN(estadosInfo.inicial.getId(),estadosInfo.inicial.getNome(),estadosInfo.inicial.isInicial(),estadosInfo.inicial.isFinal(),estadosInfo.inicial.getX(),estadosInfo.inicial.getY());
    
            // Chama o método recursivo para rastrear todos os estados alcançáveis com transições vazias.
            pegarNovoInicial(novoInicial, estadosInfo.inicial);
    
            // Antes de colocar no ArrayList de estados já processados, modificamos o atributo inicial,
            // pois o inicial do AFN continua a ser inicial no AFD.
            novoInicial.setInicial(true);
    
            lstEstadosProcessados.add(novoInicial);
    
            pegarTransicoesDasLabels(automato,novoInicial);
        }
    
        /**
         * Este método serve como auxílio ao método "estadosAlcancaveisComSimbolos()",
         * pois ele percorre os estados já processados e, para cada estado,
         * verifica quais símbolos do alfabeto podem levar a novos estados
         * a partir do estado atual.
         *
         * A cada iteração, o método chama "estadosAlcancaveisComSimbolos()" para determinar
         * os estados que podem ser alcançados a partir de um estado dado e um símbolo específico.
         *
         * O método possui um controle para evitar o processamento do símbolo vazio (""),
         * permitindo que o símbolo vazio seja ignorado na primeira iteração,
         * mas considerado nas subsequentes.
         */
        private void carregarEstadosConvertidos(Automato automato,ArrayList<EstadoAFN> lstEstadosProcessados){
            boolean primeiraVez = true;
    
            for (int i = 0; i < lstEstadosProcessados.size(); i++){
                EstadoAFN estadoAFN = lstEstadosProcessados.get(i);
    
                for (String simbolo : automato.getAlfabeto()) {
                    if (primeiraVez && simbolo.equals("")) {
                        continue; // pula o símbolo vazio na primeira vez.
                    }
    
                    estadosAlcancaveisComSimbolos(automato,estadoAFN, simbolo,lstEstadosProcessados);
    
                }
    
                primeiraVez = false;
            }
        }
    
        /**
         * Este método percorre a lista de estados processados e, para cada estado,
         * obtém o alfabeto associado. Em seguida, processa cada símbolo,exceto o
         * símbolo vazio, e chama o método "simbolosAlcancaveisComNovosEstados()"
         * para identificar as transições possíveis a partir desse estado e símbolo.
         */
        private void carregarTransicoesConvertidas(ArrayList<EstadoAFN> lstEstadosProcessados, ArrayList<Transicao> lstTransicoesProcessadas){
            for (EstadoAFN estadoAFN: lstEstadosProcessados){
                ArrayList<String> simbolos = pegarAlfabetoDoEstado(estadoAFN);
                for (String simbolo : simbolos) {
    
                    if (!simbolo.equals("")){
                        simbolosAlcancaveisComNovosEstados(estadoAFN, simbolo,lstEstadosProcessados,lstTransicoesProcessadas);
                    }
    
                }
    
            }
        }
    
        /**
         * Este método serve para renomear os estados e colocar em ordem os ids
         * já processados do novo AFD. Assim como também colocar os objetos
         * do ArrayList de estados já processados para o ArrayList de estados
         * finais.
         *
         * @param lstEstadosFinal Para transferir os objetos do ArrayList de
         *                        estados já processados para o ArrayList
         *                        de estados finais que serão gravados
         *                        no arquivo final.
         */
        private void renomearEstados(ArrayList<Estado> lstEstadosFinal,ArrayList<EstadoAFN> lstEstadosProcessados){
    
            for(int x = 0; x < lstEstadosProcessados.size(); x++) {
    
                lstEstadosProcessados.get(x).setId(x);
                lstEstadosProcessados.get(x).setNome("q" + lstEstadosProcessados.get(x).getId());
    
                lstEstadosFinal.add(lstEstadosProcessados.get(x));
    
            }
    
        }
    
        /**
         * Este método basicamente transfere todas as transições da lista processada para a lista final.
         */
        private void transferirTransicoes(ArrayList<Transicao> lstTransicoesFinal,ArrayList<Transicao> lstTransicoesProcessadas){
            for (Transicao transicao: lstTransicoesProcessadas){
                lstTransicoesFinal.add(transicao);
            }
        }
    
        /**
         * Este método retorna o alfabeto de um estado AFN, extraindo os símbolos das transições
         * e evitando duplicatas.
         *
         * @param estadoAFN Estado que deseja saber o alfabeto.
         */
        private ArrayList<String> pegarAlfabetoDoEstado(EstadoAFN estadoAFN){
            ArrayList<String> alfabeto = new ArrayList<String>();
    
            for (Transicao transicao: estadoAFN.getTransicoes()){
                if(!verificarDuplicata(alfabeto,transicao.getSimbolo())){
                    alfabeto.add(transicao.getSimbolo());
                }
            }
    
            return alfabeto;
        }
    
        /**
         * Basicamente verifica se já existe algum simbolo igual no ArrayList.
         *
         * @param novaLista ArrayList que deseja verificar o simbolo dentro dele.
         * @param simbolo Simbolo que deseja verificar no ArrayList.
         * @return Se existir retorna verdadeiro, senão, retorna false.
         */
        private boolean verificarDuplicata(ArrayList<String> novaLista, String simbolo) {
            for (String s : novaLista) {
                if (s.equals(simbolo)) {
                    return true;
                }
            }
            return false;
        }
    
        /**
         * Este Método que serve para encontrar o estado inicial e os estados finais do autômato.
         */
        private EstadosInfo getEstadoInicialEFinais(Automato automato) {
            EstadosInfo estadosInfo = new EstadosInfo();
            // Para saber se existe um inicial entre os estados.
            for (Estado e: automato.getEstados()){
                if (e.isInicial()) {
                    estadosInfo.inicial= e;
                }
    
                if(e.isFinal()){
                    estadosInfo.finais.add(e);
                }
    
            }
            return estadosInfo;
        }
    
        /**
         * Este método serve para pegar o novo estado incial do AFD. Se o estado inicial do AFN
         * tiver transições vazias, ele verifica para quais estados essas transições estão
         * indo, formando assim um novo estado com todos os estados alcançados. Se não tiver
         * transições vazias, então o novo estado inicial é o mesmo do AFN.
         *
         * Este método também está servindo para saber se ao chegar em um estado, ele tem transições
         * vazias para outros estados, se tiver, junta todos os estados que ele alcança com vazio em
         * um só estado.
         *
         * @param estadoAtual Estado que queremos formar a partir dos antigos.
         * @param estadoAnalisar Estado que queremos analisar as transições.
         */
        private void pegarNovoInicial(EstadoAFN estadoAtual, Estado estadoAnalisar) {
    
            // Verifica se o estado já foi visitado
            if (estadoAtual.jaVisitado(estadoAnalisar)) {
                return;
            }
    
            estadoAtual.addLabel(estadoAnalisar);
    
            // Percorre todas as transições do estado atual
            for (Transicao t : estadoAnalisar.getTransicoes()) {
    
                // Verifica se a transição é uma transição vazia.
                if (t.getSimbolo().equalsIgnoreCase("")) {
                    // Chamada recursiva para o estado destino
                    pegarNovoInicial(estadoAtual, t.getDestino());
                }
            }
    
        }

        public void setLabel(int id) {
            // Adiciona o id ao label apenas se ele ainda não estiver presente para evitar duplicações.
            if (!label.contains(id)) {
                label.add(id);
            }
        }
    
        // Basicamente esse método só vai checar se existe já algum id igual no ArrayList.
        public boolean jaVisitado(int id) {
            return label.contains(id);
        }
    
        /**
         * Este método adiciona ao estado AFN todas as transições dos estados cujos IDs
         * correspondem aos labels que estão no estado AFN.
         *
         * @param estadoAFN Estado que queremos pegar as transições a partir dos labels dele.
         */
        private void pegarTransicoesDasLabels(Automato automato,EstadoAFN estadoAFN){
    
            for (Estado label: estadoAFN.getLabels()){
                // Peguei a lista de labels.
                for (Estado estado: automato.getEstados()){
                    if (estado.getId() == label.getId()){
                        // Achamos o estado correspondente ao label.
                        for (Transicao transicao: estado.getTransicoes()){
                            // Pegamos todas essas transições deste estado e colocamos no estadoAFN.
                            estadoAFN.getTransicoes().add(transicao);
                        }
                    }
                }
            }
    
        }
    
    
        int idEstadoAtual = 1;
    
        /**
         * Basicamente só faz um objeto do tipo EstadoAFN.
         * @return Retorna o EstadoAFN criado.
         */
        private EstadoAFN criarNovoEstadoAFN() {
            // Cria um novo estado AFN com os parâmetros fornecidos
            return new EstadoAFN(idEstadoAtual++, "q" + idEstadoAtual, false, false, 300, 300);
        }
        
    
        /**
         * Este método encontra os estados alcançáveis a partir de um estado atual usando o símbolo fornecido.
         * Cria um novo EstadoAFN para armazenar os estados alcançados e processa recursivamente
         * as transições com base no símbolo(Se o estado fornecido tiver transições vazias).
         * Se o novo estado ainda não foi processado, adiciona-o à lista de estados processados.
         *
         * @param estadoAtual Estado que queremos saber para onde vai com as suas transições.
         * @param simbolo Simbolo da transição que queremos saber para que estado vai.
         */
        private void estadosAlcancaveisComSimbolos(Automato automato,EstadoAFN estadoAtual, String simbolo,ArrayList<EstadoAFN>lstEstadosProcessados) {
            // Criando um novo EstadoAFN para armazenar os estados alcançados.
            EstadoAFN novoEstadoAFN = criarNovoEstadoAFN();
    
            // Iterando sobre as transições do estado atual
            for (Transicao t : estadoAtual.getTransicoes()) {
                // Verifica se a transição corresponde ao símbolo fornecido e não é vazia.
                if (!t.getSimbolo().isEmpty() && t.getSimbolo().equals(simbolo)) {
    
                    if (!novoEstadoAFN.jaVisitado(t.getDestino())) {
                       
                        // Chama o método para processar transições vazias recursivamente.
                        pegarNovoInicial(novoEstadoAFN, t.getDestino());
    
                        // Adiciona o ID ao novo EstadoAFN.
                        novoEstadoAFN.addLabel(t.getDestino());
    
                    }
                }
            }
    
    
            if (!verificarSeJaExisteEstado(novoEstadoAFN,lstEstadosProcessados)) {
                // Após processar todas as transições, recupera as transições dos estados pelos IDs
                pegarTransicoesDasLabels(automato,novoEstadoAFN);
    
                lstEstadosProcessados.add(novoEstadoAFN);
    
            }
        }
    
        /**
         * Esté método é quase parecido com o anterior, o que muda é que vamos olhar as transições
         * dos novos Estados do AFD e descobrir para onde elas estão indo. Por exemplo: q0 com
         * "a" vai para q1,q2,q3, se esse estado existir nos novos Estados já gerados, então fazemos
         * a transição para esse estado com labels 1,2,3.
         *
         * @param estadoAtual Estado que queremos saber para onde vai com determinado símbolo.
         * @param simbolo Simbolo do alfabeto deste estado.
         */
        private void simbolosAlcancaveisComNovosEstados(EstadoAFN estadoAtual, String simbolo,ArrayList<EstadoAFN>lstEstadosProcessados,ArrayList<Transicao>lstTransicoesProcessadas){
            // Criando um novo EstadoAFN para armazenar os estados alcançados
            EstadoAFN novoEstadoAFN = criarNovoEstadoAFN();
    
            // Iterando sobre as transições do estado atual
            for (Transicao t : estadoAtual.getTransicoes()) {
                // Verifica se a transição corresponde ao símbolo fornecido e não é vazia
                if (!t.getSimbolo().isEmpty() && t.getSimbolo().equals(simbolo)) {
    
                    if (!novoEstadoAFN.jaVisitado(t.getDestino())) {
                        // Chama o método para processar transições vazias recursivamente
                        pegarNovoInicial(novoEstadoAFN, t.getDestino());
    
                        // Adiciona o ID ao novo EstadoAFN
                        novoEstadoAFN.addLabel(t.getDestino());
    
                    }
                }
            }
    
            // Se existir o estado então encontramos a transição correta com determinado símbolo.
            if (verificarSeJaExisteEstado(novoEstadoAFN,lstEstadosProcessados)) {
    
                Transicao novaTransicao = new Transicao(estadoAtual, ProcurarEstado(novoEstadoAFN, lstEstadosProcessados),simbolo);
                lstTransicoesProcessadas.add(novaTransicao);
    
            }
        }
    
        /**
         * Este método serve para verificar se já existe algum estado igual ao que
         * precisa gerar. Para não existir duplicatas desnecessarias.
         *
         * @param estadoAnalisar O estado que eu quero ver se já existe.
         *
         * @return Se existir algum estado igual retorna verdadeiro, senão retorna
         * falso.
         */
        private boolean verificarSeJaExisteEstado(EstadoAFN estadoAnalisar, ArrayList<EstadoAFN> lstEstadosProcessados) {

            // Ordena a lista de labels do estado a ser analisado
            List<Estado> labelAnalisarOrdenado = new ArrayList<>(estadoAnalisar.getLabels());
            labelAnalisarOrdenado.sort(Comparator.comparingInt(Estado::getId)); // Ordena pelo id do estado
        
            // Percorre a lista de estados já processados
            for (EstadoAFN estadoAFN : lstEstadosProcessados) {
                // Ordena a lista de labels do estado já processado
                List<Estado> labelProcessadoOrdenado = new ArrayList<>(estadoAFN.getLabels());
                labelProcessadoOrdenado.sort(Comparator.comparingInt(Estado::getId)); // Ordena pelo id do estado
        
                // Compara as listas de labels ordenadas
                if (labelProcessadoOrdenado.equals(labelAnalisarOrdenado) || estadoAnalisar.getLabels().isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Este método serve para procurar o estado que tem as labels correspondentes
         * que estão no "estadoAnalisar".
         *
         * @param estadoAnalisar Estado que queremos analisar as labels dentro dele.
         * @return retorna o estado encontrado que bate com as labels.
         */
        private EstadoAFN ProcurarEstado(EstadoAFN estadoAnalisar, ArrayList<EstadoAFN> lstEstadosProcessados) {
        
            // Ordena a lista de labels do estado a ser analisado
            List<Estado> labelAnalisarOrdenado = new ArrayList<>(estadoAnalisar.getLabels());
            labelAnalisarOrdenado.sort(Comparator.comparingInt(Estado::getId)); // Ordena pelo id do estado
        
            // Percorre a lista de estados já processados
            for (EstadoAFN estadoAFN : lstEstadosProcessados) {
                // Ordena a lista de labels do estado já processado
                List<Estado> labelProcessadoOrdenado = new ArrayList<>(estadoAFN.getLabels());
                labelProcessadoOrdenado.sort(Comparator.comparingInt(Estado::getId)); // Ordena pelo id do estado
        
                // Compara as listas de labels ordenadas
                if (labelProcessadoOrdenado.equals(labelAnalisarOrdenado) || estadoAnalisar.getLabels().isEmpty()) {
                    return estadoAFN;
                }
            }
            return null;
        }
        
    
        /**
         * Este método serve para gerar os novos finais do AFD convertido a partir
         * dos antigos finais do AFN passado para converter.
         */
        public void gerarNovosFinais(ArrayList<EstadoAFN>lstEstadosProcessados,EstadosInfo estadosInfo) {
            for (EstadoAFN estadoAFN : lstEstadosProcessados) {
                for (Estado estado : estadosInfo.finais) {
                    // Verifica se o estado final original foi visitado por este estado AFN
                    if (estadoAFN.jaVisitado(estado)) {
                        // Marca o estado AFN como final se ele contém o id de um estado final original
                        estadoAFN.setFinal(true);
                    }
                }
            }
        }

        public class EstadosInfo {
            private Estado inicial;
            private ArrayList<Estado> finais = new ArrayList<>();
        }

        public class EstadoAFN extends Estado {
            private ArrayList<Estado> labels;  // Lista de labels (estados) associados ao EstadoAFN
    
            // Construtor da classe EstadoAFN
            public EstadoAFN(int id, String nome, boolean inicial, boolean isFinal, double x, double y) {
                // Chama o construtor da classe pai (Estado) para inicializar os atributos herdados
                super(id, nome, inicial, isFinal, x, y);
                this.labels = new ArrayList<>();  // Inicializa a lista de labels como uma ArrayList vazia
            }
    
            // Método para adicionar um estado à lista de labels
            public void addLabel(Estado novoLabel) {
                if (!labels.contains(novoLabel)) {
                    labels.add(novoLabel);
                }
            }
    
            // Método para obter a lista de labels
            public ArrayList<Estado> getLabels() {
                return labels;
            }
    
            // Método para verificar se o estado já foi visitado
            public boolean jaVisitado(Estado estado) {
                return labels.contains(estado);
            }
        }
    }
