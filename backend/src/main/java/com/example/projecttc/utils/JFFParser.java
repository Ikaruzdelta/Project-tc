package com.example.projecttc.utils;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

public class JFFParser {

    public static Automato parse(File file) throws Exception {
        Automato automato = new Automato("Autômato Lido");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);

        doc.getDocumentElement().normalize();

        NodeList stateList = doc.getElementsByTagName("state");
        for (int i = 0; i < stateList.getLength(); i++) {
            Node stateNode = stateList.item(i);
            if (stateNode.getNodeType() == Node.ELEMENT_NODE) {
                Element stateElement = (Element) stateNode;

                String id = stateElement.getAttribute("id");
                String name = stateElement.getAttribute("name");

                // Leitura dos valores de X e Y, tratando possíveis valores nulos
                String x = stateElement.getElementsByTagName("x").item(0).getTextContent();
                String y = stateElement.getElementsByTagName("y").item(0).getTextContent();
                double xValue = x != null && !x.isEmpty() ? Double.parseDouble(x) : 0;
                double yValue = y != null && !y.isEmpty() ? Double.parseDouble(y) : 0;

                boolean isInitial = stateElement.getElementsByTagName("initial").getLength() > 0;
                boolean isFinal = stateElement.getElementsByTagName("final").getLength() > 0;

                // Criação do estado com os valores de X e Y
                Estado estado = new Estado(Integer.parseInt(id), name, isInitial, isFinal, xValue, yValue);
                automato.addEstado(estado);
            }
        }

        NodeList transitionList = doc.getElementsByTagName("transition");
        for (int i = 0; i < transitionList.getLength(); i++) {
            Node transitionNode = transitionList.item(i);
            if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element transitionElement = (Element) transitionNode;

                String from = transitionElement.getElementsByTagName("from").item(0).getTextContent();
                String to = transitionElement.getElementsByTagName("to").item(0).getTextContent();
                String symbol = transitionElement.getElementsByTagName("read").item(0).getTextContent();

                Estado origem = automato.getEstadoPorId(Integer.parseInt(from));
                Estado destino = automato.getEstadoPorId(Integer.parseInt(to));

                if (origem != null && destino != null) {
                    Transicao transicao = new Transicao(origem, destino, symbol);
                    automato.addTransicao(transicao);
                }
            }
        }

        return automato;
    }
}
