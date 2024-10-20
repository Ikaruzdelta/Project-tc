package com.example.projecttc.utils;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

public class GravarXML {

    /**
     * Este método grava os dados de um autômato (estados e transições) em um arquivo XML no formato JFLAP (.jff).
     *
     * @param estados         Lista de estados do autômato.
     * @param transicoes      Lista de transições do autômato.
     * @param outputFilePath  Caminho do arquivo onde o autômato será salvo.
     */
    public void gravarAutomato(ArrayList<Estado> estados, ArrayList<Transicao> transicoes, String outputFilePath) {
        try {
            // Criar um novo documento XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Criar o elemento raiz "structure"
            Element rootElement = doc.createElement("structure");
            doc.appendChild(rootElement);

            Element typeElement = doc.createElement("type");
            typeElement.appendChild(doc.createTextNode("fa"));  
            rootElement.appendChild(typeElement);

            Element automatonElement = doc.createElement("automaton");
            rootElement.appendChild(automatonElement);

            for (Estado estado : estados) {
                Element stateElement = doc.createElement("state");
                stateElement.setAttribute("id", String.valueOf(estado.getId()));
                stateElement.setAttribute("name", estado.getNome());

                Element xElement = doc.createElement("x");
                xElement.appendChild(doc.createTextNode(String.valueOf(estado.getX())));
                stateElement.appendChild(xElement);

                Element yElement = doc.createElement("y");
                yElement.appendChild(doc.createTextNode(String.valueOf(estado.getY())));
                stateElement.appendChild(yElement);

                if (estado.isInicial()) {
                    Element initialElement = doc.createElement("initial");
                    stateElement.appendChild(initialElement);
                }

                if (estado.isFinal()) {
                    Element finalElement = doc.createElement("final");
                    stateElement.appendChild(finalElement);
                }

                automatonElement.appendChild(stateElement);
            }

            // Adicionar as transições
            for (Transicao transicao : transicoes) {
                Element transitionElement = doc.createElement("transition");

                Element fromElement = doc.createElement("from");
                fromElement.appendChild(doc.createTextNode(String.valueOf(transicao.getOrigem().getId())));
                transitionElement.appendChild(fromElement);

                Element toElement = doc.createElement("to");
                toElement.appendChild(doc.createTextNode(String.valueOf(transicao.getDestino().getId())));
                transitionElement.appendChild(toElement);

                Element readElement = doc.createElement("read");
                readElement.appendChild(doc.createTextNode(transicao.getSimbolo()));
                transitionElement.appendChild(readElement);

                automatonElement.appendChild(transitionElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputFilePath));

            transformer.transform(source, result);

            System.out.println("Autômato salvo com sucesso no arquivo: " + outputFilePath);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
